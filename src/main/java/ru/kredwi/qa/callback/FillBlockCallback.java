package ru.kredwi.qa.callback;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class FillBlockCallback implements Consumer<Void> {

	public static final int FIREWORK_MODEL_ID = 909827261;
	
	private FireworkEffect fireworkEffect = FireworkEffect.builder()
			.with(QAConfig.FIREWORK_FOR_WINNER_TYPE.getAsFireworkType())
			.withColor(QAConfig.FIREWORK_FOR_WINNER_COLORS.getAsBukkitColorList())
			.withFade(QAConfig.FIREWORK_FOR_WINNER_FADES.getAsBukkitColorList())
			.flicker(QAConfig.FIREWORK_FOR_WINNER_FLICKER.getAsBoolean())
			.trail(QAConfig.FIREWORK_FOR_WINNER_TRAIL.getAsBoolean())
			.build();
	
	private Plugin plugin;
	private IMainGame mainGame;
	private IGame game;
	private Player player;
	
	public FillBlockCallback(Plugin plugin, IMainGame mainGame, IGame game, Player player) {
		this.plugin = plugin;
		this.mainGame = mainGame;
		this.game = game;
		this.player = player;
	}
	
	@Override
	public void accept(Void o) {
		// get path owner
		PlayerState state = game.getPlayerState(player);
		// reset all dynamic states
		state.resetState();
		
		// player build complete
		game.addBuildComplete();
		
		// checks is winner?
		if (game.isPlayerWin(state)) {
			// add winner to list winners
			game.addWinner(player);
		}
		
		// if last player complete build
		if (game.getBuildComplete() > game.getPlayers().size()) {
			
			// reset build completes
			game.resetBuildComplete();
			
			// if have winner
			if (!game.getWinners().isEmpty()) {
				
				// spawn for winners firework effects
				if (QAConfig.FIREWORK_FOR_WINNER_ENABLE.getAsBoolean()) {
					game.getWinners().forEach(p -> spawnFireworkEntity(p.getLocation()));		
				}
				
				// and alert all players in the game of winners
				game.alertOfPlayersWin();
				
				// and remove game from global games
				mainGame.removeGameWithName(game.getGameInfo().name());
				
			// if game is dont have winners questions players of new question
			} else game.questionPlayers();
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
