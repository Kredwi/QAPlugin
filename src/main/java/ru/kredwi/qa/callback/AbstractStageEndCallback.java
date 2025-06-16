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

public class AbstractStageEndCallback {
	
	protected PluginWrapper plugin;
	private IGame game;
	private IMainGame mainGame;

	
	public AbstractStageEndCallback(PluginWrapper plugin, IMainGame mainGame, IGame game) {
		this.plugin = plugin;
		this.game = game;
		this.mainGame = mainGame;
	}
	

	
	protected void winnerOrQuestionsPlayer(Player player, PlayerState state) {
		// checks is winner?
		if (game.getWinnerService().isPlayerWin(state)) {
			// add winner to list winners
			game.getWinnerService().addWinner(player);
		}
		
		if (!game.getWinnerService().getWinners().isEmpty()) {
			
			game.getWinnerService().executeWinnerHandler();
			
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
				
				game.getWinnerService().executeWinnerHandler();
				return;
			} else game.getQuestionManager().questionAllPlayers();
		}
	}

}
