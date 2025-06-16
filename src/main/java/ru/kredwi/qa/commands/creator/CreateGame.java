package ru.kredwi.qa.commands.creator;

import static ru.kredwi.qa.config.ConfigKeys.GAME_IS_CREATED;
import static ru.kredwi.qa.config.ConfigKeys.IN_ARGUMENT_NEEDED_NUMBER;
import static ru.kredwi.qa.config.ConfigKeys.IS_GAME_ALREADY_CREATED;
import static ru.kredwi.qa.config.ConfigKeys.MAX_LENGTH_PATH;
import static ru.kredwi.qa.config.ConfigKeys.MIN_LENGTH_PATH;
import static ru.kredwi.qa.config.ConfigKeys.NO_ARGS;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ALREADY_CREATE_YOUR_GAME;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ENTERED_LONG_PATH_LENGTH;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ENTERED_SHORT_PATH_LENGTH;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.GameMode;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.impl.classic.ClassicGame;
import ru.kredwi.qa.game.impl.pleonasms.PleoyasmsGame;

public class CreateGame extends CommandAbstract {
	
	private QAConfig cm;
	
	public CreateGame(QAConfig cm) {
		super("creategame", 2, true, "qaplugin.game.creator");
		this.cm = cm;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		
		IGame game = commandController.getMainGame().getGame(args[0]);
		if (Objects.isNull(game)) {
			
			Player player = (Player)sender;
			if (Objects.nonNull(commandController.getMainGame().getGameFromPlayer(player))) {
				sender.sendMessage(cm.getAsString(YOU_ALREADY_CREATE_YOUR_GAME));
				return;
			}
			
			if (args[1].equalsIgnoreCase(GameMode.CLASSIC.name())) {
				if (args.length == 3) {
					sender.sendMessage(cm.getAsString(NO_ARGS));
					return;
				}
				int maxBlocks;
				try {
					maxBlocks = Math.abs(Integer.parseInt(args[2]));
				} catch(NumberFormatException e) {
					sender.sendMessage(cm.getAsString(IN_ARGUMENT_NEEDED_NUMBER));
					return;
				}
				
				if (maxBlocks < cm.getAsInt(MIN_LENGTH_PATH)) {
					sender.sendMessage(MessageFormat.format(cm.getAsString(YOU_ENTERED_SHORT_PATH_LENGTH), maxBlocks, cm.getAsInt(MIN_LENGTH_PATH)));
					return;			
				}
			
				if (maxBlocks > cm.getAsInt(MAX_LENGTH_PATH)) {
					sender.sendMessage(MessageFormat.format(cm.getAsString(YOU_ENTERED_LONG_PATH_LENGTH), maxBlocks, cm.getAsInt(MAX_LENGTH_PATH)));
					return;
				}
				
				commandController.getMainGame().addGame(new ClassicGame(args[0], maxBlocks, player,
						commandController.getPlugin(), commandController.getSQLManager(), commandController.getMainGame()));
			} else if (args[1].equalsIgnoreCase(GameMode.PLEONASMS.name())) {
				commandController.getMainGame().addGame(new PleoyasmsGame(args[0], player,
						commandController.getPlugin(), commandController.getSQLManager(), commandController.getMainGame()));
			} else {
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
			List<String> gameMode = new ArrayList<>();
			for (GameMode mode : GameMode.values()) {
				gameMode.add(mode.name());
			}
			return gameMode;
		}
		return null;
	}

}
