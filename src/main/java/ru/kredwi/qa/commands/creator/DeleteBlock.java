package ru.kredwi.qa.commands.creator;

import static ru.kredwi.qa.config.ConfigKeys.IN_ARGUMENT_NEEDED_NUMBER;
import static ru.kredwi.qa.config.ConfigKeys.IS_PLAYER_IS_NOT_FOUND;
import static ru.kredwi.qa.config.ConfigKeys.PLAYER_DOES_NOT_HAVE_LAYERS;
import static ru.kredwi.qa.config.ConfigKeys.YOU_DONT_GAME_OWNER;
import static ru.kredwi.qa.config.ConfigKeys.YOU_NOT_CONNECTED_TO_GAME;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.CommandAbstract;
import ru.kredwi.qa.commands.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.PlayerDontHaveLayersException;
import ru.kredwi.qa.exceptions.QAException;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;

public class DeleteBlock extends CommandAbstract{

	private IMainGame mainGame;
	
	public DeleteBlock(IMainGame mainGame) {
		super("deleteblock", 1, true, "qaplugin.game.creator");
		this.mainGame = mainGame;
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender, Command cmd, String[] args)
			throws QAException {
		QAConfig cm = commandController.getConfigManager();
		
		IGame game = commandController.getMainGame().getGameFromPlayer((Player) sender);
		
		if (game == null) {
			sender.sendMessage(cm.getAsString(YOU_NOT_CONNECTED_TO_GAME));
			return;
		}
		
		if (!game.getGameInfo().isPlayerOwner((Player) sender)) {
			sender.sendMessage(cm.getAsString(YOU_DONT_GAME_OWNER));
			return;
		}
		
		// args[0] player name
		Player player = Bukkit.getPlayer(args[0]);
		
		if (player == null) {
			sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
			return;
		}
		
		PlayerState playerState = game.getPlayerService().getPlayerState(player);
		
		Collection<IRemover> blocks = getPathBlock(cm, game, player, sender, playerState);
		
		if (blocks == null) {
			sender.sendMessage(cm.getAsString(IS_PLAYER_IS_NOT_FOUND));
			return;
		}
		
		int deleteBlock = 3;
		
		if (args.length >= 2) {
			try {
				deleteBlock = Integer.valueOf(args[1]);
			} catch (NumberFormatException e) {
				sender.sendMessage(cm.getAsString(IN_ARGUMENT_NEEDED_NUMBER));
				return;
			}
		}
		
		try {
			game.getBlockConstruction().deletePathLayer(blocks, playerState, deleteBlock);
		} catch (PlayerDontHaveLayersException e) {
			sender.sendMessage(cm.getAsString(PLAYER_DOES_NOT_HAVE_LAYERS));
			return;
		}

	}

	private Collection<IRemover> getPathBlock(QAConfig cm, IGame game, Player player, CommandSender sender, PlayerState playerState) {
		if (playerState == null) {
			List<IRemover> removers = game.getBlockConstruction()
					.getGlobalRemovers(player.getUniqueId());
			
			if (removers.isEmpty()) {
				return null;
			}
			
			return removers;
		} else {
			return playerState.getPlayerBuildedBlocks();
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return null;
		
		if (!isHaveNeedsPermissions(sender)) return null;
		
		if (args.length == 1) {
			return mainGame.getGames()
				.stream().map(e -> e.getGameInfo().name()).toList();
		}
		
		if (args.length > 1) {
			IGame game = mainGame.getGame(args[1].toLowerCase());
			
			if (game == null)
				return Collections.emptyList();
			
			return game.getPlayerService().getPlayers().stream()
					.map(Player::getName)
					.toList();
		}
		
		return null;
	}

}
