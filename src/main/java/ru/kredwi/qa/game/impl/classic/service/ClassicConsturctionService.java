package ru.kredwi.qa.game.impl.classic.service;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.classic.IClassicGame;
import ru.kredwi.qa.game.impl.classic.callback.ConstructionStageEndCallback;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.BlockConstructionService;
import ru.kredwi.qa.task.FillBlocksTask;

public class ClassicConsturctionService extends BlockConstructionService{

	private IClassicGame game;
	private PluginWrapper plugin;
	
	public ClassicConsturctionService(IClassicGame game, PluginWrapper plugin, IGamePlayer gamePlayer,
			IWinnerService winnerService) {
		super(game, plugin, gamePlayer, winnerService);
		this.game = game;
		this.plugin = plugin;
	}

	@Override
	public void scheduleBuildForPlayer(Player player, PlayerState state, boolean isInit) {
		int buildBlock = neededBlockToMax(state.getAnswerCount(), state.getBuildedBlocks(), true);
		
		FillBlocksTask.Builder fbtBuilder = getFillBlockBuilder(player, state, buildBlock)
				.fillBlockCallback(new ConstructionStageEndCallback(plugin, game, player))
				.breakIsBlockedCallback(breakDeniedCallback);
		
		nextScheduleBuildForPlayer(fbtBuilder, player, state, isInit);
	}
	
	private int neededBlockToMax(int length, int buildedBlocks, boolean add) {
		int remainingBlocks = (game.getBlockToWin() - (buildedBlocks - (add
				? COUNT_OF_INIT_BLOCKS : 0)));
		
		if (remainingBlocks <= 0) return 0;
		
		return Math.min(length, remainingBlocks);
	}

}
