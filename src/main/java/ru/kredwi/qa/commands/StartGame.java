package ru.kredwi.qa.commands;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

/**
 * Initializes the game by performing the following actions:
 *
 * <ol>
 *  <li>Building initial blocks.</li>
 *  <li>Teleporting players to starting positions.</li>
 *  <li>Initializing the question list.</li>
 *  <li>Sending the first question to all players.</li>
 * </ol>
 *
 * @author Kredwi
 */
public class StartGame extends CommandAbstract {

	public StartGame(IMainGame mainGame) {
		super(mainGame, "startgame", "qaplugin.commands.startgame");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) {
			sendError(sender, QAConfig.NOT_HAVE_PERMISSION);
			return true;
		}
		
		if (!hasMoreArgsThan(args.length, 0)) {
			sendError(sender, QAConfig.NO_ARGS);
			return true;
		}
		
		IGame game = mainGame.getGame(args[0]);
		
		if (!isGameExists(game)) {
			sendError(sender, QAConfig.GAME_NOT_FOUND);
			return true;
		}
		
		Set<Player> players = game.getPlayers();
		
		if (players.size()< 1) {
			sendError(sender, QAConfig.IN_THE_GAME_NOT_FOUND_PATHS);
			return true;
		}
		game.setStart(true);
		// INIT BLOCKS
		game.processPlayerAnswers(true);
		
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		if (!playerHavePermissions(sender)) return Collections.emptyList();		
		
		return mainGame.getNamesFromGames().stream().toList();
	}
}
