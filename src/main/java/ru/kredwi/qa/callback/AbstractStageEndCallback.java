package ru.kredwi.qa.callback;

import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;

public class AbstractStageEndCallback {
	
	protected PluginWrapper plugin;
	private IGame game;

	
	public AbstractStageEndCallback(PluginWrapper plugin, IGame game) {
		this.plugin = plugin;
		this.game = game;
	}
	
	protected void winnerOrQuestionsPlayer(Player player, PlayerState state) {
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
				
				if (!game.getWinnerService().getWinners().isEmpty())
					return;
				
				game.getWinnerService().executeWinnerHandler();
				
				return;
			} else game.getQuestionManager().questionAllPlayers();
		}
	}

}
