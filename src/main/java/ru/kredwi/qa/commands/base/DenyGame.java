package ru.kredwi.qa.commands.base;

import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.REQUEST_SUCCESS_DENIED;
import static ru.kredwi.qa.config.ConfigKeys.THIS_GAME_IS_NOT_REQUESTED_YOU;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.InvalidRequestData;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.RequestInfo;

public class DenyGame extends CommandAbstract {

	private QAConfig cm;
	private IMainGame mainGame;
	
	public DenyGame(IMainGame mainGame, QAConfig cm) {
		super("denygame", 1, true, "qaplugin.game.base");
		this.mainGame = mainGame;
		this.cm = cm;
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		try {
			
			commandController.getMainGame().getGameRequestManager()
				.denyGame(((Player) sender).getUniqueId(), args[0]);
			
			sender.sendMessage(REQUEST_SUCCESS_DENIED);
			
		} catch (InvalidRequestData e) {
			sender.sendMessage(cm.getAsString(THIS_GAME_IS_NOT_REQUESTED_YOU));
			
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info(e::getMessage);
			}
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			
			Set<RequestInfo> requests = mainGame.getGameRequestManager()
					.getUserRequests(player.getUniqueId());
			
			return Objects.isNull(requests)
					? Collections.emptyList()
					: requests.stream()
					.map(RequestInfo::gameName)
					.toList();
			
		}
		return Collections.emptyList();
	}

}
