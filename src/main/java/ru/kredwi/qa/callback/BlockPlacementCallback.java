package ru.kredwi.qa.callback;

import static ru.kredwi.qa.config.ConfigKeys.BLOCK_PLACE_SOUND;
import static ru.kredwi.qa.config.ConfigKeys.CENTER_DIRECTION_IN_TELEPORT;
import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.PARTICLE_COUNT;
import static ru.kredwi.qa.config.ConfigKeys.PLACE_PARTICLE;
import static ru.kredwi.qa.config.ConfigKeys.SPAWN_PLACE_PARTICLE;
import static ru.kredwi.qa.config.ConfigKeys.TELEPORT_PLAYER_IN_PLACE;

import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.utils.LocationUtils;

public class BlockPlacementCallback implements Consumer<Location> {

	private QAConfig configManager;
	private PluginWrapper plugin;
	private Player player;
	
	public BlockPlacementCallback(PluginWrapper plugin, Player player) {
		this.configManager = plugin.getConfigManager();
		this.plugin = plugin;
		this.player = player;
	}
	
	@Override
	public void accept(Location location) {
		
		World world = location.getWorld();
		
		Bukkit.getScheduler().runTask(plugin, () -> {
			player.playSound(location, configManager.getAsSound(BLOCK_PLACE_SOUND), 1f, 1f);

			teleportPlayer(location, player);
			spawnParticle(world, location);
		});	
	}
	
	private void teleportPlayer(Location location, Player player) {
		if (player.isOnline()
				&& !player.isDead()
				&& configManager.getAsBoolean(TELEPORT_PLAYER_IN_PLACE)) {
			
			boolean centerLocation = configManager.getAsBoolean(CENTER_DIRECTION_IN_TELEPORT);
			
			player.teleport(LocationUtils.centerLocation(location, centerLocation));
		}
	}
	
	private void spawnParticle(World world, Location location) {

		if (!configManager.getAsBoolean(SPAWN_PLACE_PARTICLE)) {
			return;
		}

		if (Objects.isNull(world)) {
			if (configManager.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("Particle dont spawned. World is null.");
			}
			return;
		}
		
		Particle particle = configManager.getAsParticle(PLACE_PARTICLE);
		int particleCount = configManager.getAsInt(PARTICLE_COUNT);
		
		world.spawnParticle(particle,
				location.clone().add(0,1,0),
				particleCount,
				 0, 0, 0, // offset X, Y, Z
				 0); // extra/speed
	}

}
