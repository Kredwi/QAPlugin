package ru.kredwi.qa.game.impl.pleonasms.callback;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.callback.AbstractStageEndCallback;
import ru.kredwi.qa.game.AnswerUsedData;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;
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
		state.setLocaton(location.second());		
		isAnswerAlreadyUsed(state, location);
		// reset all dynamic states
		state.resetState();
		
		// player build complete
		game.getBlockConstruction().addBuildComplete();
		// checks is winner?
		if (game.getWinnerService().isPlayerWin(state)) {
				// add winner to list winners
				game.getWinnerService().addWinner(player);
		}
		
		// if last player complete build
		if (game.getBlockConstruction().getBuildComplete() > game.getPlayerService().getPlayers().size()) {
			
			for (Map.Entry<Player,PlayerState> playerState : game.getPlayerService().getPlayerAndStatesArray()) {
				AnswerUsedData answerUsed = playerState.getValue().getAnswerUsed();
				if (answerUsed != null) {
					deletePlayerLayer(answerUsed.layers(), playerState.getValue());
					state.setAnswerUsed(null);
					deletePlayerFromGame(playerState.getKey(), playerState.getValue(), location.second());
				}
			}
			
			// reset build completes
			game.getBlockConstruction().resetBuildComplete();
			
			super.winnerOrQuestionsPlayer(player, state);
		}
	}
	
	private void isAnswerAlreadyUsed(PlayerState state, Pair<Location, Location> location) {
		if (state.getSymbols() != null && state.getSymbols().length > 0) {
			String answer = String.copyValueOf(state.getSymbols());
			
			if (game.getGameAnswer().isAlreadyUsedAnswer(answer)) {
				state.setAnswerUsed(new AnswerUsedData(state.getSymbols().length));
				state.setLocaton(location.first());
			} else {
				game.getGameAnswer().addAlreadyUsedAnswer(answer);
			}
		}
	}
	
	private void deletePlayerLayer(int deleteBlock, PlayerState state) {
		List<IRemover> blockRemovers = state.getPlayerBuildedBlocks();
		
		int delete = deleteBlock + 4;
		int deleteInitBlocks = delete * (IBlockConstructionService.COUNT_OF_INIT_BLOCKS + 1);
		
		if (blockRemovers.size() < deleteInitBlocks) {
			delete = blockRemovers.size() / (IBlockConstructionService.COUNT_OF_INIT_BLOCKS + 1);
		}
		
		// reverse block removers
		Collections.reverse(blockRemovers);
		
		game.getBlockConstruction().deletePathLayer(state, delete);
		
		// now reverse block removers
		Collections.reverse(blockRemovers);
	}
	
	private void deletePlayerFromGame(Player player, PlayerState playerState, Location saveLocation) {
		//playerState.getPlayerBuildedBlocks()
		//	.forEach(IRemover::remove);
	
		game.getBlockConstruction()
			.addGlobalRemovers(player.getUniqueId(), playerState.getPlayerBuildedBlocks());
		
//		player.teleport(playerState.getLocaton());
//		game.getPlayerService().getPlayers().remove(player);
	}
}
