package ru.kredwi.qa;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ru.kredwi.qa.commands.base.CommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.GameRequestManager;

/**
 * Main plugin and game class
 * @author Kredwi
 * */
public class QAPlugin extends JavaPlugin implements IMainGame {
	
	public static final Random RANDOM = new Random();
	
	/**
	 * config version for validate configs
	 * @author Kredwi
	 * */
	private static final double NEED_CONFIG_VERSION = 2.1;
	
	private final GameRequestManager gameRequestManager = new GameRequestManager(this);
	
	private final CommandController commandController = new CommandController(this);
	
	private static Logger logger = null;
	
	// key: Player Name value: Game name
	private Map<String, String> connectedGames = new HashMap<>();
	
	private Map<String, IGame> games = new HashMap<>();
	
	@Override
	public void onEnable() {

		logger = getLogger();
		
		saveDefaultConfig();
		
		if (QAConfig.VERSION.getAsDouble() != NEED_CONFIG_VERSION) {
			for (int i = 0; i < 10; i++) {
				logger.info("THE CONFIG VERSION IS NOT SUITABLE FOR THIS VERSION OF THE PLUGIN!!!! DELETE config.yml ON THE Plugins/QAPlugin PATH, AND RESTART THE SERVER");
			}
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		commandController.start();
	}
	

	
	/**
	 * If in config file <b>DELETE_BLOCKS_WHEN_DISABLE</b> == <i>true</i> then<br>
	 * Delete blocks all blocks from {@link ru.kredwi.qa.game.impl.Game.buildedBlock}
	 * 
	 * @author Kredwi
	 * */
	@Override
	public void onDisable() {
		boolean deleteBlocks = QAConfig.DELETE_BLOCKS_WHEN_DISABLE.getAsBoolean();
		if (deleteBlocks) {
			games.entrySet().removeIf(e -> {
				e.getValue().deleteBuildedBlocks();
				return true;
			});	
		}
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
			game.resetAnwserCount();
			game.resetBuildComplete();
			game.deleteBuildedBlocks();
		}
		
		boolean connectIsRemoved = connectedGames.entrySet()
			.removeIf((e) -> e.getValue().equals(gameName.trim().toLowerCase()));
		return connectIsRemoved && gameIsExists;
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
	
	public static Logger getQALogger() {
		return logger;
	}
 }
