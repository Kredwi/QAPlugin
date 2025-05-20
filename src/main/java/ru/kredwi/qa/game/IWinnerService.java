package ru.kredwi.qa.game;

import java.util.List;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.state.PlayerState;

public interface IWinnerService {
	public boolean isPlayerWin(PlayerState state);
	void addWinner(Player player);
	List<Player> getWinners();
	boolean endGameIfHaveWinner(IMainGame plugin);
}
