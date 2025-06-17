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
import ru.kredwi.qa.callback.BlockBreakDeniedCallback;
import ru.kredwi.qa.callback.BlockPlacementCallback;
import ru.kredwi.qa.callback.data.BreakIsBlockedData;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.entity.displaytext.DisplayText;
import ru.kredwi.qa.entity.displaytext.IDisplayText;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.impl.pleonasms.callback.ConstructionStageEndCallback;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.BlockRemover;
import ru.kredwi.qa.utils.Pair;

/**
 * Fill block timer task
 * 
 * TODO init layer is more if `isInit == true`
 * */
public class FillBlocksTask extends BukkitRunnable {
	
	private final Plugin plugin;
	private final Location targetLocation;
	private final Location saveLocation;
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
	private Consumer<Pair<Location, Location>> buildFinalCallback;
	private Consumer<Location> placeBlockCallback;
	
	private int i = 0; // index of current iteration
	
	private FillBlocksTask(Builder builder) {
		Objects.requireNonNull(builder);
		
		this.spawnTextDisplay = Objects.requireNonNull(builder.spawnTextDisplay);
		this.displayText = Objects.requireNonNull(builder.displayText);
		this.buildFinalCallback = Objects.requireNonNull(builder.fillBlockCallback);
		this.placeBlockCallback = Objects.requireNonNull(builder.placeBlockCallback);
		this.breakIsBlockedCallback = Objects.requireNonNull(builder.breakIsBlockedCallback);
		this.plugin = Objects.requireNonNull(builder.plugin);
		this.targetLocation = Objects.requireNonNull(builder.targetLocation);
		this.saveLocation = Objects.requireNonNull(builder.targetLocation).clone();
		this.playerState = Objects.requireNonNull(builder.playerState);
		this.wordLength = Objects.requireNonNull(builder.wordLength);
		this.direction = Objects.requireNonNull(builder.direction);
		
	    this.perpendicular = new Vector(-direction.getZ(), 0, direction.getX());
	    this.allowDestroyBlock = builder.plugin.getConfigManager().getAsBoolean(ConfigKeys.ALLOW_DESTROY_ANY_BLOCK);
	    this.debug = builder.plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG);
	    
	    if (Objects.isNull(playerState.getSymbols()) || playerState.getSymbols().length == 0) {
	    	this.symbols = new char[] { ' ' };
	    } else {
	    	this.symbols = playerState.getSymbols();
	    }

	}
	
	private char getDisplaySymbol(char[] symbols, int index) {
		return index >= symbols.length ? ' ' : symbols[index];
	}
	
	@Override
	public void run() {
		if (i >= Integer.MAX_VALUE | i >= wordLength) {
			Bukkit.getScheduler().runTask(plugin, () -> {
				buildFinalCallback.accept(new Pair<Location, Location>(targetLocation, saveLocation));
			});
			cancel();
			return;
		}

		saveLocation.add(direction);
		
		List<Location> blocksToUpdate = new ArrayList<>(3);
		
		blocksToUpdate.add(saveLocation.clone());
		blocksToUpdate.add(saveLocation.clone().add(perpendicular));
		blocksToUpdate.add(saveLocation.clone().subtract(perpendicular));
		
		 setBlockBatch(blocksToUpdate, playerState, (pairs) -> {
			 if (spawnTextDisplay) {
				 final int index = i; // copy i
				 pairs.forEach((pair) -> Bukkit.getScheduler()
						 .runTaskLater(plugin, () -> {
							 displayText.createTextOnBlock(pair.second(),
										getDisplaySymbol(symbols, index), saveLocation.clone()); 
						 }, 1));
			 }
			
			placeBlockCallback.accept(saveLocation.clone());
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
			Consumer<List<Pair<Location, BlockRemover>>> callback) {
		
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
						QAPlugin.getQALogger().info("Cancel this task timer and execute final callback...");							
					}
					Bukkit.getScheduler().runTask(plugin, () -> {
						buildFinalCallback.accept(null);
					});
					task.cancel();
					return;
				}
				
			}
			
			newLocations.add(location);
		}
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			List<Pair<Location, BlockRemover>> removers = new ArrayList<>();
			for (Location loc : newLocations) {
				
				Pair<Location, BlockRemover> remover = new Pair<>(loc, new BlockRemover(loc.getBlock()));
				
				removers.add(remover);
				
				playerState.addPlayerBuildedBlocks(remover.second());
				
				loc.getBlock().setBlockData(blockData, false);
			}		
			callback.accept(removers);
		});
	}
	
    public static class Builder {
    	private boolean spawnTextDisplay;
        private DisplayText displayText;
        private Consumer<Pair<Location, Location>> fillBlockCallback;
        private Consumer<Location> placeBlockCallback;
        private Predicate<BreakIsBlockedData> breakIsBlockedCallback;
        private final PluginWrapper plugin;
        private final Location targetLocation;
        private final Vector direction;
        private final PlayerState playerState;
        private final Player player;
        private final IGame game;
        private final int wordLength;

        public Builder(PluginWrapper plugin, Location targetLocation, Vector direction, PlayerState playerState, Player player, IGame game, int wordLength) {
            this.plugin = plugin;
            this.targetLocation = targetLocation;
            this.direction = direction;
            this.playerState = playerState;
            this.player = player;
            this.game = game;
            this.wordLength = wordLength;
        }

        public Builder spawnDisplays(boolean spawn) {
            this.spawnTextDisplay = spawn;
            return this;
        }
        
        public Builder displayText(DisplayText displayText) {
            this.displayText = Objects.requireNonNull(displayText);
            return this;
        }

        public Builder fillBlockCallback(Consumer<Pair<Location, Location>> callback) {
            this.fillBlockCallback = Objects.requireNonNull(callback);
            return this;
        }

        public Builder placeBlockCallback(Consumer<Location> placeBlockCallback) {
            this.placeBlockCallback = Objects.requireNonNull(placeBlockCallback);
            return this;
        }

        public Builder breakIsBlockedCallback(Predicate<BreakIsBlockedData> breakIsBlockedCallback) {
            this.breakIsBlockedCallback = Objects.requireNonNull(breakIsBlockedCallback);
            return this;
        }
        
        public FillBlocksTask build() {
        	this.fillBlockCallback = Objects.requireNonNullElse(fillBlockCallback, new ConstructionStageEndCallback(plugin, game, player));
        	this.placeBlockCallback = Objects.requireNonNullElse(placeBlockCallback, new BlockPlacementCallback(plugin, player, playerState));
        	this.breakIsBlockedCallback = Objects.requireNonNullElse(breakIsBlockedCallback, new BlockBreakDeniedCallback(plugin, game));
        	this.displayText = Objects.requireNonNullElse(displayText, new DisplayText(plugin, spawnTextDisplay));
        	
        	return new FillBlocksTask(this);
        }
    }
}
