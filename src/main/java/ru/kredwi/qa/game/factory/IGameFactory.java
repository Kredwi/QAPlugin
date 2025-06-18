package ru.kredwi.qa.game.factory;

import java.util.List;

import ru.kredwi.qa.game.GameMode;

public interface IGameFactory {
	void register(GameMode mode, ICreatorFactory factory);
	ICreatorFactory getGameFactory(GameMode mode);
	List<GameMode> getAllGameMode();
}
