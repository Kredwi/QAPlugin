package ru.kredwi.qa.game;

import ru.kredwi.qa.game.impl.GameInfo;

public interface IGame {
	
	GameInfo getGameInfo();
	void setStart(boolean isStart);
	boolean isStart();
	
	public IGameQuestionManager getQuestionManager();
	public IGameAnswer getGameAnswer();
	public IBlockConstructionService getBlockConstruction();
	public IWinnerService getWinnerService();
	public IGamePlayer getPlayerService();
}
