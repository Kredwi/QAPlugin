package ru.kredwi.qa.game.impl.pleonasms.service;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.WinnerService;
import ru.kredwi.qa.sql.DatabaseActions;

public class PleonasmsWinnerService extends WinnerService{

	private IGame game;
	
	public PleonasmsWinnerService(PluginWrapper plugin, IGame game, DatabaseActions databaseActions) {
		super(plugin, game, databaseActions);
		this.game = game;
	}
	
	@Override
	public boolean isPlayerWin(PlayerState state) {
		return game.getPlayerService().getPlayers().size() <= 1;
	}

}
