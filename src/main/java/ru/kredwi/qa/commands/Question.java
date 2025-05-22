package ru.kredwi.qa.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class Question extends CommandAbstract {
	
	public Question(IMainGame mainGame) {
		super(mainGame, "question", "qaplugin.commands.question");
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
		
		if (!hasMoreArgsThan(args.length, 0)) {
			sendError(sender, QAConfig.NO_ARGS);
			return true;
		}
		
		Player player = ((Player) sender);
		IGame game = mainGame.getGameFromPlayer(player);
		
		if (!isGameExists(game)) {
			sendError(sender, QAConfig.GAME_NOT_FOUND);
			return true;
		}
		
		if (!(game.getGameInfo().isPlayerOwner(player))) {
			sendError(sender, QAConfig.IS_COMMAND_ONLYE_FOR_GAME_OWNER);
			return true;
		}
		
		String question = String.join(" ", args);
		
		for (Player pl : game.getPlayers()) {
			PlayerState playerState = game.getPlayerState(pl);
			playerState.resetState();
		}
		
		game.resetAnwserCount();
		game.questionPlayers(question);
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
