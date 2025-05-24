package ru.kredwi.qa.game.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;
import ru.kredwi.qa.task.FillBlocksTask;

public class BlockConstructionService implements IBlockConstructionService{

	private List<BukkitTask> buildTasks = new LinkedList<>();
	
	private QAPlugin plugin;
	
	private int buildCompleted =1;
	
	private BlockData[] sequenceBlockData;
	private boolean stopBuild;
	
	private IGame game;
	
	public BlockConstructionService(IGame game, QAPlugin plugin) {
		this.game = game;
		this.plugin = plugin;
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> 
			this.sequenceBlockData = loadBlockData().toArray(new BlockData[0]));
	}
	
	@Override
	public void scheduleBuildForPlayer(Player player, PlayerState state, boolean isInit) {
		
		int buildBlock = neededBlockToMax(state.getAnswerCount(), state.getBuildedBlocks(), true);

		state.addBuildedBlock(buildBlock);
		
		FillBlocksTask fbt = new FillBlocksTask(plugin, state.getLocaton(),
				getDirection(state.getLocaton()), game, player, buildBlock,
				!isInit ? QAConfig.SPAWN_DISPLAY_TEXTS.getAsBoolean() : isInit);
		
		getBuildedTasks().add(fbt
			.runTaskTimerAsynchronously(plugin,
					QAConfig.BUILD_DELAY.getAsInt(),
					QAConfig.BUILD_PERIOD.getAsInt()));
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
		
		int remainingBlocks = (game.getGameInfo().blocksToWin() - (buildedBlocks - (add
				? COUNT_OF_INIT_BLOCKS : 0)));
		
		if (remainingBlocks <= 0) return 0;
		
		return Math.min(length, remainingBlocks);
	}
	
	private List<BlockData> loadBlockData() {
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
		return blockDataList;
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
		
		for (PlayerState state : game.getStates()) {
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
		return buildTasks;
	}


	@Override
	public boolean buildIsStopped() {
		return stopBuild;
	}


	@Override
	public void setStopBuild(boolean isStop) {
		this.stopBuild = isStop;
	}
}
