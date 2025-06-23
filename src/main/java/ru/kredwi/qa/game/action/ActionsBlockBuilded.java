package ru.kredwi.qa.game.action;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public enum ActionsBlockBuilded {
	END_GAME(new EndGameAction()),
	IGNORE(new IgnoreInteractionAction());
	
	private BuildAction action;
	
	private ActionsBlockBuilded(BuildAction action) {
		this.action = action;
	}
	
	public boolean execute(IMainGame mainGame, QAConfig cm, IGame game) {
		return action.execute(mainGame, cm, game);
	}
	
}
