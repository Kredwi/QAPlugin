package ru.kredwi.qa.callback;

import static ru.kredwi.qa.config.ConfigKeys.INDESTRUCTIBLE_BLOCK_ENCOUNTERED;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.callback.data.BreakIsBlockedData;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.game.ActionsBlockBuilded;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.impl.GameInfo;
import ru.kredwi.qa.game.player.PlayerState;

/**
* TODO conccurentmodificationexception error if action is "END_GAME"
* TODO dont tested
*/
public class BlockBreakDeniedCallback implements Predicate<BreakIsBlockedData>{

	private boolean isGameEnd = false;
	
	private PluginWrapper plugin;
	private IMainGame mainGame;
	
	private IGamePlayer gamePlayer;
	private IWinnerService winnerService;
	private GameInfo gameInfo;

	public BlockBreakDeniedCallback(PluginWrapper plugin, IMainGame mainGame, GameInfo gameInfo, IGamePlayer gamePlayer, IWinnerService winnerService) {
		this.gameInfo = gameInfo;
		this.gamePlayer = gamePlayer;
		this.winnerService = winnerService;
		this.mainGame = mainGame;
		this.plugin = plugin;
	}
	
	@Override
	public boolean test(BreakIsBlockedData event) {
		try {
			String actionBlockBuildedName = plugin.getConfigManager().getAsString(INDESTRUCTIBLE_BLOCK_ENCOUNTERED);
			ActionsBlockBuilded actionBlockBuilded = ActionsBlockBuilded.valueOf(actionBlockBuildedName);
			return switch (actionBlockBuilded) {
				case END_GAME -> endGame();
				case IGNORE -> ignoreIteration();
			};
			
		} catch (IllegalArgumentException|NullPointerException|ConcurrentModificationException e) {
			if (plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG)) {
				QAPlugin.getQALogger().warning("ERROR IN BLOCK BREAK DENIED CALLBACK !!!");
				e.printStackTrace();
			}
			return ignoreIteration();
		}
	}
	
	private synchronized boolean endGame() {
		
		if (isGameEnd) return false;
		
		this.isGameEnd = true;
		
		Optional<Integer> maxBuildedBlocks = gamePlayer.getPlayerAndStatesArray().stream()
			.map(e -> e.getValue().getBuildedBlocks())
			.max(Integer::compare);
		
		for (Map.Entry<Player, PlayerState> s : gamePlayer.getPlayerAndStatesArray()) {
			if (maxBuildedBlocks.isPresent() && s.getValue().getBuildedBlocks() >= maxBuildedBlocks.get()) {
				winnerService.addWinner(s.getKey());
			}
		}

		winnerService.alertOfPlayersWin();
		
		mainGame.removeGameWithName(gameInfo.name());
		
		return false;
	}
	private boolean ignoreIteration() {
		return true;
	}

}
