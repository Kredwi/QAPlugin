package ru.kredwi.qa.callback;

import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_COLORS;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_ENABLE;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_FADES;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_FLICKER;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_TRAIL;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_TYPE;
import static ru.kredwi.qa.config.ConfigKeys.IMMEDIATELY_END_GAME;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class ConstructionStageEndCallback implements Consumer<Void> {

	public static final int FIREWORK_MODEL_ID = 909827261;
	
	private FireworkEffect fireworkEffect;;
	
	private PluginWrapper plugin;
	private IMainGame mainGame;
	private IGame game;
	private Player player;
	
	public ConstructionStageEndCallback(PluginWrapper plugin, IMainGame mainGame, IGame game, Player player) {
		this.plugin = plugin;
		this.mainGame = mainGame;
		this.game = game;
		this.player = player;
		
		this.fireworkEffect = FireworkEffect.builder()
				.with(plugin.getConfigManager()
						.getAsFireworkType(FIREWORK_FOR_WINNER_TYPE))
				.withColor(plugin.getConfigManager()
						.getAsBukkitColorList(FIREWORK_FOR_WINNER_COLORS))
				.withFade(plugin.getConfigManager()
						.getAsBukkitColorList(FIREWORK_FOR_WINNER_FADES))
				.flicker(plugin.getConfigManager()
						.getAsBoolean(FIREWORK_FOR_WINNER_FLICKER))
				.trail(plugin.getConfigManager()
						.getAsBoolean(FIREWORK_FOR_WINNER_TRAIL))
				.build();
	}
	
	@Override
	public void accept(Void o) {
		// get path owner
		PlayerState state = game.getPlayerService().getPlayerState(player);
		// reset all dynamic states
		state.resetState();
		
		// player build complete
		game.getBlockConstruction().addBuildComplete();
		
		// checks is winner?
		if (game.getWinnerService().isPlayerWin(state)) {
			// add winner to list winners
			game.getWinnerService().addWinner(player);
		}
		
		// if last player complete build
		if (game.getBlockConstruction().getBuildComplete() > game.getPlayerService().getPlayers().size()) {
			
			// reset build completes
			game.getBlockConstruction().resetBuildComplete();
			
			// if have winner
			if (!game.getWinnerService().getWinners().isEmpty()) {
				
				this.executeWinnerHandler();
				
			} else {
				if (game.isPreStopGame()) {
					Optional<Integer> maxBuildedBlocks = game.getPlayerService().getPlayerAndStatesArray().stream()
							.map(e -> e.getValue().getBuildedBlocks())
							.max(Integer::compare);
						
					for (Map.Entry<Player, PlayerState> s : game.getPlayerService().getPlayerAndStatesArray()) {
						if (maxBuildedBlocks.isPresent() && s.getValue().getBuildedBlocks() >= maxBuildedBlocks.get()) {
							game.getWinnerService().addWinner(s.getKey());
						}
					}
					
					this.executeWinnerHandler();
					return;
				} else game.getQuestionManager().questionAllPlayers();
			}
		}
	}
	
	private void executeWinnerHandler() {
		if (game.getWinnerService().getWinners().isEmpty()) {
			throw new NegativeArraySizeException("Winners is EMPTY !");
		}
		
		// spawn for winners firework effects
		if (plugin.getConfigManager().getAsBoolean(FIREWORK_FOR_WINNER_ENABLE)) {
			game.getWinnerService().getWinners().forEach(p -> spawnFireworkEntity(p.getLocation()));		
		}
		
		// and alert all players in the game of winners
		game.getWinnerService().alertOfPlayersWin();
		
		game.getPlayerService().spawnPlayers();
		
		game.setFinished(true);
		
		if (plugin.getConfig().getBoolean(IMMEDIATELY_END_GAME)) {
			mainGame.removeGameWithName(game.getGameInfo().name());	
		}
	}
	
	private void spawnFireworkEntity(Location location) {
		Location loc = location.clone().add(0,3,0);
		
		Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fwm = firework.getFireworkMeta();
		
		fwm.setPower(0);
		fwm.setCustomModelData(FIREWORK_MODEL_ID);
		fwm.addEffect(fireworkEffect);
		
		firework.setFireworkMeta(fwm);

		Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 3L);
	}
}
