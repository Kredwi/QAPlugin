package ru.kredwi.qa.commands;

import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.commands.base.CommandAbstract;
import ru.kredwi.qa.commands.base.ICommandController;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class Question extends CommandAbstract {
	
	public Question(IMainGame mainGame) {
		super("question", 1, true, true, "qaplugin.commands.question");
	}

	@Override
	public void run(ICommandController commandController, CommandSender sender, Command command, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(QAConfig.NO_ARGS.getAsString());
			return;
		}

		Player player = ((Player) sender);
		IGame game = commandController.getMainGame()
				.getGameFromPlayer(player);
		
		if (Objects.isNull(game)) {
			sender.sendMessage(QAConfig.GAME_NOT_FOUND.getAsString());
			return;
		}
		
		if (!(game.getGameInfo().isPlayerOwner(player))) {
			sender.sendMessage(QAConfig.IS_COMMAND_ONLYE_FOR_GAME_OWNER.getAsString());
			return;
		}
		
		String question = String.join(" ", args);
		
		for (Player pl : game.getPlayers()) {
			PlayerState playerState = game.getPlayerState(pl);
			playerState.resetState();
		}
		
		game.resetAnwserCount();
		game.questionPlayers(question);
		
		return;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
