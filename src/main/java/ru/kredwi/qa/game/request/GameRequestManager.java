package ru.kredwi.qa.game.request;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.InvalidRequestData;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.state.PlayerState;

public class GameRequestManager {
	
	private IMainGame mainGame;
	
	// ONE UUID IS PLAYER UUID
	// TWO SET<RequestInfo> IF LIST OF REQUESTED GAMES
	private Map<UUID, Set<RequestInfo>> userRequests = new HashMap<>();
	
	
	public GameRequestManager(IMainGame mainGame) {
		this.mainGame = mainGame;
	}
	
	public Set<RequestInfo> getUserRequests(UUID playerUUID) {
		return userRequests.get(playerUUID);
	}

	public void addUserRequest(UUID playerUUID, String gameName, Player sender) {
		Set<RequestInfo> requests = userRequests.getOrDefault(gameName, new HashSet<RequestInfo>());
		
		requests.add(new RequestInfo(gameName, sender, sender.getLocation().clone()));
		
		this.userRequests.put(playerUUID, requests);
	}
	
	public void clearUserRequests(UUID playerUUID) {
		this.userRequests.get(playerUUID).clear();
	}
	
	public void acceptGame(UUID playerUUID, String gameName) {
		Player player = Bukkit.getPlayer(playerUUID);
		
		if (player == null) {
			QAPlugin.getQALogger().info("Player is not found");
			return;
		}
		IGame game = mainGame.getGame(gameName);
		
		if (game == null) {
			QAPlugin.getQALogger().info("Game is not found");
			return;
		}
		
		if (game.isStart()) {
			QAPlugin.getQALogger().info("Game is started");
			return;
		}
		
		Set<RequestInfo> request = getUserRequests(playerUUID);
		if (request == null) {
			QAPlugin.getQALogger().info("requests in GameRequestsManager is NULL!");
			return;
		}
		List<RequestInfo> requestsInfo = request.stream()
			.filter(e -> e.gameName().equalsIgnoreCase(gameName))
			.toList();
		
		RequestInfo reqInfo = requestsInfo.get(0);

		connectPlayersToGame(gameName, player.getName(),
				reqInfo.sender(), reqInfo.startLocation());

		clearUserRequests(playerUUID);
	}
	
	public void denyGame(UUID playerUUID, String gameName) throws InvalidRequestData {
		Set<RequestInfo> requestsList = this.userRequests.get(playerUUID);
		
		if (Objects.isNull(requestsList) || requestsList.size() < 1) {
			throw new InvalidRequestData("Requests list for " + playerUUID + " is null");
		}
		
		List<RequestInfo> requestInfo = requestsList.stream()
				.filter(e -> e.gameName().equalsIgnoreCase(gameName.trim()))
				.toList();

		if (requestInfo.size() < 1 || Objects.isNull(requestInfo.get(0))) {
			throw new InvalidRequestData("Requests info for " + playerUUID + " is null or 0");
		}
		
		requestsList.remove(requestInfo.get(0));
		
		this.userRequests.put(playerUUID, requestsList);
	}
	
	/** 
	 * @param gameName name of game
	 * @param playerName player name to connect
	 * @param sender requests owner
	 * 
	 * @author Kredwi
	 * */
	private void connectPlayersToGame(String gameName, String playerName, Player sender, Location startLocation) {
		
		IGame game = mainGame.getGame(gameName);
		
		if (game != null) {
			
			if (game.isStart()) sender.sendMessage(QAConfig.GAME_ADD_PLAYER_ARELADY_STARTED.getAsString());
			
			Player otherPlayer = Bukkit.getPlayer(playerName);
			if (otherPlayer == null) {
				sender.sendMessage(QAConfig.IS_PLAYER_IS_NOT_FOUND.getAsString());
				return;
			}
			
			addPlayer(otherPlayer, startLocation, game);
			mainGame.connectPlayerToGame(otherPlayer, game);
			
			if (game.getGameInfo().owner().getUniqueId().equals(otherPlayer.getUniqueId())) {
				sender.sendMessage(QAConfig.PATH_CREATED.getAsString());
			} else {
				sender.sendMessage(MessageFormat.format(QAConfig.PLAYER_ACCEPTED_REQUESTS.getAsString(), otherPlayer.getName()));
			}
			
		} else sender.sendMessage(QAConfig.GAME_NOT_FOUND.getAsString());
	}
	
	private void addPlayer(Player player, Location location, IGame game) {
		game.addPath(player, new PlayerState(location.clone().add(0,-1,0), game.getRandomBlockData()));
	}
}
