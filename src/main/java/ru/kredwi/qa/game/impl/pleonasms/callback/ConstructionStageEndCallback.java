package ru.kredwi.qa.game.impl.pleonasms.callback;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.callback.AbstractStageEndCallback;
import ru.kredwi.qa.game.AnswerUsedData;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;
import ru.kredwi.qa.utils.Pair;

public class ConstructionStageEndCallback extends AbstractStageEndCallback
	implements Consumer<Pair<Location, Location>> {
	
	private IGame game;
	private Player player;
	
	public ConstructionStageEndCallback(PluginWrapper plugin, IMainGame mainGame, IGame game, Player player) {
		super(plugin, mainGame, game);
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
		
		// if last player complete build
		if (game.getBlockConstruction().getBuildComplete() > game.getPlayerService().getPlayers().size()) {
			
			for (Map.Entry<Player,PlayerState> playerState : game.getPlayerService().getPlayerAndStatesArray()) {
				AnswerUsedData answerUsed = playerState.getValue().getAnswerUsed();
				if (Objects.nonNull(answerUsed)) {
					asd(answerUsed.layers(), playerState);
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
	
	private void asd(int deleteBlock, Map.Entry<Player,PlayerState> entry) {
		int delete = (deleteBlock + 4) * (IBlockConstructionService.COUNT_OF_INIT_BLOCKS + 1);
		
		List<IRemover> blockRemovers = entry.getValue().getPlayerBuildedBlocks();
		entry.getValue().removeBuildedBlock(delete);
		Collections.reverse(blockRemovers);
		
		Iterator<IRemover> iterator = blockRemovers.iterator();
		
		for (int i =0; i < delete && iterator.hasNext(); i++) {
			IRemover remover = iterator.next();
			remover.remove();
			iterator.remove();
		}
		
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
