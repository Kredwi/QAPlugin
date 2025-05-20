package ru.kredwi.qa.game;

import java.util.Set;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.request.GameRequestManager;

public interface IMainGame {

	void addGame(IGame game);

	IGame getGame(String gameName);

	void connectPlayerToGame(Player player, IGame game);

	IGame getGameFromPlayer(Player player);

	Set<String> getNamesFromGames();

	/**
	 * Method delete games and connected players to this game
	 * @param gameName name of game
	 * @return <i>true</i> if game is deleted
	 * @author Kredwi
	 * */
	boolean removeGameWithName(String gameName);

	GameRequestManager getGameRequestManager();

}
