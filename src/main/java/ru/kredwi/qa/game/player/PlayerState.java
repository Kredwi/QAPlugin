package ru.kredwi.qa.game.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import ru.kredwi.qa.game.AnswerUsedData;
import ru.kredwi.qa.removers.IRemover;

public class PlayerState {
	
	private List<IRemover> playerBuildedBlocks = new ArrayList<>();
	private AnswerUsedData answerUsed = null;
	
	private char[] symbols;
	private boolean answered = false;
	private int buildedBlocks = 1; // builded paths
	private BlockData blockData;
	private int answerCount = 1;
	
	private Location location;
	
	public PlayerState(Location location, BlockData blockData) {
		this.location = location;
		this.blockData = blockData;
	}
	
	public boolean isAnswered() {
		return answered;
	}
	
	public BlockData getBlockData() {
		return blockData;
	}
	
	public void setAnswer(boolean isAnswer) {
		this.answered = isAnswer;
	}
	
	public int getAnswerCount() {
		return answerCount;
	}
	
	public void setAnswerCount(int count) {
		this.answerCount = count;
	}
	
	public Location getLocaton() {
		return location;
	}
	public void setLocaton(Location location) {
		this.location =location;
	}
	
	public int getBuildedBlocks() {
		return buildedBlocks;
	}
	
	public void IncrementBuildedBlock() {
		buildedBlocks++;
	}
	public void addBuildedBlock(int builded) {
		buildedBlocks += builded;
	}
	public void removeBuildedBlock(int deleteBuilded) {
		buildedBlocks -= deleteBuilded;
	}

	public char[] getSymbols() {
		return symbols;
	}

	public void setSymbols(char[] symbols) {
		this.symbols = symbols;
	}

	public List<IRemover> getPlayerBuildedBlocks() {
		return playerBuildedBlocks;
	}

	public synchronized void addPlayerBuildedBlocks(IRemover block) {
		playerBuildedBlocks.add(block);
	}
	
	public void resetState() {
		setAnswerCount(0);
		setAnswer(false);
		setSymbols(new char[] { ' ' });
	}

	public AnswerUsedData getAnswerUsed() {
		return answerUsed;
	}

	public void setAnswerUsed(AnswerUsedData answerUsed) {
		this.answerUsed = answerUsed;
	}
}
