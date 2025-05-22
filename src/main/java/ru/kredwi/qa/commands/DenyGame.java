package ru.kredwi.qa.commands;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.InvalidRequestData;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.RequestInfo;

public class DenyGame extends CommandAbstract {

	public DenyGame(IMainGame mainGame) {
		super(mainGame, "denygame", "qaplugin.commands.denygame");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) {
			sender.sendMessage(QAConfig.NOT_HAVE_PERMISSION.getAsString());
			return true;
		}
		
		if (!hasMoreArgsThan(args.length, 0)) {
			sender.sendMessage(QAConfig.NO_ARGS.getAsString());
			return true;
		}
		
		if (!sendMessageIfNotPlayer(sender)) return true;
		
		try {
			
			mainGame.getGameRequestManager()
				.denyGame(((Player) sender).getUniqueId(), args[0]);
			
		} catch (InvalidRequestData e) {
			
			sender.sendMessage(QAConfig.UNKNOWN_ERROR.getAsString());
			
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info(e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			
			Set<RequestInfo> requests = mainGame.getGameRequestManager()
					.getUserRequests(player.getUniqueId());
			
			return Objects.isNull(requests)
					? Collections.emptyList()
					: requests.stream()
					.map(e -> e.gameName())
					.toList();
			
		}
		return Collections.emptyList();
	}

}
