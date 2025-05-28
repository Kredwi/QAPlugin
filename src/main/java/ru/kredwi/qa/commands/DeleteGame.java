package ru.kredwi.qa.commands;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public class DeleteGame extends CommandAbstract {

	private IMainGame mainGame;
	
	public DeleteGame(IMainGame mainGame) {
		super("deletegame", 1, false, "qaplugin.commands.deletegame");
		this.mainGame=mainGame;
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		
		IGame game = commandController.getMainGame().getGame(args[0]);
		
		if (Objects.isNull(game)) {
			sender.sendMessage(QAConfig.GAME_NOT_FOUND.getAsString());
			return;
		}
		
		if (!game.getGameInfo().isPlayerOwner((Player) sender)) {
			sender.sendMessage(QAConfig.YOU_DONT_GAME_OWNER.getAsString());
			return;
		}
		
		
		commandController.getMainGame().removeGameWithName(args[0]);
		
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
