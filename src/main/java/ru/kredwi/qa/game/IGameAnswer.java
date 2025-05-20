package ru.kredwi.qa.game;

public interface IGameAnswer {
	void addAnwserCount();
	void resetAnwserCount();
	boolean isAllAnswered();
	void processPlayerAnswers(boolean isInit);
}
