package ru.kredwi.qa.game;

import ru.kredwi.qa.exceptions.QuestionsAreOverException;

public interface IGameQuestionManager {
	void questionPlayers() throws QuestionsAreOverException;
	void questionPlayers(String question) throws QuestionsAreOverException;
	
	boolean questionIsUsed(int questionIndex);
	void addUsedQuestion(int usedText);
	int usedQuestionSize();
}
