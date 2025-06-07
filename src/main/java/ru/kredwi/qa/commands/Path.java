package ru.kredwi.qa.commands;

import static ru.kredwi.qa.config.ConfigKeys.*;
import static ru.kredwi.qa.config.ConfigKeys.ENABLE_REQUESTS;
import static ru.kredwi.qa.config.ConfigKeys.GAME_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.IS_COMMAND_ONLY_FOR_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.IS_PLAYER_IS_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.MANY_GAME_REQUESTS;
import static ru.kredwi.qa.config.ConfigKeys.NO_ARGS;
import static ru.kredwi.qa.config.ConfigKeys.REQUESTS_SENDED;
import static ru.kredwi.qa.config.ConfigKeys.YOU_HAVE_NEW_GAME_REQUESTS;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
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
		super("path", 1, true, true, "qaplugin.commands.path");
		
		this.gameRequestManager = mainGame.getGameRequestManager();
		this.mainGame = mainGame;
		this.cm = cm;
	}
	
	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		
		Player player = (Player) sender;
		
		try {
			
			if (!cm.getAsBoolean(ENABLE_REQUESTS) && args.length > 1) {
				
				if (sendMessagesIfNegativeConditions(player, args[0])) return;
				
				Player otherPlayer = Bukkit.getPlayer(args[1]);
				
				if (Objects.isNull(otherPlayer)) {
					sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
					return;
				}
				
				gameRequestManager.connectPlayersToGame(args[0], otherPlayer.getName(),
						player, player.getLocation().clone());
				
				return;
			}
			
			// if player nickname is not entered
			if (args.length == 1 && args[0] != null) {

				if (sendMessagesIfNegativeConditions(player, args[0])) return;
				
				gameRequestManager.connectPlayersToGame(args[0], sender.getName(),
						player, player.getLocation().clone());
				
			// if player nickname is entered
			} else if (args.length > 1 && args[1] != null) {
				
				if (sendMessagesIfNegativeConditions(player, args[0])) return;
				
				Player otherPlayer = Bukkit.getPlayer(args[1]);
				
				if (Objects.isNull(otherPlayer)) {
					sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
					return;
				}
				
				gameRequestManager.addUserRequest(otherPlayer.getUniqueId(), args[0], player);
				
				otherPlayer.sendMessage(MessageFormat.format(cm.getAsString(YOU_HAVE_NEW_GAME_REQUESTS), args[0]));
				player.sendMessage(MessageFormat.format(cm.getAsString(REQUESTS_SENDED), otherPlayer.getName())); 
			} else {
				sender.sendMessage(cm.getAsString(NO_ARGS));
			}	
		} catch (RequestsOutOfBounds e) {
			
			sender.sendMessage(cm.getAsString(MANY_GAME_REQUESTS));
			
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("EXCEPTION IN PATH REQUESTS OUT OF BOUNDS: "+e.getMessage());
			}
			
		}
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
	
	private boolean sendMessagesIfNegativeConditions(Player player, String gameName) {
		IGame game = mainGame.getGame(gameName);
		
		if (Objects.isNull(game)) {
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
		
		return false;
	}

}
