package ru.kredwi.qa.commands;

import static ru.kredwi.qa.config.ConfigKeys.*;
import static ru.kredwi.qa.config.ConfigKeys.IN_ARGUMENT_NEEDED_NUMBER;
import static ru.kredwi.qa.config.ConfigKeys.IS_GAME_ALREADY_CREATED;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ALREADY_CREATE_YOUR_GAME;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.impl.Game;

public class CreateGame extends CommandAbstract {
	
	private QAConfig cm;
	
	public CreateGame(QAConfig cm) {
		super("creategame", 2, true, "qaplugin.commands.creategame");
		this.cm = cm;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		int maxBlocks;
		try {
			maxBlocks = Math.abs(Integer.parseInt(args[1]));
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
		
		IGame game = commandController.getMainGame().getGame(args[0]);
		if (Objects.isNull(game)) {
			
			Player player = (Player)sender;
			if (Objects.nonNull(commandController.getMainGame().getGameFromPlayer(player))) {
				sender.sendMessage(cm.getAsString(YOU_ALREADY_CREATE_YOUR_GAME));
				return;
			}
			
			commandController.getMainGame().addGame(new Game(args[0], player, maxBlocks,
					commandController.getPlugin(), commandController.getSQLManager(), commandController.getMainGame()));
			
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
			List<String> numbers = new ArrayList<>(10);
			
			for (int i =0; i < numbers.size(); i++) {
				numbers.add(String.valueOf(i));
			}
			
			return numbers;
		}
		return null;
	}

}
