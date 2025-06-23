package ru.kredwi.qa.game.action;

import javax.annotation.Nonnull;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public class IgnoreInteractionAction implements BuildAction{

	@Override
	public boolean execute(@Nonnull IMainGame mainGame, @Nonnull QAConfig cm, @Nonnull IGame game) {
		return true;
	}

}
