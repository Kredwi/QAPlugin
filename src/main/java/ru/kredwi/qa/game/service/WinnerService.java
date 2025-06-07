package ru.kredwi.qa.game.service;

import static ru.kredwi.qa.config.ConfigKeys.DB_ENABLE;
import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.PLAYERS_WIN_GAME;
import static ru.kredwi.qa.config.ConfigKeys.PLAYER_WIN_GAME;
import static ru.kredwi.qa.config.ConfigKeys.WIN_SOUND;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.sql.DatabaseActions;

public class WinnerService implements IWinnerService {

	private List<Player> winners = Collections.synchronizedList(new ArrayList<>());
	private DatabaseActions databaseActions;
	private QAConfig cm;
	private IGame game;
	
	public WinnerService(QAConfig cm, IGame game, DatabaseActions databaseActions) {
		this.cm = cm;
		this.game = game;
		this.databaseActions = databaseActions;
	}
	
	@Override
	public boolean isPlayerWin(PlayerState state) {
		return
				(state.getBuildedBlocks()
						- IBlockConstructionService.COUNT_OF_INIT_BLOCKS)
				>= game.getGameInfo().blocksToWin();
	}

	/**
	 * Added winners to winner pull and update database
	 * @author Kredwi
	 * */
	@Override
	public void addWinner(Player player) {
		this.winners.add(player);
		if (cm.getAsBoolean(DB_ENABLE)) {
			CompletableFuture.runAsync(() -> databaseActions.addPlayerWinCount(player.getUniqueId()));
		}
	}

	@Override
	public List<Player> getWinners() {
		return new ArrayList<>(this.winners);
	}

	@Override
	public void alertOfPlayersWin() {
		Set<Player> players = this.game.getPlayerService().getPlayers();
		
		if (Objects.isNull(players) || players.isEmpty()) {
			if (cm.getAsBoolean(DEBUG)) {
					QAPlugin.getQALogger().info("IN alertOfPlayerWin PLAYERS IS EMPTY !!!");
			}	
			return;
		}
		
		List<Player> winners = this.getWinners();
		
		boolean winCountCondition = winners.size() > 1;
		
		for (Player p : players) {
			if (winCountCondition) {
				for (Player win : winners) {
					p.sendMessage(MessageFormat.format(cm.getAsString(PLAYERS_WIN_GAME),
							win.getName()));
				}
				p.playSound(p, cm.getAsSound(WIN_SOUND), 0.8f, 1.5f);	
			} else {
				p.sendTitle(MessageFormat.format(cm.getAsString(PLAYER_WIN_GAME),
						winners.get(0).getName(), game.getGameInfo().name()), "", 30, 30, 30);
				p.playSound(p, cm.getAsSound(WIN_SOUND), 0.8f, 1.5f);
			}
		}
	}
}