package ru.kredwi.qa.game.impl.classic.service;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.WinnerService;
import ru.kredwi.qa.sql.DatabaseActions;

public class ClassicWinnerService extends WinnerService{

	private int blocksToWin;
	
	public ClassicWinnerService(int blocksToWin, QAConfig cm, IGame game, DatabaseActions databaseActions) {
		super(cm, game, databaseActions);
		this.blocksToWin = blocksToWin;
	}

	@Override
	public boolean isPlayerWin(PlayerState state) {
		return (state.getBuildedBlocks()
						- IBlockConstructionService.COUNT_OF_INIT_BLOCKS)
				>= blocksToWin;
	}

}
