package ru.kredwi.qa.game;

public interface IGameAnswer extends ServiceReader {
	void addAnwserCount();
	void resetAnwserCount();
	boolean isAllAnswered();
	void processPlayerAnswers(boolean isInit);
}
