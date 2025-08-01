package ru.kredwi.qa.entity.displaytext;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.removers.BlockRemover;
import ru.kredwi.qa.removers.DisplayRemover;
import ru.kredwi.qa.utils.LocationUtils;

public class DisplayText implements IDisplayText {
	
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
	
	private PluginWrapper plugin;
	
	private boolean spawnDisplayText;
	
	public DisplayText(PluginWrapper plugin,  boolean spawnDisplayText) {
		this.plugin = plugin;
		this.spawnDisplayText = spawnDisplayText;
	}
	
	@Override
	public void createTextOnBlock(BlockRemover blockRemover, char symbol, Location targetLocation) {
		if (!spawnDisplayText) {
			if (plugin.getConfigManager().getAsBoolean(ConfigKeys.DEBUG)) {
				QAPlugin.getQALogger().info("display text spawn is cancel");
			}
			return;
		}

		Location newTargetLocation = targetLocation.clone();
		
		newTargetLocation.setYaw(-targetLocation.getYaw());
		
		Block block = blockRemover.getBlock();
		World world = block.getWorld();
		Location blockLocation = block.getLocation().clone();
		
		final float x = 0.435F;

		createTextDisplay(blockRemover, world, blockLocation.clone().add(x, 0, 1.0 + 0.001), symbol, 0F/2, 0F);
		createTextDisplay(blockRemover, world, blockLocation.clone().add(x + 0.2, 0, -0.001), symbol, 180F/1, 0F);
		createTextDisplay(blockRemover, world, blockLocation.clone().add(1.0+0.001, 0, x + 0.2), symbol, 90F*3, 0F);
		createTextDisplay(blockRemover, world, blockLocation.clone().add(-0.001, 0, x), symbol, 270F*3, 0F);
		
		float faceYaw = LocationUtils.centerLocation(newTargetLocation, true).getYaw();
		
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
		
		createTextDisplay(blockRemover, world, headDisplayText, symbol, faceYaw, -90F);
	}
	
	@SuppressWarnings("deprecation")
	private void createTextDisplay(BlockRemover blockRemover, World world, Location location, char symbol, float rotationYaw, float rotationPith) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			TextDisplay textDisplay = world.spawn(location, TextDisplay.class);
			blockRemover.addAttribute(new DisplayRemover(textDisplay));
			
			textDisplay.setTransformation(DISPLAY_TEXT_TRANSFORMATION);
			
			textDisplay.setBillboard(Billboard.FIXED);
			
			// deprecated
			textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
			
			textDisplay.setText(String.valueOf(symbol));
			
			textDisplay.setRotation(rotationYaw, rotationPith);
			textDisplay.setShadowed(true);
});
	}
}
