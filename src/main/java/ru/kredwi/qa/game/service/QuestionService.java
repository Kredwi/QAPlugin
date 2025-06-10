package ru.kredwi.qa.game.service;

import static ru.kredwi.qa.config.ConfigKeys.DEBUG;
import static ru.kredwi.qa.config.ConfigKeys.QUESTIONS;
import static ru.kredwi.qa.config.ConfigKeys.QUESTIONS_ARE_OVER;
import static ru.kredwi.qa.config.ConfigKeys.TEMPLATE_QUESTION;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.QuestionsAreOverException;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IGameQuestionManager;
import ru.kredwi.qa.game.service.data.Question;

public class QuestionService implements IGameQuestionManager{
	
	private Map<Question, Boolean> questions = new HashMap<>();
	
	private QAConfig cm;
	private IGamePlayer playerService;
	private boolean serviceReady = false;
	
	public QuestionService(QAConfig cm, IGamePlayer playerService) {
		this.cm = cm;
		this.playerService = playerService;
	}
	
	@Override
	public void loadQuestions() {
		List<String> questions = cm.getAsStringList(QUESTIONS);
		
		if (Objects.nonNull(questions) && !questions.isEmpty()) {
			int questionLoaded = 0;
			
			for (String question : questions) {
				if (Objects.isNull(questions)) continue;
				
				this.questions.put(new Question(this.questions.size(), question), false);
				
				questionLoaded++;
			}
			
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("Question count loaded: " + questionLoaded);
			}
			this.serviceReady = true;
		} else {
			QAPlugin.getQALogger().severe("ERROR OF LOADING QUESTIONS !!!");
			
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().warning("questions is null? " + (questions == null));
			}
		}
	}
	
	/**
	 * Getting new question from question list.
	 * 
	 * @throws QuestionsAreOverException if all questions is used
	 * 
	 * @author Kredwi
	 * */
	@Override
	public Question getRandomQuestion() throws QuestionsAreOverException {
		if (Objects.isNull(this.questions) || this.questions.isEmpty()) {
			if (cm.getAsBoolean(DEBUG)) {
				QAPlugin.getQALogger().info("None any questions. HashMap is empty!");
			}
			throw new QuestionsAreOverException("None any questions. HashMap is empty!");
		}
		
		List<Question> availableQuestions = this.questions.entrySet().stream()
				.filter(Predicate.not(Map.Entry::getValue))
				.map(Map.Entry::getKey)
				.toList();
		
		if (availableQuestions.isEmpty()) {
			throw new QuestionsAreOverException("All questions is already used.");
		}
		
		int questionIndex = QAPlugin.RANDOM.nextInt(availableQuestions.size());
		
		Question question = availableQuestions.get(questionIndex);
		
		// question is used
		this.questions.put(question, true);
		
		return question;
	}

	@Override
	public Question addNewQuestion(@NonNull String questionText) {
		Objects.requireNonNull(questionText);
		
		Question question = new Question(questions.size(), questionText);
		
		// add and set to status "Used"
		this.questions.put(question, true);
		
		return question;
	}
	
	@Override
	public void questionAllPlayers() {
		try {
			this.questionAllPlayersOfQuestion(getRandomQuestion());	
		} catch (QuestionsAreOverException e) {
			sendMessagesForPlayers(cm.getAsString(QUESTIONS_ARE_OVER));
		}
	}

	@Override
	public void questionAllPlayersOfQuestion(Question question) {
		String message = MessageFormat.format(cm.getAsString(TEMPLATE_QUESTION), question.question());
		sendMessagesForPlayers(message);
	}
	
	private void sendMessagesForPlayers(String message) {
		playerService.getPlayers()
			.forEach(e -> e.sendMessage(message));		
	}

	@Override
	public boolean isServiceReady() {
		return serviceReady;
	}
	
}
