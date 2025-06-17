package ru.kredwi.qa.game.factory;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.IGame;

public interface ICreatorFactory {
	boolean validateParams(String[] args, Player owner);
	IGame createGame(String name, Player owner, String[] args);
}
