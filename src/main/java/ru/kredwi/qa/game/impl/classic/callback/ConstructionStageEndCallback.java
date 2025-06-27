package ru.kredwi.qa.game.impl.classic.callback;

import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.callback.AbstractStageEndCallback;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.utils.Pair;

public class ConstructionStageEndCallback extends AbstractStageEndCallback
	implements Consumer<Pair<Location, Location>> {
	
	private IGame game;
	private Player player;
	
	public ConstructionStageEndCallback(PluginWrapper plugin, IGame game, Player player) {
		super(plugin, game);
		this.game = game;
		this.player = player;
	}
	
	@Override
	public void accept(Pair<Location, Location> location) {
		// get path owner
		PlayerState state = game.getPlayerService().getPlayerState(player);
		String answer = new String(state.getSymbols());
		
		sendMessageIfDisplaysDisabled(answer, player.getName());
		
		state.setLocaton(location.second());
		
		// reset all dynamic states
		state.resetState();
		
		// player build complete
		game.getBlockConstruction().addBuildComplete();
		
		game.getWinnerService().addPlayerIfWin(player);
		
		// if last player complete build
		if (game.getBlockConstruction().getBuildComplete() > game.getPlayerService().getPlayers().size()) {
			// reset build completes
			game.getBlockConstruction().resetBuildComplete();
			
			super.winnerOrQuestionsPlayer(player, answer);
		}
	}
}
