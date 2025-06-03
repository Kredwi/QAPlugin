package ru.kredwi.qa.commands;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.sql.SQLManager;

public class DeleteGame extends CommandAbstract {

	private IMainGame mainGame;
	
	public DeleteGame(IMainGame mainGame) {
		super("deletegame", 0, false, "qaplugin.commands.deletegame");
		this.mainGame=mainGame;
	}

	@Override
	public void run(ICommandController commandController, SQLManager sqlManager, CommandSender sender, Command command, String[] args) {
		
		IGame game = null;
		
		if (sender instanceof Player) {
			game = mainGame.getGameFromPlayer((Player) sender);
		}
		
		if (args.length == 0 && Objects.isNull(game)) {
			sender.sendMessage(QAConfig.YOU_NOT_CONNECTED_TO_GAME.getAsString());
			return;
		}
		
		if (args.length >0) {
			game = commandController.getMainGame().getGame(args[0]);
		}
		
		if (Objects.isNull(game)) {
			sender.sendMessage(QAConfig.GAME_NOT_FOUND.getAsString());
			return;
		}
		
		if (!(sender instanceof ConsoleCommandSender) && !game.getGameInfo().isPlayerOwner((Player) sender)) {
			sender.sendMessage(QAConfig.YOU_DONT_GAME_OWNER.getAsString());
			return;
		}
		
		
		commandController.getMainGame().removeGameWithName(game.getGameInfo().name());
		
		sender.sendMessage(QAConfig.GAME_DELETE.getAsString());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!isHaveNeedsPermissions(sender)) return null;
		
		return mainGame.getNamesFromGames().stream()
			.filter(e -> e.toLowerCase().startsWith(args[0].toLowerCase()))
			.collect(Collectors.toList());
	}

}
