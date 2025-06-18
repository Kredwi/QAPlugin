package ru.kredwi.qa.game.impl.pleonasms.service;

import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.EventService;

public class PleonasmsEventService extends EventService{

	private IGame game;
	
	public PleonasmsEventService(IGame game) {
		this.game= game;
	}
	
	@Override
	public void onDeadPlayer() {
		if (game.getPlayerService().getPlayers().size() == 1) {
			
			// from ru.kredwi.qa.callback.AbstractStageEndCallback
			// TODO create chain of resp.... for create filters
			Optional<Integer> maxBuildedBlocks = game.getPlayerService().getPlayerAndStatesArray().stream()
					.map(e -> e.getValue().getBuildedBlocks())
					.max(Integer::compare);
				
			for (Map.Entry<Player, PlayerState> s : game.getPlayerService().getPlayerAndStatesArray()) {
				if (maxBuildedBlocks.isPresent() && s.getValue().getBuildedBlocks() >= maxBuildedBlocks.get()) {
					game.getWinnerService().addWinner(s.getKey());
				}
			}
			
			game.getBlockConstruction().deleteBuildedBlocks();
			
			game.getWinnerService().executeWinnerHandler();
		}
	}

}
