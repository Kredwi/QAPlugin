package ru.kredwi.qa.commands;

import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.impl.Game;

public class CreateGame extends CommandAbstract {
	
	private QAPlugin plugin;
	
	public CreateGame(QAPlugin plugin) {
		// plugin in super cast to IMainGame
		super("creategame", 1, true, "qaplugin.commands.creategame");
		this.plugin = plugin;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		int maxBlocks;
		try {
			maxBlocks = Math.abs(Integer.parseInt(args[1]));
		} catch(NumberFormatException e) {
			sender.sendMessage(QAConfig.IN_ARGUMENT_NEEDED_NUMBER.getAsString());
			return;
		}
		
		IGame game = commandController.getMainGame().getGame(args[0]);
		if (Objects.isNull(game)) {
			
			Player player = (Player)sender;
			if (Objects.nonNull(commandController.getMainGame().getGameFromPlayer(player))) {
				sender.sendMessage(QAConfig.YOU_ALREADY_CREATE_YOUR_GAME.getAsString());
				return;
			}
			
			commandController.getMainGame().addGame(new Game(args[0], player, maxBlocks, plugin));
			
			sender.sendMessage(QAConfig.GAME_IS_CREATED.getAsString());
			
		} else {
			sender.sendMessage(QAConfig.IS_GAME_ALREADY_CREATED.getAsString());
			return;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
