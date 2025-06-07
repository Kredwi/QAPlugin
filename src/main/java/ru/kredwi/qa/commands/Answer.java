package ru.kredwi.qa.commands;

import static ru.kredwi.qa.config.ConfigKeys.IN_INPUT_DATA_LITTLE_SYMBOLS;
import static ru.kredwi.qa.config.ConfigKeys.IN_INPUT_DATA_OVER_SYMBOLS;
import static ru.kredwi.qa.config.ConfigKeys.MAX_SYMBOL_IN_ANSWER;
import static ru.kredwi.qa.config.ConfigKeys.MIN_SYMBOL_IN_ANSWER;
import static ru.kredwi.qa.config.ConfigKeys.NOT_HAVE_PERMISSION;

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
	
	private QAConfig cm;
	private Consumer<PlayerAnswerData> callback;

	public Answer(QAConfig configManager, IMainGame mainGame) {
		this(configManager, mainGame, new PlayerAnswerCallback(mainGame, configManager));
	}
	
	public Answer(QAConfig configManager, IMainGame mainGame, Consumer<PlayerAnswerData> callback) {
		super("answer", true, "qaplugin.commands.answer");
		this.cm = configManager;
		this.callback = callback;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command cmd, String[] args) {
		
		if (!isHaveNeedsPermissions(sender)) {
			sender.sendMessage(cm.getAsString(NOT_HAVE_PERMISSION));
			return;
		}
		
		String text = args[0];
		
		int minSymbols = cm.getAsInt(MIN_SYMBOL_IN_ANSWER);
		int maxSymbols = cm.getAsInt(MAX_SYMBOL_IN_ANSWER);
		
		if (text.length() < minSymbols) {
			String message = 
					MessageFormat.format(cm.getAsString(IN_INPUT_DATA_LITTLE_SYMBOLS),
							minSymbols, text.length());
			sender.sendMessage(message);
			return;
		}
		
		if (text.length() > maxSymbols) {
			String message = 
					MessageFormat.format(cm.getAsString(IN_INPUT_DATA_OVER_SYMBOLS),
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