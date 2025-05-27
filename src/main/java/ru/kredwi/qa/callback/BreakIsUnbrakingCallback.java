package ru.kredwi.qa.callback;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.bukkit.entity.Player;

import ru.kredwi.qa.callback.data.BreakIsBlockedData;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.ActionsBlockBuilded;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class BreakIsUnbrakingCallback implements Predicate<BreakIsBlockedData>{

	private IMainGame mainGame;
	private IGame game;

	public BreakIsUnbrakingCallback(IGame game, IMainGame mainGame) {
		this.game = game;
		this.mainGame = mainGame;
	}
	
	@Override
	public boolean test(BreakIsBlockedData event) {
		try {
			String actionBlockBuildedName = QAConfig.INDESTRUCTIBLE_BLOCK_ENCOUNTERED.getAsString();
			ActionsBlockBuilded actionBlockBuilded = ActionsBlockBuilded.valueOf(actionBlockBuildedName);
			
			return switch (actionBlockBuilded) {
				case END_GAME -> endGame();
				case IGNORE -> ignoreIteration();
			};
			
		} catch (IllegalArgumentException e) {
			return endGame();
		}
	}
	
	private boolean endGame() {
	
		Optional<Integer> maxBuildedBlocks = game.getPlayerAndStatesArray().stream()
			.map(e -> e.getValue().getBuildedBlocks())
			.max(Integer::compare);
		
		for (Map.Entry<Player, PlayerState> s : game.getPlayerAndStatesArray()) {
			if (s.getValue().getBuildedBlocks() >= maxBuildedBlocks.get()) {
				game.addWinner(s.getKey());
			}
		}
		
		game.alertOfPlayersWin();
		
		mainGame.removeGameWithName(game.getGameInfo().name());
		
		return false;
	}
	private boolean ignoreIteration() {
		return true;
	}

}
