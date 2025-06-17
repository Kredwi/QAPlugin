package ru.kredwi.qa;

import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.factory.IGameFactory;
import ru.kredwi.qa.sql.SQLManager;

public interface PluginWrapper extends Plugin {
	public SQLManager getSqlManager();
	
	public IMainGame getGameManager();

	public QAConfig getConfigManager();
	
	public IGameFactory getGameFactory();
}
