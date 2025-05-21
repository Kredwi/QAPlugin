package ru.kredwi.qa.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.commands.handler.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.state.PlayerState;
import ru.kredwi.qa.removers.IRemover;

public class DeletePlayer extends CommandAbstract {
	
	public DeletePlayer(IMainGame mainGame) {
		super(mainGame, "deleteplayer", "qaplugin.commands.deleteplayer");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!playerHavePermissions(sender)) {
			sendError(sender, QAConfig.NOT_HAVE_PERMISSION);
			return true;
		}
		
		if (!validateArgs(args.length, 1)) {
			sendError(sender, QAConfig.NO_ARGS);
			return true;
		}
		
		String gameName = args[0];
		String playerName = args[1];
		
		if (isGameExists(gameName)) {
			IGame game = mainGame.getGame(gameName);
			Player player = Bukkit.getPlayer(playerName);
			
			if (Objects.isNull(player)) {
				player = game.getPlayer(playerName);
				if (Objects.isNull(player)) {
					
					if (QAConfig.DEBUG.getAsBoolean()) {
						QAPlugin.getQALogger().info("ru.kredwi.qa.commands.DeletePlayer.OnCommand PLAYER IS NULL");
					}
					
					sender.sendMessage(QAConfig.IS_PLAYER_IS_NOT_FOUND.getAsString());
					
					return true;
				}
			}
			
			if (game.getGameInfo().owner().getUniqueId().equals(player.getUniqueId())) {
				sendError(sender, QAConfig.IS_GAME_OWNER);
				return true;
			}
			
			PlayerState playerState = game.getPlayerState(player);
			for (IRemover remove : playerState.getPlayerBuildedBlocks()) {
				remove.remove();
			}
			game.getPlayers().remove(player);
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {		
		
		if (!playerHavePermissions(sender)) return new ArrayList<>(0);
		
		if (args.length == 1) {
			return mainGame.getNamesFromGames().stream()
					.filter(e -> e.startsWith(args[0]))
					.collect(Collectors.toList());
		}
		
		if (args.length == 2) {
			IGame game = mainGame.getGame(args[0]);
			
			if (Objects.isNull(game)) {
				return new ArrayList<String>(0);
			}
			
			return game.getPlayers().stream()
					.map(e -> e.getName())
					.filter(e -> e.startsWith(args[1]))
					.collect(Collectors.toList());
		}
		return new ArrayList<String>(0);
	}

}
