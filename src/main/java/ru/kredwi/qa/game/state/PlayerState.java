package ru.kredwi.qa.game.state;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import ru.kredwi.qa.removers.IRemover;

public class PlayerState {
	
	private Set<IRemover> playerBuildedBlocks = new HashSet<>();
	
	private char[] symbols;
	private boolean answered = false;
	private int buildedBlocks = 1;
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
	
	public int getBuildedBlocks() {
		return buildedBlocks;
	}
	
	public void IncrementBuildedBlock() {
		buildedBlocks++;
	}
	public void addBuildedBlock(int builded) {
		buildedBlocks += builded;
	}

	public char[] getSymbols() {
		return symbols;
	}

	public void setSymbols(char[] symbols) {
		this.symbols = symbols;
	}

	public Set<IRemover> getPlayerBuildedBlocks() {
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
}
