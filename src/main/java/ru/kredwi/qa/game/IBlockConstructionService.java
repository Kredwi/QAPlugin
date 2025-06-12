package ru.kredwi.qa.game;

import java.util.List;
import java.util.Set;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ru.kredwi.qa.exceptions.PlayerDontHaveLayersException;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;

public interface IBlockConstructionService extends ServiceReader {
	
	/**
	 * This is the number of blocks added to the total number of required blocks during initialization. <br>
	 * This is necessary for the correct calculation of the required number of blocks. <br>
	 * The number 2 is used because perpendiculars are created. <br>
	 * See {@link ru.kredwi.qa.commands.creator.StartGame} for more details about game initialization. <br>
	 * (counts of perpediculars blocks)
	 * @author Kredwi
	 */
	int COUNT_OF_INIT_BLOCKS = 2;
	
	public int getBuildComplete();
	public void addBuildComplete();
	public void resetBuildComplete();
	public void deleteBuildedBlocks();
	public void deletePathLayer(PlayerState playerState, int deleteBlock) throws PlayerDontHaveLayersException;
	
	public void scheduleBuildForPlayer(Player player, PlayerState state, boolean isInit);
	public Set<IRemover> getSummaryBuildedBlocks();
	
	List<BukkitTask> getBuildedTasks();
	
	
	BlockData getRandomBlockData();
	
	boolean buildIsStopped();
	void setStopBuild(boolean isStop);
}
