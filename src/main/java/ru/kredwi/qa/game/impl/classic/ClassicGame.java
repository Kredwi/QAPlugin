package ru.kredwi.qa.game.impl.classic;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.GameMode;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.classic.IClassicGame;
import ru.kredwi.qa.game.impl.Game;
import ru.kredwi.qa.game.impl.GameServices;
import ru.kredwi.qa.game.impl.classic.service.ClassicWinnerService;
import ru.kredwi.qa.sql.SQLManager;

public class ClassicGame extends Game implements IClassicGame {
	
	private int blocksToWin;
	
	public ClassicGame(String name, int blocksToWin, Player owner, PluginWrapper plugin, SQLManager sqlManager, IMainGame gameManager) {
		super(name, owner, GameMode.CLASSIC);
		setServices(new GameServices.Builder(plugin, this, sqlManager)
				.setWinnerService(new ClassicWinnerService(blocksToWin, plugin.getConfigManager(), this, sqlManager))
				.build());
		this.blocksToWin = blocksToWin;
	}

	@Override
	public boolean isAllServicesReady() {
		return getBlockConstruction().isServiceReady()
				&& getGameAnswer().isServiceReady()
				&& getPlayerService().isServiceReady()
				&& getQuestionManager().isServiceReady()
				&& getWinnerService().isServiceReady();
	}

	@Override
	public int getBlockToWin() {
		return blocksToWin;
		
	}
}
