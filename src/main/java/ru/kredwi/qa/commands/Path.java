package ru.kredwi.qa.commands;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.RequestsOutOfBounds;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.GameRequestManager;

public class Path extends CommandAbstract {
	
	private final GameRequestManager gameRequestManager;
	
	public Path(IMainGame mainGame) {
		super(mainGame, "path", "qaplugin.commands.path");
		
		this.gameRequestManager = mainGame.getGameRequestManager();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) {
			sendError(sender, QAConfig.NOT_HAVE_PERMISSION);
			return true;
		}
		
		if (!sendMessageIfNotPlayer(sender)) return true;
		
		Player player = (Player) sender;
		
		try {
			// if player nickname is not entered
			if (args.length == 1 && args[0] != null) {
				
				gameRequestManager.addUserRequest(player.getUniqueId(), args[0], player);
				gameRequestManager.acceptGame(player.getUniqueId(), args[0]);
				
			} else if (args.length > 1 && args[1] != null) {
				
				Player otherPlayer = Bukkit.getPlayer(args[1]);
				
				if (otherPlayer == null) {
					sendError(sender, QAConfig.IS_PLAYER_IS_NOT_FOUND);
					return true;
				}
				
				gameRequestManager.addUserRequest(otherPlayer.getUniqueId(), args[0], player);
				
				otherPlayer.sendMessage(MessageFormat.format(QAConfig.YOU_HAVE_NEW_GAME_REQUESTS.getAsString(), args[0]));
				player.sendMessage(MessageFormat.format(QAConfig.REQUESTS_SENDED.getAsString(), otherPlayer.getName())); 
			} else {
				sendError(sender, QAConfig.NO_ARGS);
			}	
		} catch (RequestsOutOfBounds e) {
			
			sendError(sender, QAConfig.MANY_GAME_REQUESTS);
			
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("EXCEPTION IN PATH REQUESTS OUT OF BOUNDS: "+e.getMessage());
			}
			
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) return Collections.emptyList();
		
		if (args.length == 1) {
			return mainGame.getNamesFromGames().stream()
					.filter(e -> e.startsWith(args[0]))
					.collect(Collectors.toList());
		}
		
		if (args.length == 2) {
			return Bukkit.getOnlinePlayers().stream()
					.map(e -> e.getName())
					.filter(e -> e.startsWith(args[1]))
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}

}
