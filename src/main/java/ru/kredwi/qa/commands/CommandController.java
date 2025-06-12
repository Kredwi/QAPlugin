package ru.kredwi.qa.commands;

import static ru.kredwi.qa.config.ConfigKeys.COMMAND_ONLY_FOR_PLAYERS;
import static ru.kredwi.qa.config.ConfigKeys.DB_ENABLE;
import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.MANY_GAME_REQUESTS;
import static ru.kredwi.qa.config.ConfigKeys.NOT_HAVE_PERMISSION;
import static ru.kredwi.qa.config.ConfigKeys.NO_ARGS;
import static ru.kredwi.qa.config.ConfigKeys.UNKNOWN_ERROR;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.base.AcceptGame;
import ru.kredwi.qa.commands.base.Answer;
import ru.kredwi.qa.commands.base.DenyGame;
import ru.kredwi.qa.commands.creator.CreateGame;
import ru.kredwi.qa.commands.creator.DeleteBlock;
import ru.kredwi.qa.commands.creator.DeleteGame;
import ru.kredwi.qa.commands.creator.DeletePlayer;
import ru.kredwi.qa.commands.creator.NewQuestion;
import ru.kredwi.qa.commands.creator.Path;
import ru.kredwi.qa.commands.creator.StartGame;
import ru.kredwi.qa.commands.plugin.QAReload;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.QAException;
import ru.kredwi.qa.exceptions.RequestsOutOfBounds;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.sql.SQLManager;

public class CommandController implements CommandExecutor, ICommandController {

	private Map<String, ICommand> commands = new HashMap<>();
	private SQLManager sqlManager;
	private QAPlugin plugin;
	private IMainGame gameManager;
	private QAConfig cm;
	
	public CommandController(QAPlugin plugin) {
		this.plugin=plugin;
		this.sqlManager = plugin.getSqlManager();
		this.gameManager = plugin.getGameManager();
		this.cm = plugin.getConfigManager();
	}
	
	public void start() {
		registerCommands();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			
			if (cm.getAsBoolean(DB_ENABLE) && sqlManager.connectionNonExists()) {
				throw new SQLException("SQL Connection is not setted. Please reload server or create new issue in github.");
			}
			
			ICommand command = commands.get(cmd.getName());
			
			if (Objects.isNull(command)) {
				if (cm.getAsBoolean(DEBUG)) {
					QAPlugin.getQALogger().severe("ERROR OF COMMAND EXECUTOR FOR " + cmd.getName() + "IS NULL");
				}
				sender.sendMessage("Command executor is not found");
				return true;
			}
			
			if (command.isCommandOnlyForPlayer() && !(sender instanceof Player)) {
				sender.sendMessage(cm.getAsString(COMMAND_ONLY_FOR_PLAYERS));
				return true;
			}
			
			if (!command.isHaveNeedsPermissions(sender)) {
				sender.sendMessage(cm.getAsString(NOT_HAVE_PERMISSION));
				return true;
			}

			if (!command.isSkipCheckArgs() && !(args.length >= command.needArgs())) {
				sender.sendMessage(cm.getAsString(NO_ARGS));
				return true;
			}
			
			command.run(this, sender, cmd, args);
			
		} catch (RequestsOutOfBounds e) {
			sender.sendMessage(cm.getAsString(MANY_GAME_REQUESTS));
		} catch (QAException e) {
			e.printStackTrace();
			sender.sendMessage(cm.getAsString(UNKNOWN_ERROR));
		} catch (SQLException e) {
			e.printStackTrace();
			sender.sendMessage(cm.getAsString(UNKNOWN_ERROR));
		}
		return true;
	}
	
	private void registerCommands() {
		try {
			ICommand[] commandInstaces = new ICommand[] {
					new NewQuestion(cm), new Answer(cm, gameManager), new Path(gameManager, cm),
					new CreateGame(cm), new StartGame(gameManager, cm), new DeleteGame(gameManager, cm),
					new DeletePlayer(gameManager, cm), new AcceptGame(cm, gameManager), new DenyGame(gameManager, cm),
					new QAReload(), new DeleteBlock(gameManager)
			};
			
			for (ICommand commandInstance : commandInstaces) {
				registerCommand(commandInstance);
			}
		} catch (NullPointerException e) {
			QAPlugin.getQALogger().severe("Error of loading commands: " + e.getMessage());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(plugin);
		}
	}
	
	public void registerCommand(ICommand commandInstance) {
		PluginCommand command = plugin.getCommand(commandInstance.getName());
		
		if (Objects.isNull(command)) {
			QAPlugin.getQALogger().warning(new StringBuilder("Command ")
					.append(commandInstance.getName())
					.append(" is not found. Skip..").toString());
			return;
		}
		
		this.commands.put(commandInstance.getName(), commandInstance);
		
		command.setExecutor(this);
		command.setTabCompleter(commandInstance);
	}

	@Override
	public PluginWrapper getPlugin() {
		return plugin;
	}

	@Override
	public IMainGame getMainGame() {
		return gameManager;
	}

	@Override
	public SQLManager getSQLManager() {
		return sqlManager;
	}

	@Override
	public QAConfig getConfigManager() {
		return plugin.getConfigManager();
	}

}
