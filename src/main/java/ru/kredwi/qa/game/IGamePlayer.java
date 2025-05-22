package ru.kredwi.qa.game;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.player.PlayerState;

public interface IGamePlayer {
	Set<Player> getPlayers();
	Collection<PlayerState> getStates();
	Player getPlayer(String playerName);
	PlayerState getPlayerState(Player player);
	void addPath(Player player, PlayerState state);
	Set<Map.Entry<Player, PlayerState>> getPlayerAndStatesArray();
}
