package ru.kredwi.qa.game;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.impl.GameInfo;
import ru.kredwi.qa.game.state.PlayerState;

public interface IGame
	extends IGameQuestionManager,IGameAnswer,
		IBlockConstructionService, IWinnerService {
	
	void addPath(Player player, PlayerState state);
	
	GameInfo getGameInfo();
	Set<Player> getPlayers();
	Collection<PlayerState> getStates();
	Player getPlayer(String playerName);
	PlayerState getPlayerState(Player player);
	Set<Map.Entry<Player, PlayerState>> getPlayerAndStatesArray();
	
	void setStart(boolean isStart);
	boolean isStart();
}
