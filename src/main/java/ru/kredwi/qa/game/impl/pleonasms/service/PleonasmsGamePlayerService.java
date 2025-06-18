package ru.kredwi.qa.game.impl.pleonasms.service;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.GamePlayerService;
import ru.kredwi.qa.removers.IRemover;

public class PleonasmsGamePlayerService extends GamePlayerService {
	
	private IGame game;
	
	public PleonasmsGamePlayerService(IGame game, boolean debug) {
		super(game, debug);
		this.game = game;
	}
	
	@Override
	public void deletePlayer(Player player) {
		PlayerState playerState = getPlayerState(player);
		
		if (playerState != null) {
			super.deletePlayer(player);
		}
		
		game.getBlockConstruction().getGlobalRemovers(player.getUniqueId())
			.forEach(IRemover::remove);
	}

}
