package ru.kredwi.qa.callback;

import static ru.kredwi.qa.config.ConfigKeys.ALREADY_ANSWER;
import static ru.kredwi.qa.config.ConfigKeys.GAME_IS_NOT_STARTED;
import static ru.kredwi.qa.config.ConfigKeys.PLAYER_ANSWER;
import static ru.kredwi.qa.config.ConfigKeys.YOU_ANSWER;
import static ru.kredwi.qa.config.ConfigKeys.YOU_NOT_CONNECTED_TO_GAME;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.entity.Player;

import ru.kredwi.qa.callback.data.PlayerAnswerData;
import ru.kredwi.qa.config.ConfigAs;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.player.PlayerState;

public class PlayerAnswerCallback implements Consumer<PlayerAnswerData> {
	
	private ConfigAs cm;
	private IMainGame mainGame;

	public PlayerAnswerCallback(IMainGame mainGame, ConfigAs cm) {
		this.mainGame = mainGame;
		this.cm = cm;
	}
	
	private void notifyPlayers(Player player, Set<Player> sendTo) {
		sendTo.forEach(to -> 
			to.sendMessage(MessageFormat.format(cm.getAsString(PLAYER_ANSWER),
					player.getName())));
	}

	@Override
	public void accept(PlayerAnswerData data) {
		IGame game = mainGame.getGameFromPlayer(data.player());
		
		if (Objects.isNull(game)) {
			data.player().sendMessage(cm.getAsString(YOU_NOT_CONNECTED_TO_GAME));
			return;
		}
		
		if (!game.isStart()) {
			data.player().sendMessage(cm.getAsString(GAME_IS_NOT_STARTED));
			return;
		}
		
		PlayerState playerState = game.getPlayerService().getPlayerState(data.player());
		
		if (Objects.isNull(playerState)) {
			data.player().sendMessage(cm.getAsString(YOU_NOT_CONNECTED_TO_GAME));
			return;
		}
		
		if (playerState.isAnswered()) {
			data.player().sendMessage(cm.getAsString(ALREADY_ANSWER));
			return;
		}
		playerState.setAnswer(true);
		
		data.player().sendMessage(MessageFormat.format(cm.getAsString(YOU_ANSWER), data.text()));
		
		game.getGameAnswer().addAnwserCount();
		
		playerState.setSymbols(data.text().toCharArray());
		playerState.setAnswerCount(data.text().length());
		
		notifyPlayers(data.player(), game.getPlayerService().getPlayers());
		
		if (game.getGameAnswer().isAllAnswered()) {
			game.getGameAnswer().processPlayerAnswers(false);
		}	
	}
}
