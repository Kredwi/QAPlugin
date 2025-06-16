package ru.kredwi.qa.game.impl;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import ru.kredwi.qa.game.GameMode;

public record GameInfo(String name, UUID ownerUUID, Location spawnLocation, GameMode mode) {
	
	public boolean isPlayerOwner(OfflinePlayer player) {
		return ownerUUID.equals(player.getUniqueId());
	}
	
}
