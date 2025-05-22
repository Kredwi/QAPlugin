package ru.kredwi.qa.game;

import ru.kredwi.qa.game.impl.GameInfo;

public interface IGame
	extends IGameQuestionManager,IGameAnswer,
		IBlockConstructionService, IWinnerService, IGamePlayer {
	
	GameInfo getGameInfo();
	void setStart(boolean isStart);
	boolean isStart();
}
