package ru.kredwi.qa.game.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;

public class BlockConstructionService implements IBlockConstructionService{

	private List<BukkitTask> buildTasks = new LinkedList<>();
	
	private int buildCompleted =1;
	
	private BlockData[] sequenceBlockData;
	private boolean stopBuild;
	
	private IGamePlayer playersService;
	
	public BlockConstructionService(IGamePlayer playersService, Plugin plugin) {
		this.playersService = playersService;
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			List<String> enabledBlocks = QAConfig.ENABLED_BLOCKS.getAsStringList();
			List<BlockData> blockDataList = new ArrayList<>();
			
			for (String blockName : enabledBlocks) {
				try {
					
					Material material = Material.valueOf(blockName);
					
					blockDataList.add(material.createBlockData());
					if (QAConfig.DEBUG.getAsBoolean()) {
						QAPlugin.getQALogger().info(material.name() + " added to pool");
					}
				} catch (IllegalArgumentException e) {
					blockDataList.add(Material.BLACK_CONCRETE.createBlockData());
					QAPlugin.getQALogger().info(blockName + " not found in Bukkit API. Used default BLACK_CONCRETE");
				}
			}
			
			sequenceBlockData = blockDataList.toArray(new BlockData[0]);
		});
	}
	
	
	@Override
	public void deleteBuildedBlocks() {
		if (buildTasks != null && !buildTasks.isEmpty()) {
			buildTasks.removeIf((e) -> {
				if (!e.isCancelled())
					e.cancel();
				return true;
			});
			buildTasks.clear();
		}
		getSummaryBuildedBlocks().removeIf((e -> {
			e.remove();
			return true;
		}));
	}
	
	public Set<IRemover> getSummaryBuildedBlocks() {
		Set<IRemover> blocks = new HashSet<>();
		
		for (PlayerState state : playersService.getStates()) {
			blocks.addAll(state.getPlayerBuildedBlocks());
		}
		
		return blocks;
	}
	
	@Override
	public int getBuildComplete() {
		return buildCompleted;
	}
	
	@Override
	public void addBuildComplete() {
		buildCompleted++;
	}
	
	@Override
	public void resetBuildComplete() {
		buildCompleted = 1;
	}

	@Override
	public BlockData getRandomBlockData() {
		if (Objects.isNull(sequenceBlockData)) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().warning("sequenceBlockData IS NOT INITILIZE. Used default BLACK_CONCRETE.");
			}
			return Material.BLACK_CONCRETE.createBlockData();
		}
		return sequenceBlockData[QAPlugin.RANDOM.nextInt(sequenceBlockData.length)];
	}


	@Override
	public List<BukkitTask> getBuildedTasks() {
		// TODO Auto-generated method stub
		return buildTasks;
	}


	@Override
	public boolean buildIsStopped() {
		// TODO Auto-generated method stub
		return stopBuild;
	}


	@Override
	public void setStopBuild(boolean isStop) {
		// TODO Auto-generated method stub
		this.stopBuild = isStop;
	}
	
	

}
