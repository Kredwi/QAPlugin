package ru.kredwi.qa.event;

import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.DELETE_GAME_IF_OWNER_LEAVE;

import java.util.Objects;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;

public class OwnerLeftTheGame implements Listener {
	
	private QAConfig cm;
	private IMainGame mainGame;
	
	public OwnerLeftTheGame(QAConfig cm, IMainGame mainGame) {
		this.cm = cm;
		this.mainGame = mainGame;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!cm.getAsBoolean(DELETE_GAME_IF_OWNER_LEAVE)) {
			return;
		}
		IGame game = mainGame.getGameFromPlayer(event.getPlayer());
		
		if (Objects.isNull(game) || !(game.getGameInfo().isPlayerOwner(event.getPlayer()))) {
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("OwnerLeftTheGame game is null or is not game owner");
			}
			return;
		}
		
		mainGame.removeGameWithName(game.getGameInfo().name());
		
		if (cm.getAsBoolean(DEBUG)) {
			QAPlugin.getQALogger().info("Owner of game " + game.getGameInfo().name() + " is leave from the game");
		}
	}
}
