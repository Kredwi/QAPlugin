package ru.kredwi.qa.commands;

import static ru.kredwi.qa.config.ConfigKeys.GAME_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.IS_COMMAND_ONLY_FOR_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.IS_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.IS_PLAYER_IS_NOT_FOUND;

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
import ru.kredwi.qa.config.ConfigAs;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;

public class DeletePlayer extends CommandAbstract {
	
	private ConfigAs cm;
	private IMainGame mainGame;
	
	public DeletePlayer(IMainGame mainGame, ConfigAs cm) {
		super("deleteplayer", 2, true, "qaplugin.commands.deleteplayer");
		this.mainGame = mainGame;
		this.cm = cm;
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender,
			Command command, String[] args) {
		
		String gameName = args[0];
		String playerName = args[1];

		IGame game = commandController.getMainGame().getGame(gameName);

		if (Objects.isNull(game)) {
			sender.sendMessage(cm.getAsString(GAME_NOT_FOUND));
			return;
		}
		
		if (!game.getGameInfo().isPlayerOwner((Player) sender)) {
			sender.sendMessage(cm.getAsString(IS_COMMAND_ONLY_FOR_GAME_OWNER));
			return;
		}
		
		// idk
		Player player = Bukkit.getPlayer(playerName);
		
		if (Objects.isNull(player)) {
			player = game.getPlayerService().getPlayer(playerName);
			
			if (Objects.isNull(player)) {
				sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
				return;
			}
			
		}
		
		if (game.getGameInfo().isPlayerOwner(player)) {
			sender.sendMessage(cm.getAsString(IS_GAME_OWNER));
			return;
		}
		
		PlayerState playerState = game.getPlayerService().getPlayerState(player);
		
		playerState.getPlayerBuildedBlocks()
			.forEach(IRemover::remove);
		
		game.getPlayerService().getPlayers().remove(player);
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
			
			return game.getPlayerService().getPlayers().stream()
					.map(Player::getName)
					.filter(e -> e.startsWith(args[1]))
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

}
