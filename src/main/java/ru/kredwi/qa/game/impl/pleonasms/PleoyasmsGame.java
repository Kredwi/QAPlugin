package ru.kredwi.qa.game.impl.pleonasms;

import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.game.GameMode;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.impl.Game;
import ru.kredwi.qa.game.impl.GameServices;
import ru.kredwi.qa.game.impl.pleonasms.service.PleonasmsEventService;
import ru.kredwi.qa.game.impl.pleonasms.service.PleonasmsWinnerService;
import ru.kredwi.qa.sql.SQLManager;

public class PleoyasmsGame extends Game {
	
	public PleoyasmsGame(String name, Player owner, PluginWrapper plugin, SQLManager sqlManager, IMainGame gameManager) {
		super(name, owner, GameMode.PLEONASMS);
		setServices(new GameServices.Builder(plugin, PleoyasmsGame.this, sqlManager)
				.setWinnerService(new PleonasmsWinnerService(plugin, PleoyasmsGame.this, sqlManager))
				.setEventService(new PleonasmsEventService(PleoyasmsGame.this))
				.build());
	}
}
