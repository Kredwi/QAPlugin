package ru.kredwi.qa.game.impl.pleonasms.service;

import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.service.EventService;

public class PleonasmsEventService extends EventService{

	private IGame game;
	
	public PleonasmsEventService(IGame game) {
		this.game= game;
	}
	
	@Override
	public void onDeadPlayer() {
		if (game.getPlayerService().getPlayers().size() == 1) {
			game.getBlockConstruction().deleteBuildedBlocks();
			game.getWinnerService().executeWinnerHandler();
		}
	}

}
