package ru.kredwi.qa.game.service;

import java.util.Set;

import javax.annotation.Nonnull;

public interface IGameAnswer extends ReadyService {
	void addAnwserCount();
	void resetAnwserCount();
	boolean isAllAnswered();
	void processPlayerAnswers(boolean isInit);
	
	public Set<String> getAlreadyUsedAnswers();
	public void addAlreadyUsedAnswer(@Nonnull String answer);
	public boolean isAlreadyUsedAnswer(@Nonnull String answer);
}
