package ru.kredwi.qa.game.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.removers.IRemover;

public class Game implements IGame {
	
	private final GameServices services;
	private final GameInfo gameInfo;
	private boolean isStart;
	
	public Game(String name, Player owner, int blocksToWin, QAPlugin plugin) {
		this.services = new GameServices(this, plugin);
		this.gameInfo = new GameInfo(name.trim().toLowerCase(),
				owner.getUniqueId(), blocksToWin);
	}
	
	@Override
	public GameInfo getGameInfo() {
		return gameInfo;
	}

	@Override
	public boolean isStart() {
		return isStart;
	}

	@Override
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	
	
	
	
	
	@Override
	public void questionPlayers() {
		services.getQuestionManager().questionPlayers();
	}
	
	@Override
	public void questionPlayers(String question) {
		services.getQuestionManager().questionPlayers(question);
	}
	
	@Override
	public boolean questionIsUsed(int questionIndex) {
		return services.getQuestionManager().questionIsUsed(questionIndex);
	}

	@Override
	public void addUsedQuestion(int usedText) {
		services.getQuestionManager().addUsedQuestion(usedText);
	}
	
	@Override
	public int usedQuestionSize() {
		return services.getQuestionManager().usedQuestionSize();
	}

	@Override
	public void addAnwserCount() {
		services.getGameAnswer().addAnwserCount();
	}

	@Override
	public void resetAnwserCount() {
		services.getGameAnswer().resetAnwserCount();
	}

	@Override
	public boolean isAllAnswered() {
		return services.getGameAnswer().isAllAnswered();
	}

	@Override
	public void processPlayerAnswers(boolean isInit) {
		services.getGameAnswer().processPlayerAnswers(isInit);
	}
	
	@Override
	public void alertOfPlayersWin() {
		services.getWinnerService().alertOfPlayersWin();
	}

	@Override
	public boolean isPlayerWin(PlayerState state) {
		return services.getWinnerService().isPlayerWin(state);
	}
	
	@Override
	public List<Player> getWinners() {
		return services.getWinnerService().getWinners();
	}
	
	@Override
	public void addWinner(Player player) {
		services.getWinnerService().addWinner(player);
	}
	
	@Override
	public void deleteBuildedBlocks() {
		services.getBlockConstructionService().deleteBuildedBlocks();
	}
	
	@Override
	public int getBuildComplete() {
		return services.getBlockConstructionService().getBuildComplete();
	}
	
	@Override
	public void addBuildComplete() {
		services.getBlockConstructionService().addBuildComplete();
	}
	
	@Override
	public void resetBuildComplete() {
		services.getBlockConstructionService().resetBuildComplete();
	}
	
	@Override
	public BlockData getRandomBlockData() {
		return services.getBlockConstructionService().getRandomBlockData();
	}

	@Override
	public Set<IRemover> getSummaryBuildedBlocks() {
		return services.getBlockConstructionService().getSummaryBuildedBlocks();
	}

	@Override
	public List<BukkitTask> getBuildedTasks() {
		return services.getBlockConstructionService().getBuildedTasks();
	}

	@Override
	public boolean buildIsStopped() {
		return services.getBlockConstructionService().buildIsStopped();
	}

	@Override
	public void setStopBuild(boolean isStop) {
		services.getBlockConstructionService().setStopBuild(isStop);
	}
	
	@Override
	public void scheduleBuildForPlayer(Player player, PlayerState state, boolean isInit) {
		services.getBlockConstructionService().scheduleBuildForPlayer(player, state, isInit);
	}

	@Override
	public Set<Player> getPlayers() {
		return services.getGamePlayer().getPlayers();
	}

	@Override
	public Collection<PlayerState> getStates() {
		return services.getGamePlayer().getStates();
	}

	@Override
	public Player getPlayer(String playerName) {
		return services.getGamePlayer().getPlayer(playerName);
	}

	@Override
	public PlayerState getPlayerState(Player player) {
		return services.getGamePlayer().getPlayerState(player);
	}

	@Override
	public void addPath(Player player, PlayerState state) {
		services.getGamePlayer().addPath(player, state);
	}

	@Override
	public Set<Entry<Player, PlayerState>> getPlayerAndStatesArray() {
		return services.getGamePlayer().getPlayerAndStatesArray();
	}
	
}
