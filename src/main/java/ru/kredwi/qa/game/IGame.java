package ru.kredwi.qa.game;

import ru.kredwi.qa.game.impl.GameInfo;
import ru.kredwi.qa.game.service.IBlockConstructionService;
import ru.kredwi.qa.game.service.IGameAnswer;
import ru.kredwi.qa.game.service.IGamePlayer;
import ru.kredwi.qa.game.service.IGameQuestionManager;
import ru.kredwi.qa.game.service.IWinnerService;

public interface IGame {
	
	GameInfo getGameInfo();
	void setFinished(boolean isEnd);
	void setStart(boolean isStart);
	void setPreStopGame(boolean preStopGame);
	boolean isAllServicesReady();
	boolean isPreStopGame();
	boolean isStart();
	public boolean isFinish();
	
	public IGameEvent getGameEvents();
	public IGameQuestionManager getQuestionManager();
	public IGameAnswer getGameAnswer();
	public IBlockConstructionService getBlockConstruction();
	public IWinnerService getWinnerService();
	public IGamePlayer getPlayerService();
}
