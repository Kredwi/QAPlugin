package ru.kredwi.qa.callback;

import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.INDESTRUCTIBLE_BLOCK_ENCOUNTERED;

import java.util.ConcurrentModificationException;
import java.util.function.Predicate;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.callback.data.BreakIsBlockedData;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.game.ActionsBlockBuilded;
import ru.kredwi.qa.game.IGame;

public class BlockBreakDeniedCallback implements Predicate<BreakIsBlockedData> {

	private boolean isGameEnd = false;
	
	private PluginWrapper plugin;
	
	private IGame game;

	public BlockBreakDeniedCallback(PluginWrapper plugin, IGame game) {
		this.game = game;
		this.plugin = plugin;
	}
	
	
	/**
	 * 
	 * @return continue iterator?
	 * */
	@Override
	public boolean test(BreakIsBlockedData event) {
		try {
			String actionBlockBuildedName = plugin.getConfigManager().getAsString(INDESTRUCTIBLE_BLOCK_ENCOUNTERED);
			ActionsBlockBuilded actionBlockBuilded = ActionsBlockBuilded.valueOf(actionBlockBuildedName);
			return switch (actionBlockBuilded) {
				case END_GAME -> endGame();
				case IGNORE -> ignoreIteration();
			};
			
		} catch (IllegalArgumentException|NullPointerException|ConcurrentModificationException e) {
			if (plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG)) {
				QAPlugin.getQALogger().warning("ERROR IN BLOCK BREAK DENIED CALLBACK !!!");
				e.printStackTrace();
			}
			return false;
		}
	}
	
	private synchronized boolean endGame() {
		
		if (isGameEnd) {
			if (plugin.getConfigManager().getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("Game already end.");
			}
			return false;
		};
		
		this.isGameEnd = true;
		
		// set mark of pre stop game
		game.setPreStopGame(true);
		
		return false;
	}
	private boolean ignoreIteration() {
		return true;
	}

}
