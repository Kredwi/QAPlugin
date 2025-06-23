package ru.kredwi.qa.commands.creator;

import static ru.kredwi.qa.config.ConfigKeys.GAME_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.IS_COMMAND_ONLY_FOR_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.IS_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.IS_PLAYER_IS_NOT_FOUND;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public class DeletePlayer extends CommandAbstract {
	
	private QAConfig cm;
	private IMainGame mainGame;
	
	public DeletePlayer(IMainGame mainGame, QAConfig cm) {
		super("deleteplayer", 2, true, "qaplugin.game.creator");
		this.mainGame = mainGame;
		this.cm = cm;
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender,
			Command command, String[] args) {
		String gameName = args[0];
		String playerName = args[1];

		IGame game = commandController.getMainGame().getGame(gameName);
		
		if (game == null) {
			sender.sendMessage(cm.getAsString(GAME_NOT_FOUND));
			return;
		}
		
		if (!game.getGameInfo().isPlayerOwner((Player) sender)) {
			sender.sendMessage(cm.getAsString(IS_COMMAND_ONLY_FOR_GAME_OWNER));
			return;
		}
		
		// idk
		Player player = Bukkit.getPlayer(playerName);
		
		if (player == null) {
			player = game.getPlayerService().getPlayer(playerName);
			
			if (player == null) {
				sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
				return;
			}
			
		}
		
		if (game.getGameInfo().isPlayerOwner(player)) {
			sender.sendMessage(cm.getAsString(IS_GAME_OWNER));
			return;
		}
		
		game.getPlayerService().deletePlayer(player);
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
			
			if (game == null) {
				return Collections.emptyList();
			}
			
			List<String> names = game.getPlayerService().getPlayers().stream()
					.map(Player::getName)
					.filter(e -> e.startsWith(args[1]))
					.collect(Collectors.toList());
			
			for (UUID uuid : game.getBlockConstruction().getGlobalPlayersRemovers()) {
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				if (!names.contains(player.getName()))
					names.add(player.getName());
			}
			
			return names;
		}
		return Collections.emptyList();
	}

}
