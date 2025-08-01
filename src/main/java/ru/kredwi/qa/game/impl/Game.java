package ru.kredwi.qa.game.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.GameMode;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameEvent;
import ru.kredwi.qa.game.service.IBlockConstructionService;
import ru.kredwi.qa.game.service.IGameAnswer;
import ru.kredwi.qa.game.service.IGamePlayer;
import ru.kredwi.qa.game.service.IGameQuestionManager;
import ru.kredwi.qa.game.service.IWinnerService;

public abstract class Game implements IGame {

	private final GameInfo gameInfo;

	private GameServices services;
	
	private AtomicBoolean preStopGame = new AtomicBoolean(false);
	private boolean isStart;
	private boolean isFinish;
	
	public Game(String name, Player owner, GameMode gameMode) {
		this.gameInfo = new GameInfo(name.trim().toLowerCase(), owner.getUniqueId(), owner.getLocation().clone(), gameMode);
	}
	
	public void setServices(GameServices services) {
		this.services = services;
	}
	
	@Override
	public boolean isAllServicesReady() {
		return getBlockConstruction().isServiceReady()
				&& getGameAnswer().isServiceReady()
				&& getPlayerService().isServiceReady()
				&& getQuestionManager().isServiceReady()
				&& getWinnerService().isServiceReady();
	}
	
	@Override
	public IGameEvent getGameEvents() {
		return services.getGameEvents();
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
	public boolean isPreStopGame() {
		return preStopGame.get();
	}

	@Override
	public void setPreStopGame(boolean preStopGame) {
		this.preStopGame.set(preStopGame);
	}

	@Override
	public boolean isFinish() {
		return isFinish;
	}

	@Override
	public void setFinished(boolean isFinish) {
		this.isFinish = isFinish;
	}
	
}
