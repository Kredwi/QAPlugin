package ru.kredwi.qa.game.service;

import static ru.kredwi.qa.config.ConfigKeys.DB_ENABLE;
import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.IN_THE_GAME_NOT_FOUND_PATHS;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameAnswer;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.sql.DatabaseActions;

public class GameAnswerService implements IGameAnswer{

	private DatabaseActions dbActions;
	private QAConfig cm;
	private IGame game;
	
	private int acceptCount = 0;
	
	public GameAnswerService(QAConfig cm, IGame game, DatabaseActions dbActions) {
		this.game = game;
		this.cm = cm;
		this.dbActions = dbActions;
	}

	@Override
	public void processPlayerAnswers(boolean isInit) {
		for (Map.Entry<Player, PlayerState> playerState : game.getPlayerService().getPlayerAndStatesArray()) {
			
			if (isInit && cm.getAsBoolean(DB_ENABLE)) {
				CompletableFuture.runAsync(() -> {
					dbActions.addPlayerIfNonExists(playerState.getKey().getUniqueId());
					dbActions.setPlayerLastPlayedNow(playerState.getKey().getUniqueId());
				});
			}
			
			if (game.getBlockConstruction().buildIsStopped()) {
				return;
			}
			
			if (Objects.isNull(playerState.getValue())) {
				Player owner = Bukkit.getPlayer(game.getGameInfo().ownerUUID());
				
				if (Objects.isNull(owner)) {
					if (cm.getAsBoolean(DEBUG)) 
						QAPlugin.getQALogger().warning("IN GAME"
								+ game.getGameInfo().name()
								+" OWNER IS OFFLINE");
					return;
				}
				
				owner.sendMessage(cm.getAsString(IN_THE_GAME_NOT_FOUND_PATHS));
				return;
			}
			
			resetAnwserCount();
			game.getBlockConstruction().scheduleBuildForPlayer(playerState.getKey(),playerState.getValue(), isInit);
		}
	}
	
	@Override
	public void addAnwserCount() {
		acceptCount++;
	}
	
	@Override
	public void resetAnwserCount() {
		acceptCount = 0;
	}
	
	@Override
	public boolean isAllAnswered() {
		return acceptCount >= game.getPlayerService().getPlayers().size();
	}

}
