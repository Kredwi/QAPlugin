package ru.kredwi.qa.callback;

import java.text.MessageFormat;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.service.IWinnerService;
import ru.kredwi.qa.utils.GameUtils;

public class AbstractStageEndCallback {
	
	protected PluginWrapper plugin;
	private IGame game;

	
	public AbstractStageEndCallback(@Nonnull PluginWrapper plugin, @Nonnull IGame game) {
		this.plugin = plugin;
		this.game = game;
	}
	
	protected void winnerOrQuestionsPlayer(@Nonnull Player player, @Nonnull String answer) {
		IWinnerService winnerService = game.getWinnerService();
		
		if (!winnerService.getWinners().isEmpty()) {
			winnerService.executeWinnerHandler();
			return;
		}
			
		if (!game.isPreStopGame()) {
			game.getQuestionManager().questionAllPlayers();
			return;
		}
		
		GameUtils.setWinnerWhyManyBuilded(game);
		
		if (winnerService.getWinners().isEmpty())
			return;
		
		winnerService.executeWinnerHandler();
	}
	
	protected void sendMessageIfDisplaysDisabled(@Nonnull String answer, @Nonnull String name) {
		if (answer == null || answer.trim().length() == 0)
			return;
		
		if (!plugin.getConfigManager().getAsBoolean(ConfigKeys.SPAWN_DISPLAY_TEXTS)) {
			String message = MessageFormat.format(
					plugin.getConfigManager().getAsString(ConfigKeys.PLAYER_ANSWER_OF),
					new String(answer),
					name);
		
			game.getPlayerService().getPlayers().forEach(e ->
				e.sendMessage(message));
		}
	}

}
