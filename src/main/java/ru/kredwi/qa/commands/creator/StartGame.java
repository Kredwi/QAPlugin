package ru.kredwi.qa.commands.creator;

import static ru.kredwi.qa.config.ConfigKeys.GAME_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.*;
import static ru.kredwi.qa.config.ConfigKeys.IS_COMMAND_ONLY_FOR_GAME_OWNER;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.GameMode;
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
 * @author Kredwi
 */
public class StartGame extends CommandAbstract {

	private QAConfig cm;
	private IMainGame mainGame;
	
	public StartGame(IMainGame mainGame, QAConfig cm) {
		super("startgame", 1, true, "qaplugin.game.creator");
		this.mainGame = mainGame;
		this.cm = cm;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		IGame game = commandController.getMainGame().getGame(args[0]);
		
		if (game == null) {
			sender.sendMessage(cm.getAsString(GAME_NOT_FOUND));
			return;
		}
		
		if (!game.getGameInfo().isPlayerOwner((Player) sender)) {
			sender.sendMessage(cm.getAsString(IS_COMMAND_ONLY_FOR_GAME_OWNER));
			return;
		}

		if (!game.isAllServicesReady()) {
			sender.sendMessage(cm.getAsString(SERVICES_NOT_STARTED));
			return;
		}
		
		Set<Player> players = game.getPlayerService().getPlayers();
		
		if (!players.contains((Player) sender)) {
			sender.sendMessage(cm.getAsString(IN_THE_GAME_NOT_FOUND_PATHS));
			return;
		}
		
		if (game.getGameInfo().mode().equals(GameMode.PLEONASMS)) {
			if (players.isEmpty() || players.size() < 2) {
				sender.sendMessage(cm.getAsString(IN_THE_GAME_NOT_FOUND_PATHS));
				return;
			}	
		}
		
		if (!game.isAllServicesReady()) {
			sender.sendMessage("Services is not started! Please wait..");
			return;
		}
		
		game.setStart(true);
		// INIT BLOCKS
		game.getGameAnswer().processPlayerAnswers(true);
		
		return;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

		if (!isHaveNeedsPermissions(sender)) return Collections.emptyList();		
		
		return mainGame.getNamesFromGames().stream().toList();
	}
}
