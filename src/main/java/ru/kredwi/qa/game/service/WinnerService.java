package ru.kredwi.qa.game.service;

import static ru.kredwi.qa.config.ConfigKeys.DB_ENABLE;
import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.DELETE_GAME_AFTER_TICK;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_COLORS;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_ENABLE;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_FADES;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_FLICKER;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_TRAIL;
import static ru.kredwi.qa.config.ConfigKeys.FIREWORK_FOR_WINNER_TYPE;
import static ru.kredwi.qa.config.ConfigKeys.IMMEDIATELY_END_GAME;
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

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.PluginWrapper;
import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.sql.DatabaseActions;
import ru.kredwi.qa.utils.FireworkUtils;

public abstract class WinnerService implements IWinnerService {
	
	private List<Player> winners = Collections.synchronizedList(new ArrayList<>());
	private DatabaseActions databaseActions;
	private QAConfig cm;
	private IGame game;
	private FireworkEffect fireworkEffect;
	private IMainGame mainGame;
	private Plugin plugin;
	
	public WinnerService(PluginWrapper plugin, IGame game, DatabaseActions databaseActions) {
		this.cm = plugin.getConfigManager();
		this.game = game;
		this.mainGame = plugin.getGameManager();
		this.plugin = plugin;
		this.databaseActions = databaseActions;
		this.fireworkEffect = FireworkEffect.builder()
				.with(cm.getAsFireworkType(FIREWORK_FOR_WINNER_TYPE))
				.withColor(cm.getAsBukkitColorList(FIREWORK_FOR_WINNER_COLORS))
				.withFade(cm.getAsBukkitColorList(FIREWORK_FOR_WINNER_FADES))
				.flicker(cm.getAsBoolean(FIREWORK_FOR_WINNER_FLICKER))
				.trail(cm.getAsBoolean(FIREWORK_FOR_WINNER_TRAIL))
				.build();
	}
	
	@Override
	public void executeWinnerHandler() {
		if (game.getWinnerService().getWinners().isEmpty()) {
			throw new NegativeArraySizeException("Winners is EMPTY !");
		}
		
		// spawn for winners firework effects
		if (cm.getAsBoolean(FIREWORK_FOR_WINNER_ENABLE)) {
			game.getWinnerService().getWinners().forEach(p ->
				FireworkUtils.spawnFireworkEntity(plugin, p.getLocation(), fireworkEffect));		
		}
		
		// and alert all players in the game of winners
		game.getWinnerService().alertOfPlayersWin();
		
		game.getPlayerService().spawnPlayers();
		
		game.setFinished(true);
		
		if (cm.getAsBoolean(IMMEDIATELY_END_GAME)) {
			Bukkit.getScheduler().runTaskLater(plugin, () ->
				mainGame.removeGameWithName(game.getGameInfo().name()),
				cm.getAsInt(DELETE_GAME_AFTER_TICK));
		}
	}

	/**
	 * Added winners to winner pull and update database
	 * @author Kredwi
	 * */
	@Override
	public void addWinner(Player player) {
		if (!winners.contains(player)) {
			this.winners.add(player);	
			if (cm.getAsBoolean(DB_ENABLE)) {
				CompletableFuture.runAsync(() -> databaseActions.addPlayerWinCount(player.getUniqueId()));
			}
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

	@Override
	public boolean isServiceReady() {
		return true;
	}
}