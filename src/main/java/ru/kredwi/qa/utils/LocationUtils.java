package ru.kredwi.qa.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.lang.Math;

public class LocationUtils {
	
	public static Vector horizontalizeDirection(Location targetLocation) {
		Vector direction = targetLocation.getDirection().normalize();
		direction.setY(0);
		
		if (Math.abs(direction.getX()) > Math.abs(direction.getZ())) {
			return new Vector(Math.signum(direction.getX()), 0, 0);
		} else {
			return new Vector(0, 0, Math.signum(direction.getZ()));
		}
	}
	
	public static Location centerLocation(Location location) {
		return centerLocation(location, true);
	}
	
	public static Location centerLocation(Location location, boolean normalizeYaw) {
		
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
	
	public static float normalizeYaw(float yaw) {
		return (float)Math.floorMod((long)yaw + 180, 360) - 180;
	}
}
