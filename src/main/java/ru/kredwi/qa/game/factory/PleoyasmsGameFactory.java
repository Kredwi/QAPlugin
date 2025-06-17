package ru.kredwi.qa.game.factory;

import java.util.Objects;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.impl.pleonasms.PleoyasmsGame;
import ru.kredwi.qa.sql.SQLManager;

public class PleoyasmsGameFactory implements ICreatorFactory{
	
//	private QAConfig cm;
	private PluginWrapper plugin;
	private SQLManager sqlManager;
	private IMainGame gameManager;
	
	public PleoyasmsGameFactory(PluginWrapper plugin) {
//		this.cm = plugin.getConfigManager();
		this.plugin = plugin;
		this.sqlManager = plugin.getSqlManager();
		this.gameManager = plugin.getGameManager();
	}
	
	@Override
	public boolean validateParams(String[] args, Player owner) {
		return true;
	}

	@Override
	public IGame createGame(String name, Player owner, String[] args) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(owner);
		Objects.requireNonNull(args);
		return new PleoyasmsGame(name, owner, plugin, sqlManager, gameManager);
	}

}
