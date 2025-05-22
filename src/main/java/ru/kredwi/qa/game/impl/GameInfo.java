package ru.kredwi.qa.game.impl;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

public record GameInfo(String name, UUID ownerUUID, int blocksToWin) {
	
	public boolean isPlayerOwner(OfflinePlayer player) {
		return ownerUUID.equals(player.getUniqueId());
	}
	
}
