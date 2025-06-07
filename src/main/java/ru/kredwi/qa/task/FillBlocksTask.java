package ru.kredwi.qa.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.callback.BlockPlacementCallback;
import ru.kredwi.qa.callback.ConstructionStageEndCallback;
import ru.kredwi.qa.callback.data.BreakIsBlockedData;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.entity.displaytext.DisplayText;
import ru.kredwi.qa.entity.displaytext.IDisplayText;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.BlockRemover;

/**
 * Fill block timer task
 * */
public class FillBlocksTask extends BukkitRunnable {
	
	private final Plugin plugin;
	private final Location targetLocation;
	private final Vector perpendicular;
	private final Vector direction;
	private final IDisplayText displayText;
	private final char[] symbols;
	private final int wordLength;
	
	private boolean debug;
	private boolean spawnTextDisplay;
	private boolean allowDestroyBlock;
	
	private PlayerState playerState;
	
	private Predicate<BreakIsBlockedData> breakIsBlockedCallback;
	private Consumer<Void> buildFinalCallback;
	private Consumer<Location> placeBlockCallback;
	
	private int i = 0;
	
	public FillBlocksTask(PluginWrapper plugin, IMainGame gameManager, Location targetLocation,
			Vector direction, IGame game, Player player, 
			int wordLength, boolean spawnTextDisplay, Predicate<BreakIsBlockedData> breakIsBlockedCallback) {
		
		this(plugin, targetLocation, direction,
				game.getPlayerService().getPlayerState(player), player,
				wordLength, spawnTextDisplay,
				new DisplayText(plugin, game.getPlayerService().getPlayerState(player),spawnTextDisplay),
				new ConstructionStageEndCallback(plugin, gameManager, game, player),
				new BlockPlacementCallback(plugin, player),
				breakIsBlockedCallback);
	}

	public FillBlocksTask(PluginWrapper plugin, Location targetLocation,
			Vector direction, PlayerState playerState, Player player, 
			int wordLength, boolean spawnTextDisplay,
			DisplayText displayText, ConstructionStageEndCallback fillBlockCallback,
			BlockPlacementCallback placeBlockCallback, Predicate<BreakIsBlockedData> breakIsBlockedCallback) {
		
	    this.targetLocation = targetLocation;
	    this.direction = direction;
	    this.wordLength = wordLength;
	    this.spawnTextDisplay = spawnTextDisplay;
	    this.plugin = plugin;
	    this.playerState = playerState;
	    this.displayText = displayText;
	    this.buildFinalCallback = fillBlockCallback;
	    this.placeBlockCallback = placeBlockCallback;
	    this.breakIsBlockedCallback =  breakIsBlockedCallback;
	    this.perpendicular = new Vector(-direction.getZ(), 0, direction.getX());
	    this.allowDestroyBlock = plugin.getConfigManager().getAsBoolean(ConfigKeys.ALLOW_DESTROY_ANY_BLOCK);
	    this.debug = plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG);
	    
	    if (Objects.isNull(playerState.getSymbols()) || playerState.getSymbols().length == 0) {
	    	this.symbols = new char[] { ' ' };
	    } else {
	    	this.symbols = playerState.getSymbols();
	    }
	}
	
	private char getDisplaySymbol(char[] symbols) {
		return i >= symbols.length ? ' ' : symbols[i];
	}
	
	@Override
	public void run() {
		if (i >= Integer.MAX_VALUE | i >= wordLength) {
			Bukkit.getScheduler().runTask(plugin, () -> {
				buildFinalCallback.accept(null);
			});
			cancel();
			return;
		}

		targetLocation.add(direction);
		
		List<Location> blocksToUpdate = new ArrayList<>(3);
		
		blocksToUpdate.add(targetLocation.clone());
		blocksToUpdate.add(targetLocation.clone().add(perpendicular));
		blocksToUpdate.add(targetLocation.clone().subtract(perpendicular));
		
		 setBlockBatch(blocksToUpdate, playerState, (locations) -> {
			 if (spawnTextDisplay) {
				locations.forEach(loc ->
					displayText.createTextOnBlock(loc.getBlock(),
							getDisplaySymbol(symbols), targetLocation));
			}
			
			placeBlockCallback.accept(targetLocation.clone());
			i++;
		});
	}
	
	/**
	 * Attempts to set blocks at the specified locations on the server's main thread,
	 * then calls the callback with the locations where blocks were successfully set.
	 *
	 * @param locations the list of locations where blocks should be placed
	 * @param playerState the player's state containing block data and other relevant information
	 * @param callback a callback function invoked after blocks are set,
	 *                 receiving the list of locations where blocks were placed
	 * @author Kredwi
	 */
	private void setBlockBatch(List<Location> locations, PlayerState playerState,
			Consumer<List<Location>> callback) {
		
		BukkitRunnable task = FillBlocksTask.this;
		List<Location> newLocations = new ArrayList<>();
		BlockData blockData = playerState.getBlockData();
			
		for (Location location : locations) {
			
			Block block = location.getBlock();
				
			if (!allowDestroyBlock && !block.getType().equals(Material.AIR)) {
				
				if (debug) {
					QAPlugin.getQALogger().info("Allow destroy block is false. Checking next actions...");							
				}
				
				boolean callbackPredicate = breakIsBlockedCallback
						.test(new BreakIsBlockedData(block, location));
				
				if (callbackPredicate) {
					if (debug) {
						QAPlugin.getQALogger().info("Skip block...");							
					}
					continue;
				} else {
					if (debug) {
						QAPlugin.getQALogger().info("Stop game...");							
					}
					task.cancel();
					return;
				}
				
			}
			
			newLocations.add(location);
		}
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			for (Location loc : newLocations) {
				playerState.addPlayerBuildedBlocks(new BlockRemover(loc.getBlock()));
				loc.getBlock().setBlockData(blockData, false);
			}
			callback.accept(newLocations);
		});
	}
}
