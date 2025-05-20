package ru.kredwi.qa.game;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.state.PlayerState;

public interface GamePath {
	void addPath(Player player, PlayerState state);
}
