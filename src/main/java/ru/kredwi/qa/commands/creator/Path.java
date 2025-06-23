package ru.kredwi.qa.commands.creator;

import static ru.kredwi.qa.config.ConfigKeys.ENABLE_REQUESTS;
import static ru.kredwi.qa.config.ConfigKeys.GAME_IS_CREATED;
import static ru.kredwi.qa.config.ConfigKeys.GAME_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.IS_COMMAND_ONLY_FOR_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.IS_PLAYER_IS_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.MANY_GAME_REQUESTS;
import static ru.kredwi.qa.config.ConfigKeys.NO_ARGS;
import static ru.kredwi.qa.config.ConfigKeys.REQUESTS_SENDED;
import static ru.kredwi.qa.config.ConfigKeys.SERVICES_NOT_STARTED;
import static ru.kredwi.qa.config.ConfigKeys.YOU_HAVE_NEW_GAME_REQUESTS;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.RequestsOutOfBounds;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.request.GameRequestManager;


public class Path extends CommandAbstract {
	
	private final GameRequestManager gameRequestManager;
	private QAConfig cm;
	private IMainGame mainGame;
	
	public Path(IMainGame mainGame, QAConfig cm) {
		super("path", 1, true, true, "qaplugin.game.creator");
		
		this.gameRequestManager = mainGame.getGameRequestManager();
		this.mainGame = mainGame;
		this.cm = cm;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		Player playerSender = (Player) sender;
		
		try {
			switch (args.length) {
				case 0 -> sender.sendMessage(cm.getAsString(NO_ARGS));
				case 1 -> doExecute(playerSender, playerSender, args[0], false);
				default -> {
					Player player = Bukkit.getPlayer(args[1]);
					
					if (!cm.getAsBoolean(ENABLE_REQUESTS)) {
						doExecute(playerSender, player, args[0], false);
						break;
					}
					doExecute(playerSender, player, args[0], true);
				}
			}
		} catch (RequestsOutOfBounds e) {
			sender.sendMessage(cm.getAsString(MANY_GAME_REQUESTS));
		}
	}
	
	private void doExecute(@Nonnull CommandSender sender, @Nullable Player player, @Nonnull String gameName, boolean request) {
		if (player == null) {
			sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
			return;
		}
		
		if (sendMessagesIfNegativeConditions((Player) sender, gameName)) return;
		
		if (request) {
			gameRequestManager.addUserRequest(player.getUniqueId(), gameName, player);
			player.sendMessage(MessageFormat.format(cm.getAsString(YOU_HAVE_NEW_GAME_REQUESTS), gameName));
			sender.sendMessage(MessageFormat.format(cm.getAsString(REQUESTS_SENDED), player.getName())); 
		} else {
			gameRequestManager.connectPlayersToGame(gameName, player.getName(),
					(Player) sender, ((Player) sender).getLocation().clone());
		}
	}
	
	private boolean sendMessagesIfNegativeConditions(Player player, String gameName) {
		IGame game = mainGame.getGame(gameName);
		
		if (game == null) {
			player.sendMessage(cm.getAsString(GAME_NOT_FOUND));
			return true;
		}
		
		if (!game.getGameInfo().isPlayerOwner(player)) {
			player.sendMessage(cm.getAsString(IS_COMMAND_ONLY_FOR_GAME_OWNER));
			return true;
		}
		
		if (game.isStart()) {
			player.sendMessage(cm.getAsString(GAME_IS_CREATED));
			return true;
		}

		if (!game.isAllServicesReady()) {
			player.sendMessage(cm.getAsString(SERVICES_NOT_STARTED));
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if (!isHaveNeedsPermissions(sender)) return Collections.emptyList();
		
		if (args.length == 1) {
			return mainGame.getNamesFromGames().stream()
					.filter(e -> e.startsWith(args[0]))
					.collect(Collectors.toList());
		}
		
		if (args.length == 2) {
			return Bukkit.getOnlinePlayers().stream()
					.map(Player::getName)
					.filter(e -> e.startsWith(args[1]))
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}

}
