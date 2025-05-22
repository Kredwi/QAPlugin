package ru.kredwi.qa.callback;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import ru.kredwi.qa.callback.data.PlayerAnswerData;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class PlayerAnswerCallback implements Consumer<PlayerAnswerData> {
	
	private IMainGame mainGame;
	

	public PlayerAnswerCallback(IMainGame mainGame) {
		this.mainGame = mainGame;
	}
	
	private void notifyPlayers(Player player, Set<Player> sendTo) {
		sendTo.forEach(to -> 
			to.sendMessage(MessageFormat.format(QAConfig.PLAYER_ANSWER.getAsString(),
					player.getName())));
	}

	@Override
	public void accept(PlayerAnswerData data) {
		IGame game = mainGame.getGameFromPlayer(data.player());
		
		if (Objects.isNull(game)) {
			data.player().sendMessage(QAConfig.YOU_NOT_CONNECTED_TO_GAME.getAsString());
			return;
		}
		
		if (!game.isStart()) {
			data.player().sendMessage(QAConfig.GAME_IS_NOT_STARTED.getAsString());
			return;
		}
		
		PlayerState playerState = game.getPlayerState(data.player());
		
		if (Objects.isNull(playerState)) {
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
		playerState.setAnswerCount(data.text().length());
		
		notifyPlayers(data.player(), game.getPlayers());
		
		if (game.isAllAnswered()) {
			game.processPlayerAnswers(false);
		}	
	}
}
