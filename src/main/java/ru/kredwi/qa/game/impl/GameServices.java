package ru.kredwi.qa.game.impl;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameAnswer;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IGameQuestionManager;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.service.BlockConstructionService;
import ru.kredwi.qa.game.service.GameAnswerService;
import ru.kredwi.qa.game.service.GamePlayerService;
import ru.kredwi.qa.game.service.QuestionService;
import ru.kredwi.qa.game.service.WinnerService;
import ru.kredwi.qa.sql.SQLManager;

public class GameServices {
	private final IGameQuestionManager questionManager;
	private final IGameAnswer gameAnswer;
	private final IBlockConstructionService blockConstructionService;
	private final IWinnerService winnerService;
	private final IGamePlayer gamePlayer;
	
	public GameServices(PluginWrapper plugin, IMainGame gameManager, IGame game, SQLManager sqlManager) {
		this.gameAnswer = new GameAnswerService(plugin.getConfigManager(), game, sqlManager);
		this.winnerService = new WinnerService(plugin.getConfigManager(), game, sqlManager);
		this.gamePlayer = new GamePlayerService(plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG));
		
		this.blockConstructionService = new BlockConstructionService(game, plugin, getGamePlayer(), getWinnerService());
		this.questionManager = new QuestionService(plugin.getConfigManager(), getGamePlayer());
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
