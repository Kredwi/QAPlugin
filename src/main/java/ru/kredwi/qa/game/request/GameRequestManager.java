package ru.kredwi.qa.game.request;

import static ru.kredwi.qa.config.ConfigKeys.GAME_ADD_PLAYER_ARELADY_STARTED;
import static ru.kredwi.qa.config.ConfigKeys.GAME_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.IS_PLAYER_IS_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.MAX_REQUESTS_SIZE;
import static ru.kredwi.qa.config.ConfigKeys.PATH_CREATED;
import static ru.kredwi.qa.config.ConfigKeys.PLAYER_ACCEPTED_REQUESTS;

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
import ru.kredwi.qa.exceptions.RequestsOutOfBounds;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class GameRequestManager {
	
	private IMainGame mainGame;
	
	// ONE UUID IS PLAYER UUID
	// TWO SET<RequestInfo> IF LIST OF REQUESTED GAMES
	private Map<UUID, Set<RequestInfo>> userRequests = new HashMap<>();
	private QAConfig cm;
	
	public GameRequestManager(QAConfig cm, IMainGame mainGame) {
		this.mainGame = mainGame;
		this.cm = cm;
	}
	
	public void acceptGame(UUID playerUUID, String gameName) {
		Player player = Bukkit.getPlayer(playerUUID);
		
		if (Objects.isNull(player)) {
			QAPlugin.getQALogger().info("Player is not found");
			return;
		}
		IGame game = mainGame.getGame(gameName);
		
		if (Objects.isNull(game)) {
			QAPlugin.getQALogger().info("Game is not found");
			return;
		}
		
		if (game.isStart()) {
			QAPlugin.getQALogger().info("Game is started");
			return;
		}
		
		Set<RequestInfo> request = getUserRequests(playerUUID);
		if (Objects.isNull(request)) {
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
	
	/** 
	 * @param gameName name of game
	 * @param playerName player name to connect
	 * @param sender requests owner
	 * 
	 * @author Kredwi
	 * */
	public void connectPlayersToGame(String gameName, String playerName, Player sender, Location startLocation) {
		
		IGame game = mainGame.getGame(gameName);
		
		if (Objects.nonNull(game)) {
			
			if (game.isStart()) sender.sendMessage(cm.getAsString(GAME_ADD_PLAYER_ARELADY_STARTED));
			
			Player otherPlayer = Bukkit.getPlayer(playerName);
			if (Objects.isNull(otherPlayer)) {
				sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
				return;
			}
			
			addPlayer(otherPlayer, startLocation, game);
			mainGame.connectPlayerToGame(otherPlayer, game);
			
			if (game.getGameInfo().isPlayerOwner(otherPlayer)) {
				sender.sendMessage(cm.getAsString(PATH_CREATED));
			} else {
				sender.sendMessage(MessageFormat.format(cm.getAsString(PLAYER_ACCEPTED_REQUESTS), otherPlayer.getName()));
			}
			
		} else sender.sendMessage(cm.getAsString(GAME_NOT_FOUND));
	}
	
	public void denyGame(UUID playerUUID, String gameName) throws InvalidRequestData {
		Set<RequestInfo> requestsList = this.userRequests.get(playerUUID);
		
		if (Objects.isNull(requestsList) || requestsList.isEmpty()) {
			throw new InvalidRequestData("Requests list for " + playerUUID + " is null");
		}
		
		List<RequestInfo> requestInfo = requestsList.stream()
				.filter(e -> e.gameName().equalsIgnoreCase(gameName.trim()))
				.toList();

		if (requestInfo.isEmpty() || Objects.isNull(requestInfo.get(0))) {
			throw new InvalidRequestData("Requests info for " + playerUUID + " is null or 0");
		}
		
		requestsList.remove(requestInfo.get(0));
		
		this.userRequests.put(playerUUID, requestsList);
	}
	
	public void addUserRequest(UUID playerUUID, String gameName, Player sender) throws RequestsOutOfBounds {
		Set<RequestInfo> requests = userRequests.getOrDefault(gameName, new HashSet<RequestInfo>());
		
		requests.add(new RequestInfo(gameName, sender, sender.getLocation().clone()));
		
		if (requests.size() >= cm.getAsInt(MAX_REQUESTS_SIZE))
			throw new RequestsOutOfBounds("Game requests out of bounds");
		
		this.userRequests.put(playerUUID, requests);
	}
	
	private void addPlayer(Player player, Location location, IGame game) {
		game.getPlayerService().addPath(player, new PlayerState(location.clone().add(0,-1,0),
				game.getBlockConstruction().getRandomBlockData()));
	}
	
	public Set<RequestInfo> getUserRequests(UUID playerUUID) {
		return userRequests.get(playerUUID);
	}
	
	public void clearUserRequests(UUID playerUUID) {
		this.userRequests.get(playerUUID).clear();
	}
}
