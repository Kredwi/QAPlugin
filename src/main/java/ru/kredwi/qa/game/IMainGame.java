package ru.kredwi.qa.game;

import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.request.GameRequestManager;

public interface IMainGame {

	void addGame(@Nonnull IGame game);

	IGame getGame(@Nonnull String gameName);
	
	Set<IGame> getGames();

	void connectPlayerToGame(@Nonnull Player player, @Nonnull IGame game);

	IGame getGameFromPlayer(@Nonnull Player player);

	Set<String> getNamesFromGames();

	/**
	 * Method delete games and connected players to this game
	 * @param gameName name of game
	 * @return <i>true</i> if game is deleted
	 * @author Kredwi
	 * */
	boolean removeGameWithName(@Nonnull String gameName);

	GameRequestManager getGameRequestManager();

}
