package ru.kredwi.qa.commands;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.callback.PlayerAnswerCallback;
import ru.kredwi.qa.callback.data.PlayerAnswerData;
import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IMainGame;

public class Answer extends CommandAbstract{
	
	private Consumer<PlayerAnswerData> callback;

	public Answer(IMainGame mainGame) {
		this(mainGame, new PlayerAnswerCallback(mainGame));
	}
	
	public Answer(IMainGame mainGame, Consumer<PlayerAnswerData> callback) {
		super("answer", false, "qaplugin.commands.answer");
		this.callback = callback;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command cmd, String[] args) {
		
		if (!isHaveNeedsPermissions(sender)) {
			sender.sendMessage(QAConfig.NOT_HAVE_PERMISSION.getAsString());
			return;
		}
		
		String text = args[0];
		
		int minSymbols = QAConfig.MIN_SYMBOL_IN_ANSWER.getAsInt();
		int maxSymbols = QAConfig.MAX_SYMBOL_IN_ANSWER.getAsInt();
		
		if (text.length() < minSymbols) {
			String message = 
					MessageFormat.format(QAConfig.IN_INPUT_DATA_LITTLE_SYMBOLS.getAsString(),
							minSymbols, text.length());
			sender.sendMessage(message);
			return;
		}
		
		if (text.length() > maxSymbols) {
			String message = 
					MessageFormat.format(QAConfig.IN_INPUT_DATA_OVER_SYMBOLS.getAsString(),
							maxSymbols, text.length());
			sender.sendMessage(message);
			return;
		}
		
		callback.accept(new PlayerAnswerData(((Player) sender), text));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return Collections.emptyList();
	}
	
	public void setCallback(Consumer<PlayerAnswerData> callback) {
		this.callback = callback;
	}
}