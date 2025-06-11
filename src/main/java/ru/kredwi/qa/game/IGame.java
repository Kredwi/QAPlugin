package ru.kredwi.qa.game;

import ru.kredwi.qa.game.impl.GameInfo;

public interface IGame {
	
	GameInfo getGameInfo();
	void setFinished(boolean isEnd);
	void setStart(boolean isStart);
	void setPreStopGame(boolean preStopGame);
	boolean isAllServicesReady();
	boolean isPreStopGame();
	boolean isStart();
	public boolean isFinish();
	
	public IGameQuestionManager getQuestionManager();
	public IGameAnswer getGameAnswer();
	public IBlockConstructionService getBlockConstruction();
	public IWinnerService getWinnerService();
	public IGamePlayer getPlayerService();
}
