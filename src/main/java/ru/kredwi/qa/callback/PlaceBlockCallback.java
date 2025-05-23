package ru.kredwi.qa.callback;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.utils.LocationUtils;

public class PlaceBlockCallback implements Consumer<Location> {

	private Plugin plugin;
	private Player player;
	
	public PlaceBlockCallback(Plugin plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	@Override
	public void accept(Location location) {
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			player.playSound(location, QAConfig.BLOCK_PLACE_SOUND.getAsSound(), 1F, 1f);
			if (player.isOnline() & !player.isDead()) {
				player.teleport(LocationUtils.centerLocation(location));	
			}
		});	
	}

}
