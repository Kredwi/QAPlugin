package ru.kredwi.qa.game;

import org.checkerframework.checker.nullness.qual.NonNull;

import ru.kredwi.qa.exceptions.QuestionsAreOverException;
import ru.kredwi.qa.game.service.data.Question;

/**
 * Interface for managing questions used in games.
 * Provides methods for sending questions to players, tracking used questions,
 * and obtaining information about the number of used questions.
 *
 * @author Kredwi
 */
public interface IGameQuestionManager extends ServiceReader {

	void loadQuestions();
	
	Question getRandomQuestion() throws QuestionsAreOverException;
	
	void questionAllPlayers();
	
	void questionAllPlayersOfQuestion(Question question);
	
	Question addNewQuestion(@NonNull String textOfQuestion);
}
