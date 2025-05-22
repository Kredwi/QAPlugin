package ru.kredwi.qa.game.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameAnswer;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IGameQuestionManager;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.player.PlayerState;
import ru.kredwi.qa.game.service.BlockConstructionService;
import ru.kredwi.qa.game.service.GameAnswerService;
import ru.kredwi.qa.game.service.GamePlayerService;
import ru.kredwi.qa.game.service.QuestionService;
import ru.kredwi.qa.game.service.WinnerService;
import ru.kredwi.qa.removers.IRemover;

public class Game implements IGame {
	
	private IGameQuestionManager questionManager;
	private IGameAnswer gameAnswer;
	private IBlockConstructionService blockConstructionService;
	private IWinnerService winnerService;
	private IGamePlayer gamePlayer;
	
	private final GameInfo gameInfo;
	
	private boolean isStart;
	
	public Game(String name, Player owner, int blocksToWin, QAPlugin plugin) {
		this.questionManager = new QuestionService(this,this);
		this.gameAnswer = new GameAnswerService(this, plugin);
		this.blockConstructionService = new BlockConstructionService(this, plugin);
		this.winnerService = new WinnerService(this);
		this.setGamePlayer(new GamePlayerService());
		
		this.gameInfo = new GameInfo(name.trim().toLowerCase(),
				owner.getUniqueId(), blocksToWin);
	}
	
	public void setQuestionManager(IGameQuestionManager questionManager) {
		this.questionManager = questionManager;
	}
	
	public void setGameAnswer(IGameAnswer gameAnswer) {
		this.gameAnswer = gameAnswer;
	}
	
	public void setBlockConstructionService(IBlockConstructionService blockConstructionService) {
		this.blockConstructionService = blockConstructionService;
	}
	
	public void setWinnerService(IWinnerService winnerService) {
		this.winnerService = winnerService;
	}
	public void setGamePlayer(IGamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
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
		questionManager.questionPlayers();
	}
	
	@Override
	public void questionPlayers(String question) {
		questionManager.questionPlayers(question);
	}
	

	@Override
	public void addAnwserCount() {
		gameAnswer.addAnwserCount();
	}

	@Override
	public void resetAnwserCount() {
		gameAnswer.resetAnwserCount();
	}

	@Override
	public boolean isAllAnswered() {
		return gameAnswer.isAllAnswered();
	}

	@Override
	public void processPlayerAnswers(boolean isInit) {
		gameAnswer.processPlayerAnswers(isInit);
	}
	
	@Override
	public void alertOfPlayersWin() {
		winnerService.alertOfPlayersWin();
	}

	@Override
	public boolean isPlayerWin(PlayerState state) {
		return winnerService.isPlayerWin(state);
	}
	
	@Override
	public void deleteBuildedBlocks() {
		blockConstructionService.deleteBuildedBlocks();
	}
	
	@Override
	public int getBuildComplete() {
		return blockConstructionService.getBuildComplete();
	}
	
	@Override
	public void addBuildComplete() {
		blockConstructionService.addBuildComplete();
	}
	
	@Override
	public void resetBuildComplete() {
		blockConstructionService.resetBuildComplete();
	}
	
	@Override
	public List<Player> getWinners() {
		return winnerService.getWinners();
	}
	
	@Override
	public void addWinner(Player player) {
		winnerService.addWinner(player);
	}

	@Override
	public boolean questionIsUsed(int questionIndex) {
		return questionManager.questionIsUsed(questionIndex);
	}

	@Override
	public void addUsedQuestion(int usedText) {
		questionManager.addUsedQuestion(usedText);
	}
	
	@Override
	public int usedQuestionSize() {
		return questionManager.usedQuestionSize();
	}
	
	@Override
	public BlockData getRandomBlockData() {
		return blockConstructionService.getRandomBlockData();
	}

	@Override
	public Set<IRemover> getSummaryBuildedBlocks() {
		return blockConstructionService.getSummaryBuildedBlocks();
	}

	@Override
	public List<BukkitTask> getBuildedTasks() {
		return blockConstructionService.getBuildedTasks();
	}

	@Override
	public boolean buildIsStopped() {
		return blockConstructionService.buildIsStopped();
	}

	@Override
	public void setStopBuild(boolean isStop) {
		blockConstructionService.setStopBuild(isStop);
	}

	@Override
	public Set<Player> getPlayers() {
		return gamePlayer.getPlayers();
	}

	@Override
	public Collection<PlayerState> getStates() {
		return gamePlayer.getStates();
	}

	@Override
	public Player getPlayer(String playerName) {
		return gamePlayer.getPlayer(playerName);
	}

	@Override
	public PlayerState getPlayerState(Player player) {
		return gamePlayer.getPlayerState(player);
	}

	@Override
	public void addPath(Player player, PlayerState state) {
		gamePlayer.addPath(player, state);
	}

	@Override
	public Set<Entry<Player, PlayerState>> getPlayerAndStatesArray() {
		return gamePlayer.getPlayerAndStatesArray();
	}
	
}
