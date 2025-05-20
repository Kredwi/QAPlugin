package ru.kredwi.qa.game;

import java.util.List;
import java.util.Set;

import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitTask;

import ru.kredwi.qa.removers.IRemover;

public interface IBlockConstructionService {
	
	int COUNT_OF_INIT_BLOCKS = 2;
	
	public int getBuildComplete();
	public void addBuildComplete();
	public void resetBuildComplete();
	public void deleteBuildedBlocks();
	
	public Set<IRemover> getSummaryBuildedBlocks();
	
	List<BukkitTask> getBuildedTasks();
	
	
	BlockData getRandomBlockData();
	
	boolean buildIsStopped();
	void setStopBuild(boolean isStop);
}
