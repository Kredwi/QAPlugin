package ru.kredwi.qa.game.action;

import static ru.kredwi.qa.config.ConfigKeys.DEBUG;

import javax.annotation.Nonnull;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public class EndGameAction implements BuildAction {

	@Override
	public boolean execute(@Nonnull IMainGame mainGame, @Nonnull QAConfig cm, @Nonnull IGame game) {
		if (game.isPreStopGame()) {
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("Game already end.");
			}
			return false;
		};
		
		// set mark of pre stop game
		game.setPreStopGame(true);
		
		return false;
	}

}
