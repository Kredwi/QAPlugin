package ru.kredwi.qa.game.factory;

import static ru.kredwi.qa.config.ConfigKeys.IN_ARGUMENT_NEEDED_NUMBER;
import static ru.kredwi.qa.config.ConfigKeys.MAX_LENGTH_PATH;
import static ru.kredwi.qa.config.ConfigKeys.MIN_LENGTH_PATH;
import static ru.kredwi.qa.config.ConfigKeys.NO_ARGS;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ENTERED_LONG_PATH_LENGTH;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ENTERED_SHORT_PATH_LENGTH;

import java.text.MessageFormat;
import java.util.Objects;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.impl.classic.ClassicGame;
import ru.kredwi.qa.sql.SQLManager;

public class ClassicGameFactory implements ICreatorFactory {

	private QAConfig cm;
	private PluginWrapper plugin;
	private SQLManager sqlManager;
	private IMainGame gameManager;
	
	public ClassicGameFactory(PluginWrapper plugin) {
		this.cm = plugin.getConfigManager();
		this.plugin = plugin;
		this.sqlManager = plugin.getSqlManager();
		this.gameManager = plugin.getGameManager();
	}
	
	@Override
	public boolean validateParams(String[] args, Player owner) {
		if (args.length < 3) {
			owner.sendMessage(cm.getAsString(NO_ARGS));
			return false;
		}
		int maxBlocks;
		
		try {
			maxBlocks = Math.abs(Integer.parseInt(args[2]));
		} catch(NumberFormatException e) {
			owner.sendMessage(cm.getAsString(IN_ARGUMENT_NEEDED_NUMBER));
			return false;
		}
		
		if (maxBlocks < cm.getAsInt(MIN_LENGTH_PATH)) {
			owner.sendMessage(MessageFormat.format(cm.getAsString(YOU_ENTERED_SHORT_PATH_LENGTH), maxBlocks, cm.getAsInt(MIN_LENGTH_PATH)));
			return false;			
		}
	
		if (maxBlocks > cm.getAsInt(MAX_LENGTH_PATH)) {
			owner.sendMessage(MessageFormat.format(cm.getAsString(YOU_ENTERED_LONG_PATH_LENGTH), maxBlocks, cm.getAsInt(MAX_LENGTH_PATH)));
			return false;
		}
		
		return true;
	}

	@Override
	public IGame createGame(String name, Player owner, String[] args) {
		
		Objects.requireNonNull(name, "invalid name of game");
		Objects.requireNonNull(owner, "owner is not null");
		Objects.requireNonNull(args, "invalid data");
		
		if (args.length > 0) {
			// correct checking in `validateParams`
			int blocksToWin = Integer.parseInt(args[0]);
			
			return new ClassicGame(name, blocksToWin, owner, plugin, sqlManager, gameManager);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
}
