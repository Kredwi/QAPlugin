package ru.kredwi.qa.callback;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.utils.LocationUtils;

public class PlaceBlockCallback implements ICallback {

	private Plugin plugin;
	private Player player;
	
	public PlaceBlockCallback(Plugin plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
	}
	
	@Override
	public void run(Object o) {
		if (o instanceof Location location) {
			Bukkit.getScheduler().runTask(plugin, () -> {
				player.playSound(location, QAConfig.BLOCK_PLACE_SOUND.getAsSound(), 1F, 1f);
				if (!player.isDead()) {
					player.teleport(LocationUtils.getInstance().centerLocation(location));	
				}
			});	
		}
	}

}
