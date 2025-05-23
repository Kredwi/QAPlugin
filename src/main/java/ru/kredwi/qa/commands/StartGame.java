package ru.kredwi.qa.commands;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
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

	private IMainGame mainGame;
	
	public StartGame(IMainGame mainGame) {
		super("startgame", 1, true, "qaplugin.commands.startgame");
		this.mainGame = mainGame;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		
		IGame game = commandController.getMainGame().getGame(args[0]);
		
		if (Objects.isNull(game)) {
			sender.sendMessage(QAConfig.GAME_NOT_FOUND.getAsString());
			return;
		}
		
		Set<Player> players = game.getPlayers();
		
		if (players.size()< 1) {
			sender.sendMessage(QAConfig.IN_THE_GAME_NOT_FOUND_PATHS.getAsString());
			return;
		}
		game.setStart(true);
		// INIT BLOCKS
		game.processPlayerAnswers(true);
		
		return;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		if (!isHaveNeedsPermissions(sender)) return Collections.emptyList();		
		
		return mainGame.getNamesFromGames().stream().toList();
	}
}
