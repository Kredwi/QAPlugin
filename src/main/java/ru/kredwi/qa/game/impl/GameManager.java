package ru.kredwi.qa.game.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.GameRequestManager;

public class GameManager implements IMainGame {
	private Plugin plugin;
	private final GameRequestManager gameRequestManager;
	
	// key: Player Name value: Game name
	private Map<String, String> connectedGames = new HashMap<>();
	
	private Map<String, IGame> games = new HashMap<>();
	
	public GameManager(PluginWrapper plugin) {
		gameRequestManager = new GameRequestManager(plugin.getConfigManager(), this);
		this.plugin = plugin;
	}
	
	/**
	 * Method delete games and connected players to this game
	 * @param gameName name of game
	 * @return <i>true</i> if game is deleted
	 * @author Kredwi
	 * */
	@Override
	public boolean removeGameWithName(String gameName) {
		IGame game = games.remove(gameName.trim().toLowerCase());
		boolean gameIsExists = game != null;
		
		if (gameIsExists) {
			game.getGameAnswer().resetAnwserCount();
			game.getBlockConstruction().resetBuildComplete();
			Bukkit.getScheduler().runTask(plugin, () -> game.getBlockConstruction().deleteBuildedBlocks());
		}
		
		boolean connectIsRemoved = connectedGames.entrySet()
			.removeIf((e) -> e.getValue().equals(gameName.trim().toLowerCase()));
		return connectIsRemoved && gameIsExists;
	}
	
	@Override
	public Set<IGame> getGames() {
		return games.values().stream()
				.collect(Collectors.toSet());
	}
	
	@Override
	public IGame getGame(String gameName) {
		return gameName == null ? null : games.get(gameName.trim().toLowerCase());
	}
	@Override
	public void addGame(IGame game) {
		this.games.put(game.getGameInfo().name(), game);
	}
	@Override
	public void connectPlayerToGame(Player player, IGame game) {
		connectedGames.put(player.getName(), game.getGameInfo().name());
	}
	@Override
	public IGame getGameFromPlayer(Player player) {
		return getGame(connectedGames.get(player.getName()));
	}
	@Override
	public Set<String> getNamesFromGames() {
		return games.keySet();
	}

	@Override
	public GameRequestManager getGameRequestManager() {
		return gameRequestManager;
	}

}
