package ru.kredwi.qa.commands;

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
import ru.kredwi.qa.sql.SQLManager;

public class Path extends CommandAbstract {
	
	private final GameRequestManager gameRequestManager;
	private IMainGame mainGame;
	
	public Path(IMainGame mainGame) {
		super("path", 1, true, true, "qaplugin.commands.path");
		
		this.gameRequestManager = mainGame.getGameRequestManager();
		this.mainGame = mainGame;
	}
	
	@Override
	public void run(ICommandController commandController, SQLManager sqlManager, CommandSender sender, Command command, String[] args) {
		
		Player player = (Player) sender;
		
		try {
			// if player nickname is not entered
			if (args.length == 1 && args[0] != null) {

				if (sendMessageIfNotOwner(player, args[0])) return;
				
				gameRequestManager.addUserRequest(player.getUniqueId(), args[0], player);
				gameRequestManager.acceptGame(player.getUniqueId(), args[0]);
				// if requests already maximum player dont create game
				// TODO rewrite this
				
				
			// if player nickname is entered
			} else if (args.length > 1 && args[1] != null) {
				
				if (sendMessageIfNotOwner(player, args[0])) return;
				
				Player otherPlayer = Bukkit.getPlayer(args[1]);
				
				if (Objects.isNull(otherPlayer)) {
					sender.sendMessage(QAConfig.IS_PLAYER_IS_NOT_FOUND.getAsString());
					return;
				}
				
				gameRequestManager.addUserRequest(otherPlayer.getUniqueId(), args[0], player);
				
				otherPlayer.sendMessage(MessageFormat.format(QAConfig.YOU_HAVE_NEW_GAME_REQUESTS.getAsString(), args[0]));
				player.sendMessage(MessageFormat.format(QAConfig.REQUESTS_SENDED.getAsString(), otherPlayer.getName())); 
			} else {
				sender.sendMessage(QAConfig.NO_ARGS.getAsString());
			}	
		} catch (RequestsOutOfBounds e) {
			
			sender.sendMessage(QAConfig.MANY_GAME_REQUESTS.getAsString());
			
			if (QAConfig.DEBUG.getAsBoolean()) {
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
					.map(e -> e.getName())
					.filter(e -> e.startsWith(args[1]))
					.collect(Collectors.toList());
		}
		
		return Collections.emptyList();
	}
	
	private boolean sendMessageIfNotOwner(Player player, String gameName) {
		IGame game = mainGame.getGame(gameName);
		
		if (!game.getGameInfo().isPlayerOwner(player)) {
			player.sendMessage(QAConfig.IS_COMMAND_ONLY_FOR_GAME_OWNER.getAsString());
			return true;
		}
		return false;
	}

}
