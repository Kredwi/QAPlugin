package ru.kredwi.qa.callback;

import java.text.MessageFormat;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.kredwi.qa.callback.data.PlayerAnswerData;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.state.PlayerState;

public class PlayerAnswerCallback implements ICallback {
	
	private IMainGame mainGame;
	

	public PlayerAnswerCallback(IMainGame mainGame) {
		this.mainGame = mainGame;
	}
	
	@Override
	public void run(Object o) {
		if (o instanceof PlayerAnswerData data) {
			IGame game = mainGame.getGameFromPlayer(data.player());
			
			if (game == null) {
				data.player().sendMessage(QAConfig.YOU_NOT_CONNECTED_TO_GAME.getAsString());
				return;
			}
			
			if (!game.isStart()) {
				data.player().sendMessage(QAConfig.GAME_IS_NOT_STARTED.getAsString());
				return;
			}
			
			PlayerState playerState = game.getPlayerState(data.player());
			
			if (playerState == null) {
				data.player().sendMessage(QAConfig.YOU_NOT_CONNECTED_TO_GAME.getAsString());
				return;
			}
			
			if (playerState.isAnswered()) {
				data.player().sendMessage(QAConfig.ALREADY_ANSWER.getAsString());
				return;
			}
			playerState.setAnswer(true);
			
			data.player().sendMessage(MessageFormat.format(QAConfig.YOU_ANSWER.getAsString(), data.text()));
			
			game.addAnwserCount();
			
			playerState.setSymbols(data.text().toCharArray());
			playerState.setAnswerCount(data.getWordLength());
			
			playerIsAnswer(data.player(), game.getPlayers());
			
			if (game.isAllAnswered()) {
				game.processPlayerAnswers(false);
			}	
		} else throw new ClassCastException(o.getClass().getName() + " IS NOT SUPPORTED IN PLAYER ANSWER CALLBACK");
	}
	
	private void playerIsAnswer(Player player, Set<Player> sendTo) {
		for (Player to : sendTo) {
			to.sendMessage(MessageFormat.format(QAConfig.PLAYER_ANSWER.getAsString(), player.getName()));
		}
	}
}
