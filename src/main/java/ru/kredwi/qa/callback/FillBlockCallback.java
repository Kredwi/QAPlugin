package ru.kredwi.qa.callback;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class FillBlockCallback implements Consumer<Void> {

	private IMainGame mainGame;
	private IGame game;
	private Player player;
	
	public FillBlockCallback(IMainGame mainGame, IGame game, Player player) {
		this.mainGame = mainGame;
		this.game = game;
		this.player = player;
	}
	
	@Override
	public void accept(Void o) {
		// get path owner
		PlayerState state = game.getPlayerState(player);
		// reset all dynamic states
		state.resetState();
		
		// player build complete
		game.addBuildComplete();
		
		// checks is winner?
		if (game.isPlayerWin(state)) {
			// add winner to list winners
			game.addWinner(player);
		}
		
		// if last player complete build
		if (game.getBuildComplete() > game.getPlayers().size()) {
			
			// reset build completes
			game.resetBuildComplete();
			
			// if have winner
			if (!game.getWinners().isEmpty()) {
				
				// alert all players in the game of winners
				game.alertOfPlayersWin();
				
				// delete all register blocks in the game
				game.deleteBuildedBlocks();
				
				// and remove game from global games
				mainGame.removeGameWithName(game.getGameInfo().name());
				
			// if game is dont have winners questions players of new question
			} else game.questionPlayers();
		}
	}
}
