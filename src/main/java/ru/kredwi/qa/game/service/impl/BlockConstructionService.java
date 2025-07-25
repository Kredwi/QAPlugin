package ru.kredwi.qa.game.service.impl;

import static ru.kredwi.qa.config.ConfigKeys.BUILD_DELAY;
import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.ENABLED_BLOCKS;
import static ru.kredwi.qa.config.ConfigKeys.SPAWN_DISPLAY_TEXTS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.callback.BlockBreakDeniedCallback;
import ru.kredwi.qa.callback.data.BreakIsBlockedData;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.PlayerDontHaveLayersException;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.IBlockConstructionService;
import ru.kredwi.qa.game.service.IGamePlayer;
import ru.kredwi.qa.game.service.IWinnerService;
import ru.kredwi.qa.removers.IRemover;
import ru.kredwi.qa.task.FillBlocksTask;
import ru.kredwi.qa.utils.LocationUtils;

public class BlockConstructionService implements IBlockConstructionService{

	private List<FillBlocksTask> buildTasks = new LinkedList<>();
	private Map<UUID, List<IRemover>> globalRemovers = new HashMap<>();
	
	private PluginWrapper plugin;
	private QAConfig cm;
	protected Predicate<BreakIsBlockedData> breakDeniedCallback;
	
	private int buildCompleted =1;
	
	private BlockData[] sequenceBlockData;
	private boolean serviceReady = false;
	private boolean stopBuild;
	
	private IGame game;
	
	public BlockConstructionService(IGame game, PluginWrapper plugin, IGamePlayer gamePlayer, IWinnerService winnerService) {
		this.game = game;
		this.plugin = plugin;
		this.cm = plugin.getConfigManager();
		this.breakDeniedCallback = new BlockBreakDeniedCallback(plugin, game);
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> 
			this.sequenceBlockData = loadBlockData().toArray(new BlockData[0]));
	}
	
	protected void nextScheduleBuildForPlayer(FillBlocksTask.Builder fbtBuilder, Player player, PlayerState state, boolean isInit) {
		fbtBuilder.spawnDisplays(!isInit 
				? cm.getAsBoolean(SPAWN_DISPLAY_TEXTS)
				: isInit);
		
		
		FillBlocksTask fbt = fbtBuilder.build();
		
		Executor delayedExecutor= getDelayedExecutor();
		CompletableFuture.runAsync(fbt, delayedExecutor);
		buildTasks.add(fbt);
		
	}
	
	protected FillBlocksTask.Builder getFillBlockBuilder(Player player, PlayerState state, int buildBlock) {
		Vector direction = LocationUtils.horizontalizeDirection(state.getLocaton());

		if (cm.getAsBoolean(DEBUG)) {
			QAPlugin.getQALogger().info("[FillBlockBuilder] Count of path layer " + buildBlock);
		}
		
		return new FillBlocksTask.Builder(plugin, state.getLocaton(), direction,
				state, player, game, buildBlock);
	}
	
	private List<BlockData> loadBlockData() {
		List<String> enabledBlocks = cm.getAsStringList(ENABLED_BLOCKS);
		List<BlockData> blockDataList = new ArrayList<>();
		for (String blockName : enabledBlocks) {
			try {
				
				Material material = Material.valueOf(blockName);
				
				blockDataList.add(material.createBlockData());
				if (cm.getAsBoolean(DEBUG)) {
					QAPlugin.getQALogger().info(material.name() + " added to pool");
				}
			} catch (IllegalArgumentException e) {
				blockDataList.add(Material.BLACK_CONCRETE.createBlockData());
				QAPlugin.getQALogger().info(blockName + " not found in Bukkit API. Used default BLACK_CONCRETE");
			}
		}
		this.serviceReady = true;
		return blockDataList;
	}
	
	@Override
	public void scheduleBuildForPlayer(Player player, PlayerState state, boolean isInit) {
		int buildBlock = state.getAnswerCount();
		
		FillBlocksTask.Builder fbtBuilder = getFillBlockBuilder(player, state, buildBlock)
				.breakIsBlockedCallback(breakDeniedCallback);
		nextScheduleBuildForPlayer(fbtBuilder, player, state, isInit);
	}
	
	/**
	 * Cancel and delete active tasks and remove builded blocks<br>
	 * <b>DONT USE IN ASYNC BLOCKS DONT CHANGED IF THREAD IS NOT MAIN !!!</b>
	 * @author Kredwi
	 * */
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
		
		for (PlayerState state : game.getPlayerService().getStates()) {
			blocks.addAll(state.getPlayerBuildedBlocks());
		}
		
		for (List<IRemover> removers : globalRemovers.values()) {
			blocks.addAll(removers);
		}
		
		return blocks;
	}

	@Override
	public void deletePathLayer(@Nonnull Collection<IRemover> path, PlayerState playerState, int deleteBlock) throws PlayerDontHaveLayersException {
		int delete = deleteBlock * (COUNT_OF_INIT_BLOCKS + 1);
		
		if (path.isEmpty() || path.size() < delete) {
			throw new PlayerDontHaveLayersException("Player dont have needs layers");
		}
		if (playerState != null) {
			playerState.removeBuildedBlock(delete);
		}
		
		Iterator<IRemover> iterator = path.iterator();
		for (int i =0; i < delete && iterator.hasNext(); i++) {
			IRemover remover = iterator.next();
			remover.remove();
			iterator.remove();
		}
	}
	
	@Override
	public BlockData getRandomBlockData() {
		if (sequenceBlockData == null) {
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().warning("sequenceBlockData IS NOT INITILIZE. Used default BLACK_CONCRETE.");
			}
			return Material.BLACK_CONCRETE.createBlockData();
		}
		return sequenceBlockData[QAPlugin.RANDOM.nextInt(sequenceBlockData.length)];
	}
	
	@Override
	public Executor getDelayedExecutor() {
		return CompletableFuture.delayedExecutor(cm.getAsInt(BUILD_DELAY), TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void addGlobalRemovers(UUID uuid, List<IRemover> removers) {
		Objects.requireNonNull(uuid);
		Objects.requireNonNull(removers);
		
		globalRemovers.put(uuid, removers);
	}

	/**
	 * Getting global remover
	 * 
	 * @param uuid of player
	 * @return list of removers or empty list if remover dont found
	 * @author Kredwi
	 * */
	@Override
	public List<IRemover> getGlobalRemovers(UUID uuid) {
		return globalRemovers.getOrDefault(uuid, Collections.emptyList());
	}
	
	@Override
	public void removeGlobalRemovers(UUID uuid) {
		Objects.requireNonNull(uuid);
		globalRemovers.remove(uuid);
	}
	
	@Override
	public List<UUID> getGlobalPlayersRemovers() {
		return new ArrayList<>(globalRemovers.keySet());
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
	public List<FillBlocksTask> getBuildedTasks() {
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

	@Override
	public boolean isServiceReady() {
		return serviceReady;
	}
}
