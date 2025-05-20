package ru.kredwi.qa.task;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.callback.FillBlockCallback;
import ru.kredwi.qa.callback.ICallback;
import ru.kredwi.qa.callback.PlaceBlockCallback;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.state.PlayerState;
import ru.kredwi.qa.removers.BlockRemover;
import ru.kredwi.qa.removers.DisplayRemover;
import ru.kredwi.qa.utils.LocationUtils;

public class FillBlocksTask extends BukkitRunnable {
	
	private static final float DISPLAY_TEXT_X_SCALE = 5F;
	private static final float DISPLAY_TEXT_Y_SCALE = 3.6F;
	private static final float DISPLAY_TEXT_Z_SCALE = 5F;
	
	private static final Quaternionf INSTANCE_OF_QUATERNIONF = new Quaternionf();
	
	private static final Transformation DISPLAY_TEXT_TRANSFORMATION = new Transformation(
			new Vector3f(),
			INSTANCE_OF_QUATERNIONF,
			new Vector3f(DISPLAY_TEXT_X_SCALE, DISPLAY_TEXT_Y_SCALE, DISPLAY_TEXT_Z_SCALE),
			INSTANCE_OF_QUATERNIONF
			);
	
	private final Plugin plugin;
	private final Location targetLocation;
	private final Vector direction;
	private final char[] symbols;
	private final int wordLength;
	private final boolean isInit;
	private final Vector perpendicular;
	
	private PlayerState playerState;
	private ICallback buildFinalCallback;
	private ICallback placeBlockCallback;
	
	private int i = 0;
	
	public FillBlocksTask(QAPlugin plugin, Location targetLocation, Vector direction, IGame game, Player player, int wordLength) {
		this(plugin, targetLocation, direction, game, player, wordLength, false);
	}
	
	public FillBlocksTask(QAPlugin plugin, Location targetLocation, Vector direction, IGame game, Player player, int wordLength, boolean isInit) {
	    this.targetLocation = targetLocation;
	    this.direction = direction;
	    this.wordLength = wordLength;
	    this.plugin = plugin;
	    this.isInit = isInit;
	    this.playerState = game.getPlayerState(player);
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
				buildFinalCallback.run(null);
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
		
		Location centerLocation = targetLocation.clone();

		Location rightLocation = centerLocation.clone().add(perpendicular);
		
		Location leftLocation = centerLocation.clone().subtract(perpendicular);
		
		setBlock(centerLocation, playerState);
		createTextOnBlock(centerLocation.getBlock(), symbol, targetLocation);
		
		setBlock(rightLocation, playerState);
		createTextOnBlock(rightLocation.getBlock(), symbol, targetLocation);
		
		setBlock(leftLocation, playerState);
		createTextOnBlock(leftLocation.getBlock(), symbol, targetLocation);
		
		placeBlockCallback.run(centerLocation);
		
		i++;
	}
	
	public void createTextOnBlock(Block block, char symbol, Location targetLocation) {
		
		// if game in init state dont create displays on blocks
		if (isInit) {
			return;
		}
		
		Location newTargetLocation = targetLocation.clone();
		
		newTargetLocation.setYaw(-targetLocation.getYaw());
		
		World world = block.getWorld();
		Location blockLocation = block.getLocation().clone();
		
		final float x = 0.435F;
		
		createTextDisplay(world, blockLocation.clone().add(x, 0, 1.0 + 0.001), symbol, 0F/2, 0F);
		createTextDisplay(world, blockLocation.clone().add(x + 0.2, 0, -0.001), symbol, 180F/1, 0F);
		createTextDisplay(world, blockLocation.clone().add(1.0+0.001, 0, x + 0.2), symbol, 90F*3, 0F);
		createTextDisplay(world, blockLocation.clone().add(-0.001, 0, x), symbol, 270F*3, 0F);
		
		float faceYaw = LocationUtils.getInstance().centerLocation(newTargetLocation, true).getYaw();
		
		Location headDisplayText = blockLocation.clone().add(0, 1.01, 0);
		
		switch ((int) faceYaw) {
			case 0 -> {
				headDisplayText.add(0.6,0,0);
				faceYaw = 180;
				break;
			}
			case -90 -> headDisplayText.add(1,0,0.6);
			case 180, -180 -> {
				headDisplayText.add(0.4,0,1);
				faceYaw = 0;
				break;
			}
			case 90 -> headDisplayText.add(0,0,0.4);
		}
		
		createTextDisplay(world, headDisplayText, symbol, faceYaw, -90F);
	}
	
	@SuppressWarnings("deprecation")
	private void createTextDisplay(World world, Location location, char symbol, float rotationYaw, float rotationPith) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			TextDisplay textDisplay = world.spawn(location, TextDisplay.class);
			
			textDisplay.setTransformation(DISPLAY_TEXT_TRANSFORMATION);
			
			textDisplay.setBillboard(Billboard.FIXED);
			
			textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
			
			textDisplay.setText(String.valueOf(symbol));
			
			textDisplay.setRotation(rotationYaw, rotationPith);
			textDisplay.setShadowed(true);
			playerState.addPlayerBuildedBlocks(new DisplayRemover(textDisplay));
		});
	}
	
	private void setBlock(Location location, PlayerState playerState) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			Block block = location.getBlock();
			playerState.addPlayerBuildedBlocks(new BlockRemover(block));
			block.setBlockData(playerState.getBlockData(), false);
		});
	}
	
	public void setCallback(ICallback callback) {
		this.buildFinalCallback = callback;
	}
}
