package ru.kredwi.qa.game.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.kredwi.qa.game.GameMode;

public class GameFactory implements IGameFactory {

	private Map<GameMode, ICreatorFactory> factory = new HashMap<>();
	
	public void register(GameMode mode, ICreatorFactory factory) {
		this.factory.put(mode, factory);
	}
	
	public ICreatorFactory getGameFactory(GameMode mode) {
		ICreatorFactory gameFactory = factory.get(mode);
		if (gameFactory == null) {
			throw new NullPointerException("Game factory dont found");
		}
		return gameFactory;
	}
	
	@Override
	public List<GameMode> getAllGameMode() {
		return new ArrayList<>(factory.keySet());
	}

}
