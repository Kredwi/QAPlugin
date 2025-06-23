package ru.kredwi.qa.game.impl;

import java.util.Objects;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameEvent;
import ru.kredwi.qa.game.service.IBlockConstructionService;
import ru.kredwi.qa.game.service.IGameAnswer;
import ru.kredwi.qa.game.service.IGamePlayer;
import ru.kredwi.qa.game.service.IGameQuestionManager;
import ru.kredwi.qa.game.service.IWinnerService;
import ru.kredwi.qa.game.service.impl.BlockConstructionService;
import ru.kredwi.qa.game.service.impl.EventService;
import ru.kredwi.qa.game.service.impl.GameAnswerService;
import ru.kredwi.qa.game.service.impl.GamePlayerService;
import ru.kredwi.qa.game.service.impl.QuestionService;
import ru.kredwi.qa.sql.SQLManager;

public class GameServices {
	private final IGameQuestionManager questionManager;
	private final IGameAnswer gameAnswer;
	private final IBlockConstructionService blockConstructionService;
	private final IWinnerService winnerService;
	private final IGamePlayer gamePlayer;
	private final IGameEvent gameEvents;
	
    private GameServices(Builder builder) {
    	Objects.requireNonNull(builder);
    	this.questionManager = Objects.requireNonNull(builder.questionManager);
    	this.gameAnswer = Objects.requireNonNull(builder.gameAnswer);
    	this.blockConstructionService = Objects.requireNonNull(builder.blockConstructionService);
    	this.winnerService = Objects.requireNonNull(builder.winnerService);
    	this.gamePlayer = Objects.requireNonNull(builder.gamePlayer);
    	this.gameEvents=Objects.requireNonNull(builder.gameEvents);
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
	
    /**
	 * @return the gameEvents
	 */
	public IGameEvent getGameEvents() {
		return gameEvents;
	}

	public static class Builder {
        private IGameQuestionManager questionManager;
        private IGameAnswer gameAnswer;
        private IBlockConstructionService blockConstructionService;
        private IWinnerService winnerService;
        private IGamePlayer gamePlayer;
        private IGameEvent gameEvents;
        
        private final PluginWrapper plugin;
        private final IGame game;
        private final SQLManager sqlManager;

        public Builder(PluginWrapper plugin, IGame game, SQLManager sqlManager) {
            this.plugin = Objects.requireNonNull(plugin);
            this.game = Objects.requireNonNull(game);
            this.sqlManager = Objects.requireNonNull(sqlManager);
        }

        /**
         * @param eventService the eventService to set
         */
        public Builder setEventService(IGameEvent eventService) {
        	this.gameEvents = eventService;
        	return this;
        	}
        
        /**
         * @param questionManager the questionManager to set
         */
        public Builder setQuestionManager(IGameQuestionManager questionManager) {
            this.questionManager = Objects.requireNonNull(questionManager);
            return this;
        }


        /**
         * @param gameAnswer the gameAnswer to set
         */
        public Builder setGameAnswer(IGameAnswer gameAnswer) {
            this.gameAnswer = Objects.requireNonNull(gameAnswer);
            return this;
        }

        public Builder setBlockConstructionService(IBlockConstructionService blockConstructionService) {
            this.blockConstructionService = Objects.requireNonNull(blockConstructionService);
            return this;
        }

        public Builder setWinnerService(IWinnerService winnerService) {
            this.winnerService = Objects.requireNonNull(winnerService);
            return this;
        }

        public Builder setGamePlayer(IGamePlayer gamePlayer) {
            this.gamePlayer = Objects.requireNonNull(gamePlayer);
            return this;
        }

        public GameServices build() {
        	this.gameEvents = Objects.requireNonNullElse(gameEvents, new EventService());
        	this.gamePlayer = Objects.requireNonNullElse(gamePlayer, new GamePlayerService(game, plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG)));
        	this.questionManager = Objects.requireNonNullElse(questionManager, new QuestionService(plugin.getConfigManager(), gamePlayer));
            this.gameAnswer = Objects.requireNonNullElse(gameAnswer, new GameAnswerService(plugin.getConfigManager(), game, sqlManager));
            this.winnerService = Objects.requireNonNull(winnerService);
            this.blockConstructionService = Objects.requireNonNullElse(blockConstructionService, new BlockConstructionService(game, plugin, gamePlayer, winnerService));
        	
            return new GameServices(this);
        }
    }
}
