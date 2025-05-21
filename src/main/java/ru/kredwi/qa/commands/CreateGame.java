package ru.kredwi.qa.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.handler.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.impl.Game;

public class CreateGame extends CommandAbstract {
	
	private QAPlugin plugin;
	
	public CreateGame(QAPlugin plugin) {
		super(plugin, "creategame", "qaplugin.commands.creategame");
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) {
			sendError(sender, QAConfig.NOT_HAVE_PERMISSION);
			return true;
		}
		
		if (!isPlayer(sender)) {
			sendError(sender, QAConfig.COMMAND_ONLY_FOR_PLAYERS);
			return true;
		}
		
		
		if (!validateArgs(args.length, 1)) {
			sendError(sender, QAConfig.NO_ARGS);
			return true;
		}
		
		int maxBlocks;
		try {
			maxBlocks = Integer.parseInt(args[1]);
			if (maxBlocks < 0) {
				maxBlocks = -maxBlocks;
			}
		} catch(NumberFormatException e) {
			sendError(sender, QAConfig.IN_ARGUMENT_NEEDED_NUMBER);
			return true;
		}
		if (!isGameExists(args[0])) {
			
			Player player = (Player)sender;
			if (mainGame.getGameFromPlayer(player) != null) {
				sendError(sender, QAConfig.YOU_ALREADY_CREATE_YOUR_GAME);
				return true;
			}
			
			mainGame.addGame(new Game(args[0], player, maxBlocks, plugin));
			
			sendSuccess(sender, QAConfig.GAME_IS_CREATED);
			
		} else {
			sendError(sender, QAConfig.IS_GAME_ALREADY_CREATED);
			return true;
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
