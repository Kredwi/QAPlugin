package ru.kredwi.qa.event;

import java.util.Objects;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public class OwnerLeftTheGame implements Listener {
	
	private IMainGame mainGame;
	
	public OwnerLeftTheGame(IMainGame mainGame) {
		this.mainGame = mainGame;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!QAConfig.DELETE_GAME_IF_OWNER_LEAVE.getAsBoolean()) {
			return;
		}
		IGame game = mainGame.getGameFromPlayer(event.getPlayer());
		
		if (Objects.isNull(game) || !(game.getGameInfo().isPlayerOwner(event.getPlayer()))) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("OwnerLeftTheGame game is null or is not game owner");
			}
			return;
		}
		
		mainGame.removeGameWithName(game.getGameInfo().name());
		
		if (QAConfig.DEBUG.getAsBoolean()) {
			QAPlugin.getQALogger().info("Owner of game " + game.getGameInfo().name() + " is leave from the game");
		}
	}
}
