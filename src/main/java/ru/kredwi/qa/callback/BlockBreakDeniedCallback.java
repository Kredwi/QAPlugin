package ru.kredwi.qa.callback;

import static ru.kredwi.qa.config.ConfigKeys.INDESTRUCTIBLE_BLOCK_ENCOUNTERED;

import java.util.ConcurrentModificationException;
import java.util.function.Predicate;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.callback.data.BreakIsBlockedData;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.action.ActionsBlockBuilded;

public class BlockBreakDeniedCallback implements Predicate<BreakIsBlockedData> {

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
			
			return actionBlockBuilded.execute(plugin.getGameManager(), plugin.getConfigManager(), game);
		} catch (IllegalArgumentException|NullPointerException|ConcurrentModificationException e) {
			if (plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG)) {
				QAPlugin.getQALogger().warning("ERROR IN BLOCK BREAK DENIED CALLBACK !!!");
				e.printStackTrace();
			}
			return false;
		}
	}

}
