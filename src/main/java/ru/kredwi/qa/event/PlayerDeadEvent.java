package ru.kredwi.qa.event;

import static ru.kredwi.qa.config.ConfigKeys.GAME_DELETE;
import static ru.kredwi.qa.config.ConfigKeys.PLAYER_DEAD_AND_LOSE;
import static ru.kredwi.qa.config.ConfigKeys.YOU_DEAD_AND_LOSE;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

/**
 * TODO if enabled mode pleonasms, and 1 player alive, this player dont win if dont write /answer now
 * */
public class PlayerDeadEvent implements Listener {

	private QAConfig cm;
	private IMainGame mainGame;
	
	public PlayerDeadEvent(QAConfig cm, IMainGame mainGame) {
		this.cm = cm;
		this.mainGame = mainGame;
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		IGame game = mainGame.getGameFromPlayer(event.getEntity());
		
		if (game == null) 
			return;
		
		PlayerState playerState = game.getPlayerService().getPlayerState(event.getEntity());
		
		game.getBlockConstruction()
			.addGlobalRemovers(event.getEntity().getUniqueId(), playerState.getPlayerBuildedBlocks());
		
		game.getPlayerService().getPlayers().remove(event.getEntity());
		
		String messageForPlayers = MessageFormat.format(cm.getAsString(PLAYER_DEAD_AND_LOSE),
				event.getEntity().getName());
		String messageForPlayer = cm.getAsString(YOU_DEAD_AND_LOSE);
		
		event.getEntity().sendMessage(messageForPlayer);
		
		if (game.getPlayerService().getPlayers().isEmpty()) {

			Player player = Bukkit.getPlayer(game.getGameInfo().ownerUUID());
			
			if (player == null || player.isOnline()) {
				player.sendMessage(cm.getAsString(GAME_DELETE));
			}
			
			mainGame.removeGameWithName(game.getGameInfo().name());
			return;
		}
	
		game.getGameEvents().onDeadPlayer();
		
		game.getPlayerService().getPlayers().forEach(p -> p.sendMessage(messageForPlayers));
	}
	
}
