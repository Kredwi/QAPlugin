package ru.kredwi.qa.utils;

import org.bukkit.Location;

public class LocationUtils {
	
	private static LocationUtils INSTANCE;
	
	public static LocationUtils getInstance() {
		if (INSTANCE== null) {
			INSTANCE = new LocationUtils();
		}
		return INSTANCE;
	}
	
	public Location centerLocation(Location location) {
		return centerLocation(location, true);
	}
	
	public Location centerLocation(Location location, boolean normalizeYaw) {
		
		int blockX = location.getBlockX();
		int blockY = location.getBlockY();
		int blockZ = location.getBlockZ();
		
		double centerX = blockX + 0.5;
		double centerY = blockY + 1;
		double centerZ = blockZ + 0.5;
		
		Location centerLocation = new Location(location.getWorld(), centerX, centerY, centerZ);
		
		if (normalizeYaw) {
			float yaw = location.getYaw();
			
			float roundYaw = Math.round(yaw / 90) * 90;
			
			centerLocation.setYaw(normalizeYaw(roundYaw));
			centerLocation.setPitch(0);	
		} else {
			centerLocation.setYaw(location.getYaw());
			centerLocation.setPitch(location.getPitch());
		}
		
		return centerLocation;
	}
	
	private float normalizeYaw(float yaw) {
		if (yaw > 180) {
			yaw -= 360;
		}
		
		while (yaw < -180) {
			yaw += 360;
		}
		
		return yaw;
	}
}
