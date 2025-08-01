package ru.kredwi.qa.game.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.player.PlayerState;

public interface IGamePlayer extends ReadyService {
	void deletePlayer(@Nonnull Player player);
	void spawnPlayers();
	Set<Player> getPlayers();
	Collection<PlayerState> getStates();
	Player getPlayer(String playerName);
	PlayerState getPlayerState(Player player);
	void addPath(Player player, PlayerState state);
	Set<Map.Entry<Player, PlayerState>> getPlayerAndStatesArray();
}
