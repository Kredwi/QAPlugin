package ru.kredwi.qa.game.service;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;

import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.state.PlayerState;

public class WinnerService implements IWinnerService {

	private List<Player> winners = new LinkedList<>();
	private IGame game;
	
	public WinnerService(IGame game) {
		this.game = game;
	}
	
	@Override
	public boolean isPlayerWin(PlayerState state) {
		return
				(state.getBuildedBlocks()
						- IBlockConstructionService.COUNT_OF_INIT_BLOCKS)
				>= game.getGameInfo().blocksToWin();
	}

	@Override
	public void addWinner(Player player) {
		winners.add(player);
	}

	@Override
	public List<Player> getWinners() {
		return winners;
	}

	@Override
	public void alertOfPlayersWin() {
		
		boolean winCountCondition = winners.size() > 1;
		
		for (Player p : game.getPlayers()) {
			if (winCountCondition) {
				for (Player win : winners) {
					p.sendMessage(MessageFormat.format(QAConfig.PLAYERS_WIN_GAME.getAsString(),
							win.getName()));
				}
				p.playSound(p, QAConfig.WIN_SOUND.getAsSound(), 0.8f, 1.5f);	
			} else {
				p.sendTitle(MessageFormat.format(QAConfig.PLAYER_WIN_GAME.getAsString(),
						winners.get(0).getName(), game.getGameInfo().name()), "", 30, 30, 30);
				p.playSound(p, QAConfig.WIN_SOUND.getAsSound(), 0.8f, 1.5f);
			}
		}
	}

}
