package ru.kredwi.qa.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.handler.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.RequestInfo;

public class ConfirmGame extends CommandAbstract {
	
	public ConfirmGame(IMainGame mainGame) {
		super(mainGame, "acceptgame");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!sendMessageIfNotPlayer(sender)) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("IN CONFIRM GAME SENDER IS NOT PLAYER " + sender.getName());
			}
			return true;
		}
		
		Player player = (Player) sender;
		String connectedToGame = "";
		List<RequestInfo> requests = new ArrayList<>(mainGame.getGameRequestManager().getUserRequests(player.getUniqueId()));
		if (requests == null || requests.isEmpty()) {
			sendError(sender, QAConfig.YOU_DONT_HAVE_GAME_REQUESTS);
			return true;
		}
		
		if (validateArgs(args.length, 0)) {
			
			if (!requests.stream()
					.anyMatch(e -> e.gameName().equalsIgnoreCase(args[0].trim()))) {
				
				sendError(sender, QAConfig.THIS_GAME_IS_NOT_REQUESTED_YOU);
				return true;
			}
			
			mainGame.getGameRequestManager().acceptGame(player.getUniqueId(), args[0]);
			connectedToGame = args[0];
		} else {
			String gameName = requests.get(0).gameName();
			mainGame.getGameRequestManager().acceptGame(player.getUniqueId(), gameName);
			connectedToGame = gameName;
		}

		sendSuccess(sender, QAConfig.YOU_CONNECTED_TO, connectedToGame);
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			
			Set<RequestInfo> UserRequests = mainGame.getGameRequestManager().getUserRequests(player.getUniqueId());
			
			if (UserRequests == null) {
				return new ArrayList<>(0);
			}
			
			if (args.length == 0) {
				return UserRequests.stream()
						.map(e -> e.gameName()).toList();
			}
			
			return UserRequests.stream()
					.map(e -> e.gameName())
					.filter(e -> e.toLowerCase().startsWith(args[0].toLowerCase()))
					.toList();
		}
		
		return null;
	}

}
