package ru.kredwi.qa.commands.creator;

import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.GAME_FINISHED;
import static ru.kredwi.qa.config.ConfigKeys.GAME_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.IN_INPUT_DATA_LITTLE_SYMBOLS;
import static ru.kredwi.qa.config.ConfigKeys.IN_INPUT_DATA_OVER_SYMBOLS;
import static ru.kredwi.qa.config.ConfigKeys.IS_COMMAND_ONLY_FOR_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.MAX_SYMBOL_IN_ANSWER;
import static ru.kredwi.qa.config.ConfigKeys.MIN_SYMBOL_IN_ANSWER;
import static ru.kredwi.qa.config.ConfigKeys.NO_ARGS;

import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.IGameQuestionManager;
import ru.kredwi.qa.game.service.data.Question;

public class NewQuestion extends CommandAbstract {
	
	private QAConfig cm;
	
	public NewQuestion(QAConfig cm) {
		super("question", 1, true, true, "qaplugin.game.creator");
		this.cm = cm;
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(cm.getAsString(NO_ARGS));
			return;
		}

		Player player = ((Player) sender);
		IGame game = commandController.getMainGame()
				.getGameFromPlayer(player);
		
		if (Objects.isNull(game)) {
			sender.sendMessage(cm.getAsString(GAME_NOT_FOUND));
			return;
		}
		
		if (!(game.getGameInfo().isPlayerOwner(player))) {
			sender.sendMessage(cm.getAsString(IS_COMMAND_ONLY_FOR_GAME_OWNER));
			return;
		}
		
		if (game.isFinish()) {
			sender.sendMessage(cm.getAsString(GAME_FINISHED));
			return;
		}
		
		String textOfQuestion = String.join(" ", args);
		int minSymbols = cm.getAsInt(MIN_SYMBOL_IN_ANSWER);
		int maxSymbols = cm.getAsInt(MAX_SYMBOL_IN_ANSWER);
		
		if (textOfQuestion.length() < minSymbols) {
			String message = 
					String.format(cm.getAsString(IN_INPUT_DATA_LITTLE_SYMBOLS), minSymbols);
			sender.sendMessage(message);
			return;
		}
		
		if (textOfQuestion.length() > maxSymbols) {
			String message = 
					String.format(cm.getAsString(IN_INPUT_DATA_OVER_SYMBOLS), maxSymbols);
			sender.sendMessage(message);
			return;
		}
		
		for (Player pl : game.getPlayerService().getPlayers()) {
			PlayerState playerState = game.getPlayerService().getPlayerState(pl);
			playerState.resetState();
		}
		
		game.getGameAnswer().resetAnwserCount();
		
		IGameQuestionManager questionService = game.getQuestionManager();
		
		// Add new question
		Question question = questionService.addNewQuestion(textOfQuestion);
		
		if (Objects.isNull(question)) {
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("New custom question in NewQuestion is NULL !!");
			}
			return;
		}
		
		// Add question all players in the game of new question
		questionService.questionAllPlayersOfQuestion(question);
		
		return;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		return null;
	}

}
