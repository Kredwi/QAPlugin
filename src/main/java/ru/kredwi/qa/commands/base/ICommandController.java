package ru.kredwi.qa.commands.base;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.config.ConfigAs;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.sql.SQLManager;

public interface ICommandController {
	public PluginWrapper getPlugin();
	public IMainGame getMainGame();
	public SQLManager getSQLManager();
	public ConfigAs getConfigManager();
}
