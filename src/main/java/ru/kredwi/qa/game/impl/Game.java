package ru.kredwi.qa.game.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.game.IBlockConstructionService;
import ru.kredwi.qa.game.IGame;
import ru.kredwi.qa.game.IGameAnswer;
import ru.kredwi.qa.game.IGameQuestionManager;
import ru.kredwi.qa.game.IMainGame;
import ru.kredwi.qa.game.IWinnerService;
import ru.kredwi.qa.game.service.BlockConstructionService;
import ru.kredwi.qa.game.service.GameAnswerService;
import ru.kredwi.qa.game.service.QuestionService;
import ru.kredwi.qa.game.service.WinnerService;
import ru.kredwi.qa.game.state.PlayerState;
import ru.kredwi.qa.removers.IRemover;

public class Game implements IGame {
	
	private IGameQuestionManager questionManager;
	private IGameAnswer gameAnswer;
	private IBlockConstructionService blockConstructionService;
	private IWinnerService winnerService;
	
	private final GameInfo gameInfo;
	
	private Map<Player, PlayerState> states = new HashMap<>();
	
	private boolean isStart;
	
	public Game(String name, Player owner, int blocksToWin, QAPlugin plugin) {
		this.questionManager = new QuestionService(this);
		this.gameAnswer = new GameAnswerService(this, plugin);
		this.blockConstructionService = new BlockConstructionService(this, plugin);
		this.winnerService = new WinnerService(this);
		
		this.gameInfo = new GameInfo(name.trim().toLowerCase(),
				owner, blocksToWin);
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
	
	
	
	@Override
	public void addPath(Player player, PlayerState state) {
		this.states.put(player, state);
	}
	
	@Override
	public GameInfo getGameInfo() {
		return gameInfo;
	}
	
	@Override
	public Player getPlayer(String playerName) {
		
		if (Objects.isNull(playerName)) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("playerName in Game.getPlayer(String) is NULL");
			}
			return null;
		}
		
		List<Player> players = this.states.keySet().stream()
				.filter(e -> e.getName().equalsIgnoreCase(playerName))
				.toList();
		
		if (players.size() < 1) {
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("size players in Game.getPlayer(String) is 0!");
			}
			return null;
		}
		
		return players.get(0);
	}
	
	@Override
	public PlayerState getPlayerState(Player player) {
		return this.states.get(player);
	}
	
	@Override
	public Collection<PlayerState> getStates() {
		return states.values();
	}
	@Override
	public Set<Player> getPlayers() {
		return states.keySet();
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
	public Set<Map.Entry<Player, PlayerState>> getPlayerAndStatesArray() {
		// TODO Auto-generated method stub
		return states.entrySet();
	}
	
	
	
	
	
	@Override
	public boolean endGameIfHaveWinner(IMainGame mainGame) {
		return winnerService.endGameIfHaveWinner(mainGame);
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
		// TODO Auto-generated method stub
		gameAnswer.addAnwserCount();
	}

	@Override
	public void resetAnwserCount() {
		// TODO Auto-generated method stub
		gameAnswer.resetAnwserCount();
	}

	@Override
	public boolean isAllAnswered() {
		// TODO Auto-generated method stub
		return gameAnswer.isAllAnswered();
	}

	@Override
	public void processPlayerAnswers(boolean isInit) {
		// TODO Auto-generated method stub
		gameAnswer.processPlayerAnswers(isInit);
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
		// TODO Auto-generated method stub
		return blockConstructionService.getSummaryBuildedBlocks();
	}

	@Override
	public List<BukkitTask> getBuildedTasks() {
		// TODO Auto-generated method stub
		return blockConstructionService.getBuildedTasks();
	}

	@Override
	public boolean buildIsStopped() {
		// TODO Auto-generated method stub
		return blockConstructionService.buildIsStopped();
	}

	@Override
	public void setStopBuild(boolean isStop) {
		// TODO Auto-generated method stub
		blockConstructionService.setStopBuild(isStop);
	}
	
}
