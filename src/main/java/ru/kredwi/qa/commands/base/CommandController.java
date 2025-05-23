package ru.kredwi.qa.commands.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.Answer;
import ru.kredwi.qa.commands.ConfirmGame;
import ru.kredwi.qa.commands.CreateGame;
import ru.kredwi.qa.commands.DeleteGame;
import ru.kredwi.qa.commands.DeletePlayer;
import ru.kredwi.qa.commands.DenyGame;
import ru.kredwi.qa.commands.Path;
import ru.kredwi.qa.commands.Question;
import ru.kredwi.qa.commands.StartGame;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.QAException;
import ru.kredwi.qa.exceptions.RequestsOutOfBounds;
import ru.kredwi.qa.game.IMainGame;

public class CommandController implements CommandExecutor, ICommandController {

	private Map<String, ICommand> commands = new HashMap<>(); 
	private QAPlugin plugin;
	
	public CommandController(QAPlugin plugin) {
		this.plugin=plugin;
	}
	
	public void start() {
		registerCommands();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			
			ICommand command = commands.get(cmd.getName());
			
			if (Objects.isNull(command)) {
				if (QAConfig.DEBUG.getAsBoolean()) {
					QAPlugin.getQALogger().severe("ERROR OF COMMAND EXECUTOR FOR " + cmd.getName() + "IS NULL");
				}
				sender.sendMessage("Command executor is not found");
				return true;
			}
			
			if (command.isCommandOnlyForPlayer() && !(sender instanceof Player)) {
				sender.sendMessage(QAConfig.COMMAND_ONLY_FOR_PLAYERS.getAsString());
				return true;
			}
			
			if (!command.isHaveNeedsPermissions(sender)) {
				sender.sendMessage(QAConfig.NOT_HAVE_PERMISSION.getAsString());
				return true;
			}

			if (!command.isSkipCheckArgs() && !(args.length >= command.needArgs())) {
				sender.sendMessage(QAConfig.NO_ARGS.getAsString());
				return true;
			}
			
			command.run(this, sender, cmd, args);
			
		} catch (RequestsOutOfBounds e) {
			sender.sendMessage(QAConfig.MANY_GAME_REQUESTS.getAsString());
		} catch (QAException e) {
			e.printStackTrace();
			sender.sendMessage(QAConfig.UNKNOWN_ERROR.getAsString());
		}
		return true;
	}
	
	private void registerCommands() {
		try {
			ICommand[] commandInstaces = new ICommand[] {
					new Question(plugin), new Answer(plugin), new Path(plugin),
					new CreateGame(plugin), new StartGame(plugin), new DeleteGame(plugin),
					new DeletePlayer(plugin), new ConfirmGame(plugin), new DenyGame(plugin)
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
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public IMainGame getMainGame() {
		return plugin;
	}

}
