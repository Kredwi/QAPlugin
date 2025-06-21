package ru.kredwi.qa.callback;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.utils.GameUtils;

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
				GameUtils.setWinnerWhyManyBuilded(game);
				
				if (!game.getWinnerService().getWinners().isEmpty())
					return;
				
				game.getWinnerService().executeWinnerHandler();
				
				return;
			} else game.getQuestionManager().questionAllPlayers();
		}
	}

}
