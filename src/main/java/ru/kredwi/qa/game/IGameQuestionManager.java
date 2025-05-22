package ru.kredwi.qa.game;

import ru.kredwi.qa.exceptions.QuestionsAreOverException;

/**
 * Interface for managing questions used in games.
 * Provides methods for sending questions to players, tracking used questions,
 * and obtaining information about the number of used questions.
 *
 * @author Kredwi
 */
public interface IGameQuestionManager {

    /**
     * Sends a random question to all players.
     * The question is selected from a list of available questions. If all questions have been used,
     * a {@link QuestionsAreOverException} is thrown.
     *
     * @throws QuestionsAreOverException If all available questions have already been used.
     */
    void questionPlayers() throws QuestionsAreOverException;

    /**
     * Sends the specified question to all players.
     * If all questions have been used, a {@link QuestionsAreOverException} is thrown.
     *
     * @param question The text of the question to send to players.
     * @throws QuestionsAreOverException If all available questions have already been used.
     */
    void questionPlayers(String question) throws QuestionsAreOverException;

    /**
     * Checks if the question with the specified index has already been used in the current game.
     *
     * @param questionIndex The index of the question in the list of questions.
     * @return {@code true} if the question with the given index has already been used, {@code false} otherwise.
     */
    boolean questionIsUsed(int questionIndex);

    /**
     * Marks the question with the specified index as used.
     * Adds the index of the question to the list of used questions.
     *
     * @param usedText The index of the used question in the list of questions.
     */
    void addUsedQuestion(int usedText);

    /**
     * Returns the number of questions that have already been used in the current game.
     *
     * @return The number of used questions.
     */
    int usedQuestionSize();
}
