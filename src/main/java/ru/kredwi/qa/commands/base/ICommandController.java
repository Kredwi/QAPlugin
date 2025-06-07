package ru.kredwi.qa.commands.base;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.sql.SQLManager;

public interface ICommandController {
	public PluginWrapper getPlugin();
	public IMainGame getMainGame();
	public SQLManager getSQLManager();
	public QAConfig getConfigManager();
}
