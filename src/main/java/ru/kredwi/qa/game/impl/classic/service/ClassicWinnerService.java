package ru.kredwi.qa.game.impl.classic.service;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.IBlockConstructionService;
import ru.kredwi.qa.game.service.impl.WinnerService;
import ru.kredwi.qa.sql.DatabaseActions;

public class ClassicWinnerService extends WinnerService{

	private int blocksToWin;
	
	public ClassicWinnerService(int blocksToWin, PluginWrapper plugin, IGame game, DatabaseActions databaseActions){
		super(plugin, game, databaseActions);
		this.blocksToWin = blocksToWin;
	}

	@Override
	public boolean isPlayerWin(PlayerState state) {
		return (state.getBuildedBlocks()
						- IBlockConstructionService.COUNT_OF_INIT_BLOCKS)
				>= blocksToWin;
	}

}
