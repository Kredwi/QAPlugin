package ru.kredwi.qa.game.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.state.PlayerState;

public class GamePlayerService implements IGamePlayer {

	private Map<Player, PlayerState> states = new HashMap<>();
	
	@Override
	public Player getPlayer(String playerName) {
		
		if (Objects.isNull(playerName)) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("playerName in Game.getPlayer(String) is NULL");
			}
			return null;
		}
		
		List<Player> players = this.states.keySet().stream()
				.filter(e -> e.getName().equalsIgnoreCase(playerName))
				.toList();
		
		if (players.size() < 1) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("size players in Game.getPlayer(String) is 0!");
			}
			return null;
		}
		
		return players.get(0);
	}
	
	@Override
	public PlayerState getPlayerState(Player player) {
		return this.states.get(player);
	}
	@Override
	public void addPath(Player player, PlayerState state) {
		this.states.put(player, state);
	}
	@Override
	public Collection<PlayerState> getStates() {
		return states.values();
	}
	@Override
	public Set<Player> getPlayers() {
		return states.keySet();
	}
	@Override
	public Set<Map.Entry<Player, PlayerState>> getPlayerAndStatesArray() {
		return states.entrySet();
	}
}
