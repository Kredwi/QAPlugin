package ru.kredwi.qa.game.service;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameAnswer;
import ru.kredwi.qa.game.state.PlayerState;
import ru.kredwi.qa.task.FillBlocksTask;

public class GameAnswerService implements IGameAnswer{

	private QAPlugin plugin;
	private IGame game;
	
	private int acceptCount = 0;
	
	public GameAnswerService(IGame game, QAPlugin plugin) {
		this.game = game;
		this.plugin = plugin;
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
		return acceptCount >= game.getPlayers().size();
	}

	@Override
	public void processPlayerAnswers(boolean isInit) {
		for (Map.Entry<Player, PlayerState> playerState : game.getPlayerAndStatesArray()) {
			
			if (game.buildIsStopped()) {
				return;
			}
			
			if (playerState.getValue() == null) {
				game.getGameInfo().owner().sendMessage(QAConfig.IN_THE_GAME_NOT_FOUND_PATHS.getAsString());
				return;
			}
			
			Player player = playerState.getKey();
			PlayerState state = playerState.getValue();
			
			int buildBlock = neededBlockToMax(state.getAnswerCount(), state.getBuildedBlocks(), true);

			state.addBuildedBlock(buildBlock);
			resetAnwserCount();
			
			game.getBuildedTasks().add(new FillBlocksTask(plugin, state.getLocaton(), getDirection(state.getLocaton()), game, player, buildBlock, isInit)
				.runTaskTimerAsynchronously(plugin, QAConfig.BUILD_DELAY.getAsInt(), QAConfig.BUILD_PERIOD.getAsInt()));
		}
	}
	
	private Vector getDirection(Location targetLocation) {
		Vector direction = targetLocation.getDirection().normalize();
		direction.setY(0);
		
		if (Math.abs(direction.getX()) > Math.abs(direction.getZ())) {
			return new Vector(Math.signum(direction.getX()), 0, 0);
		} else {
			return new Vector(0, 0, Math.signum(direction.getZ()));
		}
	}
	
	private int neededBlockToMax(int length, int buildedBlocks, boolean add) {
		
		int remainingBlocks = (game.getGameInfo().blocksToWin() - (buildedBlocks - (add ? IBlockConstructionService.COUNT_OF_INIT_BLOCKS : 0)));
		
		if (remainingBlocks <= 0) return 0;
		
		return Math.min(length, remainingBlocks);
	}

}
