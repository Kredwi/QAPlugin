package ru.kredwi.qa.game.action;

import javax.annotation.Nonnull;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public interface BuildAction {

	/**
	 * @param mainGame Main game manager
	 * @param cm Config Manager
	 * @param game Current game
	 * 
	 * @return continue iterator?
	 * */
	boolean execute(@Nonnull IMainGame mainGame, @Nonnull QAConfig cm, @Nonnull IGame game);

}
