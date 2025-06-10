package ru.kredwi.qa.game.impl;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameAnswer;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IGameQuestionManager;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.sql.SQLManager;

public class Game implements IGame {
	
	private final GameServices services;
	private final GameInfo gameInfo;
	private boolean isStart;
	
	public Game(String name, Player owner, int blocksToWin, PluginWrapper plugin, SQLManager sqlManager, IMainGame gameManager) {
		this.gameInfo = new GameInfo(name.trim().toLowerCase(),
				owner.getUniqueId(), blocksToWin);
		this.services = new GameServices(plugin, gameManager, this, sqlManager);
	}
	
	@Override
	public IGameQuestionManager getQuestionManager() {
		return services.getQuestionManager();
	};
	
	@Override
	public IGameAnswer getGameAnswer() {
		return services.getGameAnswer();
	};
	
	@Override
	public IBlockConstructionService getBlockConstruction() {
		return services.getBlockConstructionService();
	};
	
	@Override
	public IWinnerService getWinnerService() {
		return services.getWinnerService();
	};
	
	@Override
	public IGamePlayer getPlayerService() {
		return services.getGamePlayer();
	};
	
	@Override
	public GameInfo getGameInfo() {
		return gameInfo;
	}

	@Override
	public boolean isStart() {
		return isStart;
	}

	@Override
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	@Override
	public boolean isAllServicesReady() {
		return getBlockConstruction().isServiceReady()
				&& getGameAnswer().isServiceReady()
				&& getPlayerService().isServiceReady()
				&& getQuestionManager().isServiceReady()
				&& getWinnerService().isServiceReady();
	}
}
