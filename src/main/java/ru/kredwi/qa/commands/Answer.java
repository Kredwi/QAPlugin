package ru.kredwi.qa.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.kredwi.qa.callback.ICallback;
import ru.kredwi.qa.callback.PlayerAnswerCallback;
import ru.kredwi.qa.callback.data.PlayerAnswerData;
import ru.kredwi.qa.commands.handler.CommandAbstract;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IMainGame;

public class Answer extends CommandAbstract {
	
	private ICallback callback;

	public Answer(IMainGame mainGame) {
		this(mainGame, new PlayerAnswerCallback(mainGame));
	}
	
	public Answer(IMainGame mainGame, ICallback callback) {
		super(mainGame, "answer");
		this.callback = callback;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length < 1) {
			sendError(sender, QAConfig.NO_ARGS);
			return true;
		}
		
		if (isPlayer(sender)) {
			String text = args[0];
			callback.run(new PlayerAnswerData(((Player) sender), text));
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return new ArrayList<>(0);
	}
	
	public void setCallback(ICallback callback) {
		this.callback = callback;
	}
}