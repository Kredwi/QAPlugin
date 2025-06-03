package ru.kredwi.qa.game.impl;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameAnswer;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IGameQuestionManager;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.service.BlockConstructionService;
import ru.kredwi.qa.game.service.GameAnswerService;
import ru.kredwi.qa.game.service.GamePlayerService;
import ru.kredwi.qa.game.service.QuestionService;
import ru.kredwi.qa.game.service.WinnerService;

public class GameServices {
	private final IGameQuestionManager questionManager;
	private final IGameAnswer gameAnswer;
	private final IBlockConstructionService blockConstructionService;
	private final IWinnerService winnerService;
	private final IGamePlayer gamePlayer;
	
	public GameServices(IGame game, QAPlugin plugin) {
		this.questionManager = new QuestionService(game,game);
		this.gameAnswer = new GameAnswerService(game, plugin.getSqlManager());
		this.blockConstructionService = new BlockConstructionService(game, plugin);
		this.winnerService = new WinnerService(game, plugin.getSqlManager());
		this.gamePlayer = new GamePlayerService();
	}

	public IGameQuestionManager getQuestionManager() {
		return questionManager;
	}

	public IGameAnswer getGameAnswer() {
		return gameAnswer;
	}

	public IBlockConstructionService getBlockConstructionService() {
		return blockConstructionService;
	}

	public IWinnerService getWinnerService() {
		return winnerService;
	}

	public IGamePlayer getGamePlayer() {
		return gamePlayer;
	}
}
