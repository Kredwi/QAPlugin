package ru.kredwi.qa.game.factory;

import ru.kredwi.qa.game.GameMode;

public interface IGameFactory {
	void register(GameMode mode, ICreatorFactory factory);
	ICreatorFactory getGameFactory(GameMode mode);
}
