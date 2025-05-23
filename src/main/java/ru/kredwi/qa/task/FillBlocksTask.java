package ru.kredwi.qa.task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.callback.FillBlockCallback;
import ru.kredwi.qa.callback.PlaceBlockCallback;
import ru.kredwi.qa.entity.displaytext.DisplayText;
import ru.kredwi.qa.entity.displaytext.IDisplayText;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.BlockRemover;

public class FillBlocksTask extends BukkitRunnable {
	
	private final Plugin plugin;
	private final Location targetLocation;
	private final Vector perpendicular;
	private final Vector direction;
	private final IDisplayText displayText;
	private final char[] symbols;
	private final int wordLength;
	private boolean spawnTextDisplay;
	
	private PlayerState playerState;
	private Consumer<Void> buildFinalCallback;
	private Consumer<Location> placeBlockCallback;
	
	private int i = 0;

	public FillBlocksTask(QAPlugin plugin, Location targetLocation, Vector direction, IGame game, Player player, int wordLength, boolean spawnTextDisplay) {
	    this.targetLocation = targetLocation;
	    this.direction = direction;
	    this.wordLength = wordLength;
	    this.spawnTextDisplay = spawnTextDisplay;
	    this.plugin = plugin;
	    this.playerState = game.getPlayerState(player);
	    this.displayText = new DisplayText(plugin, playerState, spawnTextDisplay);
	    this.buildFinalCallback = new FillBlockCallback(plugin, game, player);
	    this.placeBlockCallback = new PlaceBlockCallback(plugin, player);
	    this.perpendicular = new Vector(-direction.getZ(), 0, direction.getX());
	    if (playerState.getSymbols() == null) {
	    	this.symbols = new char[] { ' ' };
	    } else {
	    	this.symbols = playerState.getSymbols();
	    }
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
		
		char symbol;
		if (i >= symbols.length) {
			symbol = ' ';
		} else {
			symbol = symbols[i];
		}

		targetLocation.add(direction);
		
		List<Location> blocksToUpdate = new ArrayList<>(3);
		
		blocksToUpdate.add(targetLocation.clone());
		blocksToUpdate.add(targetLocation.clone().add(perpendicular));
		blocksToUpdate.add(targetLocation.clone().subtract(perpendicular));
		
		setBlockBatch(blocksToUpdate, playerState);
		
		if (spawnTextDisplay) {
			blocksToUpdate.forEach(l ->
				displayText.createTextOnBlock(l.getBlock(), symbol, targetLocation));
		}
		
		placeBlockCallback.accept(targetLocation.clone());
		
		i++;
	}
	
	private void setBlockBatch(List<Location> locations, PlayerState playerState) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			BlockData blockData = playerState.getBlockData();
			for (Location location : locations) {
				
				Block block = location.getBlock();
				playerState.addPlayerBuildedBlocks(new BlockRemover(block));
				block.setBlockData(blockData, false);	
				
			}
		});
	}
	
	public void setBuildFinalCallback(Consumer<Void> callback) {
		this.buildFinalCallback = callback;
	}
	public void setPlaceBlockCallback(Consumer<Location> callback) {
		this.placeBlockCallback = callback;
	}
}
