package ru.kredwi.qa.game.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.QuestionsAreOverException;
import ru.kredwi.qa.game.IGameQuestionManager;

public class Questions {
	
	private static Questions INSTANCE;
	private List<String> questions;
	
	private Questions() {};
	
	public static Questions getInstance() {
		if (INSTANCE == null) {
			
			List<String> questions = QAConfig.QUESTIONS.getAsStringList();
			
			if (questions != null && questions.size() > 0) {
				INSTANCE = new Questions(questions);
			} else {

				QAPlugin.getQALogger().severe("ERROR OF LOADING QUESTIONS !!!");

				INSTANCE = new Questions(new ArrayList<String>(0));

				if (QAConfig.DEBUG.getAsBoolean()) {
					QAPlugin.getQALogger().warning("questions is null? " + (questions == null));
				}
			}
		}
		return INSTANCE;
	}
	
	private Questions(List<String> questions) {
		QAPlugin.getQALogger().info("Question count loaded: " + questions.size());
		this.questions = questions;
	}
	
	/**
	 * @param questionService for checking: Question is used?
	 * @throws QuestionsAreOverException if all questions is used
	 * @author Kredwi
	 * */
	public int getTextIndex(IGameQuestionManager questionService) throws QuestionsAreOverException {
		for (int i = 0; i< questions.size(); i++) {
			int randomNumber = QAPlugin.RANDOM.nextInt(questions.size());
			if (questionService.questionIsUsed(randomNumber)) {
				continue;
			}
			if (questionService.usedQuestionSize() >= questions.size()) {
				throw new QuestionsAreOverException("All questions are over");
			}
		
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("QUESTION ID: " + randomNumber);
			}
			
			questionService.addUsedQuestion(randomNumber);
			
			return randomNumber;
		}
		throw new QuestionsAreOverException("All questions are over");
	}
	
	public String getQuestions(int i) throws QuestionsAreOverException{
		if (questions.size() <= i) {
			throw new QuestionsAreOverException();
		}
		return questions.get(i);
	}	
	public void executeQuestion(Player player, String question) {
		player.sendMessage(MessageFormat.format(QAConfig.TEMPLATE_QUESTION.getAsString(), question));
	}
}
