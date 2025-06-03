package ru.kredwi.qa.commands;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;
import ru.kredwi.qa.sql.SQLManager;

public class DeletePlayer extends CommandAbstract {
	
	private IMainGame mainGame;
	
	public DeletePlayer(IMainGame mainGame) {
		super("deleteplayer", 2, true, "qaplugin.commands.deleteplayer");
		this.mainGame = mainGame;
	}

	@Override
	public void run(ICommandController commandController, SQLManager sqlManager, CommandSender sender,
			Command command, String[] args) {
		
		String gameName = args[0];
		String playerName = args[1];

		IGame game = commandController.getMainGame().getGame(gameName);

		if (Objects.isNull(game)) {
			sender.sendMessage(QAConfig.GAME_NOT_FOUND.getAsString());
			return;
		}
		
		if (!game.getGameInfo().isPlayerOwner((Player) sender)) {
			sender.sendMessage(QAConfig.IS_COMMAND_ONLY_FOR_GAME_OWNER.getAsString());
			return;
		}
		
		// idk
		Player player = Bukkit.getPlayer(playerName);
		
		if (Objects.isNull(player)) {
			player = game.getPlayer(playerName);
			
			if (Objects.isNull(player)) {
				sender.sendMessage(QAConfig.IS_PLAYER_IS_NOT_FOUND.getAsString());
				return;
			}
			
		}
		
		if (game.getGameInfo().isPlayerOwner(player)) {
			sender.sendMessage(QAConfig.IS_GAME_OWNER.getAsString());;
			return;
		}
		
		PlayerState playerState = game.getPlayerState(player);
		
		for (IRemover remove : playerState.getPlayerBuildedBlocks()) {
			remove.remove();
		}
		
		game.getPlayers().remove(player);
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
			IGame game = mainGame.getGame(args[0]);
			
			if (Objects.isNull(game)) {
				return Collections.emptyList();
			}
			
			return game.getPlayers().stream()
					.map(e -> e.getName())
					.filter(e -> e.startsWith(args[1]))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

}
