package ru.kredwi.qa.commands.creator;

import static ru.kredwi.qa.config.ConfigKeys.GAME_IS_CREATED;
import static ru.kredwi.qa.config.ConfigKeys.INPUTED_INVALID_DATA;
import static ru.kredwi.qa.config.ConfigKeys.IS_GAME_ALREADY_CREATED;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ALREADY_CREATE_YOUR_GAME;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.GameMode;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.factory.ICreatorFactory;
import ru.kredwi.qa.game.factory.IGameFactory;

public class CreateGame extends CommandAbstract {
	
	private IGameFactory gameFactory;
	private QAConfig cm;
	
	public CreateGame(QAConfig cm, IGameFactory gameFactory) {
		super("creategame", 2, true, "qaplugin.game.creator");
		this.cm = cm;
		this.gameFactory = gameFactory;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		IGame game = commandController.getMainGame().getGame(args[0]);
		if (game == null) {
			
			Player player = (Player)sender;
			if (commandController.getMainGame().getGameFromPlayer(player) != null) {
				sender.sendMessage(cm.getAsString(YOU_ALREADY_CREATE_YOUR_GAME));
				return;
			}
			
			try {
				GameMode mode = GameMode.valueOf(args[1].trim().toUpperCase());
				
				ICreatorFactory factory = gameFactory.getGameFactory(mode);
				
				// if validates is invalid stop next execute code
				if (!factory.validateParams(args, player)) return;
				
				String[] arguments;
				
				// recheck
				if (super.needArgs() < args.length) {
					arguments = Arrays.copyOfRange(args, super.needArgs(), args.length);
				} else {
					arguments = new String[0];
				}
				
				IGame newGame = factory.createGame(args[0], player, arguments);
				
				newGame.getQuestionManager().loadQuestions();
				
				commandController.getMainGame().addGame(newGame);
				
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				sender.sendMessage(cm.getAsString(INPUTED_INVALID_DATA));
				return;
			} catch (NullPointerException e) {
				e.printStackTrace();
				sender.sendMessage(cm.getAsString(INPUTED_INVALID_DATA));
				return;
			}
			sender.sendMessage(cm.getAsString(GAME_IS_CREATED));
			
		} else {
			sender.sendMessage(cm.getAsString(IS_GAME_ALREADY_CREATED));
			return;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!isHaveNeedsPermissions(sender)) return Collections.emptyList();
		
		if (args.length == 2) {
			return gameFactory.getAllGameMode().stream()
					.map(Enum::name)
					.toList();
		}
		return null;
	}

}
