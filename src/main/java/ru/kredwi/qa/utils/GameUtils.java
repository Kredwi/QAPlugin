package ru.kredwi.qa.utils;

import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;

public class GameUtils {

	public static final void setWinnerWhyManyBuilded(IGame game) {
		Optional<Integer> maxBuildedBlocks = game.getPlayerService().getPlayerAndStatesArray().stream()
				.map(e -> e.getValue().getBuildedBlocks())
				.max(Integer::compare);
			
		for (Map.Entry<Player, PlayerState> s : game.getPlayerService().getPlayerAndStatesArray()) {
			if (maxBuildedBlocks.isPresent() && s.getValue().getBuildedBlocks() >= maxBuildedBlocks.get()) {
				game.getWinnerService().addWinner(s.getKey());
			}
		}
	}

}
