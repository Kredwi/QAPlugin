package ru.kredwi.qa.game.service;

import java.util.List;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.player.PlayerState;

/**
 * Interface for managing winners in a game.
 * Provides methods for determining if a player has won, adding winners to a list,
 * and retrieving the list of winners.  It also handles ending the game
 * when winners are present.
 *
 * @author Kredwi
 */
public interface IWinnerService extends ReadyService {
	
	public void executeWinnerHandler();
	
	void alertOfPlayersWin();
	
    /**
     * Checks if a player has met the winning condition based on their current state.
     *
     * @param state The player's state to evaluate for a win condition.
     * @return {@code true} if the player has won based on their state, {@code false} otherwise.
     */
    boolean isPlayerWin(PlayerState state);

    /**
     * Adds a player to the list of winners.
     *
     * @param player The player to add to the list of winners.
     */
    void addWinner(Player player);

    /**
     * Retrieves the list of winners.
     *
     * @return A {@link List} containing all the players who have won the game. The list may be empty
     * if there are no winners.
     */
    List<Player> getWinners();
}