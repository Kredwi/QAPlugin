package ru.kredwi.qa.utils;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

public final class FireworkUtils {

	public static final int FIREWORK_MODEL_ID = 909827261;
	
	public static final void spawnFireworkEntity(Plugin plugin, Location location, FireworkEffect fireworkEffect) {
		Location loc = location.clone().add(0,3,0);
		
		Firework firework = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fwm = firework.getFireworkMeta();
		
		fwm.setPower(0);
		fwm.setCustomModelData(FIREWORK_MODEL_ID);
		fwm.addEffect(fireworkEffect);
		
		firework.setFireworkMeta(fwm);

		Bukkit.getScheduler().runTaskLater(plugin, firework::detonate, 3L);
	}

}
