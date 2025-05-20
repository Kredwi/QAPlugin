package ru.kredwi.qa.commands.handler;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public abstract class CommandAbstract implements CommandExecutor, TabCompleter {
	protected final IMainGame mainGame;
	public final CommandRecord info;
	
	protected CommandAbstract(IMainGame mainGame, String name) {
		this.mainGame = mainGame;
		info = new CommandRecord(name.toLowerCase().trim());
	}
	
	protected final boolean validateArgs(int argsLength, int needArgs) {
		return argsLength > needArgs;
	}
	
	protected final void sendSuccess(CommandSender sender, String success) {
		sender.sendMessage(ChatColor.GREEN + success);
	}
	protected final void sendSuccess(CommandSender sender, QAConfig success, String...args) {
		if (args != null && args.length >0) {
			sender.sendMessage(MessageFormat.format(success.getAsString(), ((Object[]) args)));
			return;
		}
		sender.sendMessage(success.getAsString());
	}
	protected final void sendError(CommandSender sender, String error) {
		sender.sendMessage(ChatColor.RED + error);
	}
	protected final void sendError(CommandSender sender, QAConfig error) {
		sender.sendMessage(error.getAsString());
	}
	
	protected final boolean isPlayer(CommandSender sender) {
		return sender instanceof Player;
	}
	
	protected final boolean isGameExists(String gameName) {
		return isGameExists(mainGame.getGame(gameName));
	}
	
	protected final boolean isGameExists(IGame game) {
		return game != null;
	}
	
	protected final boolean sendMessageIfNotPlayer(CommandSender sender) {
		boolean isPlayer = isPlayer(sender);
		if (!isPlayer) {
			sendError(sender, QAConfig.COMMAND_ONLY_FOR_PLAYERS);
		}
		return isPlayer;
	}
}