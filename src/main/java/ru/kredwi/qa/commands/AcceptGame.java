package ru.kredwi.qa.commands;

import static ru.kredwi.qa.config.ConfigKeys.THIS_GAME_IS_NOT_REQUESTED_YOU;
import static ru.kredwi.qa.config.ConfigKeys.YOU_CONNECTED_TO;
import static ru.kredwi.qa.config.ConfigKeys.YOU_DONT_HAVE_GAME_REQUESTS;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.RequestInfo;

public class AcceptGame extends CommandAbstract {
	
	private QAConfig cm;
	private IMainGame mainGame;
	
	public AcceptGame(QAConfig cm, IMainGame mainGame) {
		super("acceptgame", 1, true, "qaplugin.commands.acceptgame");
		this.mainGame = mainGame;
		this.cm = cm;
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		
		Player player = (Player) sender;
		String connectedToGame = "";
		Set<RequestInfo> requests = commandController.getMainGame().getGameRequestManager()
				.getUserRequests(player.getUniqueId());
		
		if (Objects.isNull(requests) || requests.isEmpty()) {
			sender.sendMessage(cm.getAsString(YOU_DONT_HAVE_GAME_REQUESTS));
			return;
		}
		
		List<RequestInfo> securityRequests = new ArrayList<>(requests);
		
		if (args.length > 0) {
			
			if (!securityRequests.stream()
					.anyMatch(e -> e.gameName().equalsIgnoreCase(args[0].trim()))) {
				
				sender.sendMessage(cm.getAsString(THIS_GAME_IS_NOT_REQUESTED_YOU));
				return;
			}
			
			commandController.getMainGame()
				.getGameRequestManager().acceptGame(player.getUniqueId(), args[0]);
			connectedToGame = args[0];
		} else {
			String gameName = securityRequests.get(0).gameName();
			commandController.getMainGame()
				.getGameRequestManager().acceptGame(player.getUniqueId(), gameName);
			connectedToGame = gameName;
		}
		sender.sendMessage(MessageFormat.format(cm.getAsString(YOU_CONNECTED_TO), connectedToGame));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!isHaveNeedsPermissions(sender)) return null;
			
		if (sender instanceof Player player) {
			
			Set<RequestInfo> UserRequests = mainGame.getGameRequestManager()
					.getUserRequests(player.getUniqueId());
			
			if (UserRequests == null) {
				return Collections.emptyList();
			}
			
			if (args.length == 0) {
				return UserRequests.stream()
						.map(RequestInfo::gameName).toList();
			}
			
			return UserRequests.stream()
					.map(RequestInfo::gameName)
					.filter(e -> e.toLowerCase().startsWith(args[0].toLowerCase()))
					.toList();
		}
		
		return null;
	}

}
