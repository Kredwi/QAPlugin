package ru.kredwi.qa.game.impl.classic;

import static ru.kredwi.qa.config.ConfigKeys.DEBUG;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.GameMode;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.classic.IClassicGame;
import ru.kredwi.qa.game.impl.Game;
import ru.kredwi.qa.game.impl.GameServices;
import ru.kredwi.qa.game.impl.classic.service.ClassicConsturctionService;
import ru.kredwi.qa.game.impl.classic.service.ClassicWinnerService;
import ru.kredwi.qa.game.service.GamePlayerService;
import ru.kredwi.qa.sql.SQLManager;

public class ClassicGame extends Game implements IClassicGame {
	
	private int blocksToWin;
	
	public ClassicGame(String name, int blocksToWin, Player owner, PluginWrapper plugin, SQLManager sqlManager, IMainGame gameManager) {
		super(name, owner, GameMode.CLASSIC);
		
		IGamePlayer playerService = new GamePlayerService(ClassicGame.this, plugin.getConfigManager().getAsBoolean(DEBUG));
		IWinnerService winnerService= new ClassicWinnerService(blocksToWin, plugin, this, sqlManager);
		
		setServices(new GameServices.Builder(plugin, this, sqlManager)
				.setWinnerService(winnerService)
				.setBlockConstructionService(new ClassicConsturctionService(ClassicGame.this, plugin, playerService, winnerService))
				.setGamePlayer(playerService)
				.build());
		
		this.blocksToWin = blocksToWin;
	}

	@Override
	public int getBlockToWin() {
		return blocksToWin;
		
	}
}
