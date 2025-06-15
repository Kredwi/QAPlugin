package ru.kredwi.qa.game;

import java.util.Set;

public interface IGameAnswer extends ServiceReader {
	void addAnwserCount();
	void resetAnwserCount();
	boolean isAllAnswered();
	void processPlayerAnswers(boolean isInit);
	
	public Set<String> getAlreadyUsedAnswers();
	public void addAlreadyUsedAnswer(String answer);
	public boolean isAlreadyUsedAnswer(String answer);
}
