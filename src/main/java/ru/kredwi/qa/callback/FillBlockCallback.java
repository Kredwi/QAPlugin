package ru.kredwi.qa.callback;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.state.PlayerState;

public class FillBlockCallback implements ICallback {

	private IMainGame mainGame;
	private IGame game;
	private Player player;
	
	public FillBlockCallback(IMainGame mainGame, IGame game, Player player) {
		this.mainGame = mainGame;
		this.game = game;
		this.player = player;
	}
	
	@Override
	public void run(Object o) {
		PlayerState state = game.getPlayerState(player);
		state.resetState();
		
		game.addBuildComplete();
		if (game.isPlayerWin(state)) {
			game.addWinner(player);	
		}
		if (game.getBuildComplete() > game.getPlayers().size()) {
			if (!game.endGameIfHaveWinner(mainGame)) {
				game.resetBuildComplete();
				game.questionPlayers();
			}
		}
	}
}
