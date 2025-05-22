package ru.kredwi.qa;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ru.kredwi.qa.commands.Answer;
import ru.kredwi.qa.commands.ConfirmGame;
import ru.kredwi.qa.commands.CreateGame;
import ru.kredwi.qa.commands.DeleteGame;
import ru.kredwi.qa.commands.DeletePlayer;
import ru.kredwi.qa.commands.DenyGame;
import ru.kredwi.qa.commands.Path;
import ru.kredwi.qa.commands.Question;
import ru.kredwi.qa.commands.StartGame;
import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.GameRequestManager;

public class QAPlugin extends JavaPlugin implements IMainGame {
	
	public static final Random RANDOM = new Random();
	
	private final GameRequestManager gameRequestManager = new GameRequestManager(this);
	
	private static Logger logger = null;
	
	// key: Player Name value: Game name
	private Map<String, String> connectedGames = new HashMap<>();
	
	private Map<String, IGame> games = new HashMap<>();
	
	@Override
	public void onEnable() {

		logger = getLogger();
		
		saveDefaultConfig();
		
		if (QAConfig.VERSION.getAsDouble() != 1.9) {
			for (int i = 0; i < 10; i++) {
				logger.info("THE CONFIG VERSION IS NOT SUITABLE FOR THIS VERSION OF THE PLUGIN!!!! DELETE config.yml ON THE Plugins/QAPlugin PATH, AND RESTART THE SERVER");
			}
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		registerCommands();
	}
	
	private void registerCommands() {
		try {
			CommandAbstract[] commandInstaces = new CommandAbstract[] {
					new Question(this), new Answer(this), new Path(this),
					new CreateGame(this), new StartGame(this), new DeleteGame(this),
					new DeletePlayer(this), new ConfirmGame(this), new DenyGame(this)
			};
			
			for (CommandAbstract commandInstance : commandInstaces) {
				PluginCommand command = getCommand(commandInstance.info.name());
				
				if (command == null) {
					logger.warning(new StringBuilder("Command ")
							.append(commandInstance.info.name())
							.append(" is not found. Skip..").toString());
					return;
				}
				
				command.setExecutor(commandInstance);
				command.setTabCompleter(commandInstance);
			}	
		} catch (NullPointerException e) {
			logger.severe("Error of loading commands: " + e.getMessage());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
		}
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
		boolean connectIsRemoved = connectedGames.entrySet()
			.removeIf((e) -> e.getValue().equals(gameName.trim().toLowerCase()));
		return connectIsRemoved & game != null;
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
