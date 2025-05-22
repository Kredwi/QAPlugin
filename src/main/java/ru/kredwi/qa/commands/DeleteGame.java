package ru.kredwi.qa.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public class DeleteGame extends CommandAbstract {

	public DeleteGame(IMainGame mainGame) {
		super(mainGame, "deletegame", "qaplugin.commands.deletegame");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) {
			sendError(sender, QAConfig.NOT_HAVE_PERMISSION);
			return true;
		}
		
		if (!sendMessageIfNotPlayer(sender)) return true;
		
		if (!hasMoreArgsThan(args.length, 0)) {
			sendError(sender, QAConfig.GAME_NOT_FOUND);
			return true;
		}
		
		if (isGameExists(args[0])) {
			IGame game = mainGame.getGame(args[0]);
			if (game.getGameInfo().isPlayerOwner((Player) sender)) {
				game.deleteBuildedBlocks();
				if (!mainGame.removeGameWithName(args[0])) {
					sendError(sender, QAConfig.UNKOWN_PROBLEM_WITH_GAME_DELETE);
					return true;
				}
				sendSuccess(sender, QAConfig.GAME_DELETE);
			} else sendError(sender, QAConfig.YOU_DONT_GAME_OWNER);
		} else sendError(sender, QAConfig.GAME_NOT_FOUND);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) return null;
		
		return mainGame.getNamesFromGames().stream()
			.filter(e -> e.toLowerCase().startsWith(args[0].toLowerCase()))
			.collect(Collectors.toList());
	}

}
