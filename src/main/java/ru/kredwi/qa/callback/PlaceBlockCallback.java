package ru.kredwi.qa.callback;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.QAPlugin;
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
		
		World world = location.getWorld();
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			player.playSound(location, QAConfig.BLOCK_PLACE_SOUND.getAsSound(), 1F, 1f);

			teleportPlayer(location, player);
			spawnParticle(world, location);
		});	
	}
	
	private void teleportPlayer(Location location, Player player) {
		if (player.isOnline()
				&& !player.isDead()
				&& QAConfig.TELEPORT_PLAYER_IN_PLACE.getAsBoolean()) {
			
			boolean centerLocation = QAConfig.CENTER_DIRECTION_IN_TELEPORT.getAsBoolean();
			
			player.teleport(LocationUtils.centerLocation(location, centerLocation));
		}
	}
	
	private void spawnParticle(World world, Location location) {

		if (!QAConfig.SPAWN_PLACE_PARTICLE.getAsBoolean()) {
			return;
		}

		if (Objects.isNull(world)) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("Particle dont spawned. World is null.");
			}
			return;
		}
		
		Particle particle = QAConfig.PLACE_PARTICLE.getAsParticle();
		int particleCount = QAConfig.PARTICLE_COUNT.getAsInt();
		
		world.spawnParticle(particle,
				location.clone().add(0,1,0),
				particleCount,
				 0, 0, 0, // offset X, Y, Z
				 0); // extra/speed
	}

}
