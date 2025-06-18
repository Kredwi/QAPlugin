package ru.kredwi.qa;

import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import ru.kredwi.qa.commands.CommandController;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.config.impl.ConfigManager;
import ru.kredwi.qa.event.FireworkDamageListener;
import ru.kredwi.qa.event.OwnerLeftTheGame;
import ru.kredwi.qa.event.PlayerDeadEvent;
import ru.kredwi.qa.game.GameMode;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.factory.ClassicGameFactory;
import ru.kredwi.qa.game.factory.GameFactory;
import ru.kredwi.qa.game.factory.IGameFactory;
import ru.kredwi.qa.game.factory.PleoyasmsGameFactory;
import ru.kredwi.qa.game.impl.GameManager;
import ru.kredwi.qa.sql.SQLManager;

/**
 * Main plugin and game class
 * @author Kredwi
 * */
public class QAPlugin extends JavaPlugin implements PluginWrapper {
	
	public static final Random RANDOM = new Random();
	
	/**
	 * config version for validate configs
	 * @author Kredwi
	 * */
	private static final double NEED_CONFIG_VERSION = 3.1;
	
	private static Logger logger = null;
	
	private CommandController commandController;

	private QAConfig configManager;
	
	private IMainGame gameManager;
	private IGameFactory gameFactory;
	
	private SQLManager sqlManager;
	
	@Override
	public void onLoad() {
		this.configManager = new ConfigManager(getConfig());
		this.gameManager = new GameManager(configManager);
		this.sqlManager = new SQLManager(configManager);
		this.gameFactory = new GameFactory();
		this.commandController = new CommandController(this);
	}
	
	@Override
	public void onEnable() {

		logger = getLogger();
		
		saveDefaultConfig();
		
		if (configManager.getAsDouble(ConfigKeys.VERSION) != NEED_CONFIG_VERSION) {
			for (int i = 0; i < 10; i++) {
				logger.info("THE CONFIG VERSION IS NOT SUITABLE FOR THIS VERSION OF THE PLUGIN!!!! DELETE config.yml ON THE Plugins/QAPlugin PATH, AND RESTART THE SERVER");
			}
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		if (configManager.getAsBoolean(ConfigKeys.DB_ENABLE)) {
			try {
				sqlManager.connect();
				sqlManager.createTables();
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
			}
		}
		
		commandController.start();
		
		loadGameFactories();

		Bukkit.getPluginManager().registerEvents(new OwnerLeftTheGame(configManager, gameManager), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDeadEvent(configManager, gameManager), this);
		Bukkit.getPluginManager().registerEvents(new FireworkDamageListener(), this);
	}
	
	private void loadGameFactories() {
		gameFactory.register(GameMode.CLASSIC, new ClassicGameFactory(this));
		gameFactory.register(GameMode.PLEONASMS, new PleoyasmsGameFactory(this));
	}
	
	/**
	 * If in config file <b>DELETE_BLOCKS_WHEN_DISABLE</b> == <i>true</i> then<br>
	 * Delete blocks all blocks from {@link ru.kredwi.qa.game.impl.pleonasms.ClassicGame.buildedBlock}
	 * 
	 * @author Kredwi
	 * */
	@Override
	public void onDisable() {
		boolean deleteBlocks = configManager.getAsBoolean(ConfigKeys.DELETE_BLOCKS_WHEN_DISABLE);
		if (deleteBlocks) {
			gameManager.getGames().removeIf(g -> {
				g.getBlockConstruction().getSummaryBuildedBlocks()
				.removeIf((b -> {
					b.remove();
					return true;
				}));
				return true;
			});	
		}
		if (configManager.getAsBoolean(ConfigKeys.DB_ENABLE)) {
			try {
				sqlManager.disconnect();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		this.configManager = null;
		this.commandController = null;
		this.gameManager = null;
		this.sqlManager = null;
	}
	
	@Override
	public SQLManager getSqlManager() {
		return sqlManager;
	}

	@Override
	public IMainGame getGameManager() {
		return gameManager;
	}

	@Override
	public QAConfig getConfigManager() {
		return configManager;
	}
	
	@Override
	public IGameFactory getGameFactory() {
		return gameFactory;
	}
	
	public static Logger getQALogger() {
		return logger;
	}
 }
