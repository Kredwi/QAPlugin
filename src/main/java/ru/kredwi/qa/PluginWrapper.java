package ru.kredwi.qa;

import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.config.ConfigAs;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.sql.SQLManager;

public interface PluginWrapper extends Plugin {
	public SQLManager getSqlManager();
	
	public IMainGame getGameManager();

	public ConfigAs getConfigManager();
}
