package ru.kredwi.qa.game.service;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.exceptions.QuestionsAreOverException;
import ru.kredwi.qa.game.IGamePlayer;
import ru.kredwi.qa.game.IGameQuestionManager;
import ru.kredwi.qa.game.impl.Questions;

public class QuestionService implements IGameQuestionManager{

	private Set<Integer> usedTexts = new HashSet<>();
	private IGamePlayer playersService;
	private IGameQuestionManager questionService;
	
	public QuestionService(IGamePlayer playersService, IGameQuestionManager questionService) {
		this.playersService = playersService;
		this.questionService =questionService;
	}
	
	@Override
	public void questionPlayers() {
		try {
			
			int i = Questions.getInstance().getTextIndex(questionService);
			this.questionPlayers(Questions.getInstance().getQuestions(i));
			
		} catch (QuestionsAreOverException e) {
			
			if (QAConfig.DEBUG.getAsBoolean()) {
				QAPlugin.getQALogger().info("QUESTIONS ARE OVER EXCEPTION " + e.getMessage());
			}
			
			for (Player player : playersService.getPlayers()) {
				player.sendMessage(QAConfig.QUESTIONS_ARE_OVER.getAsString());
			}
			
		}
	}
	
	@Override
	public void questionPlayers(String question) {
		for (Player player : playersService.getPlayers()) {
			Questions.getInstance().executeQuestion(player, question);
		}
	}

	@Override
	public boolean questionIsUsed(int questionIndex) {
		return usedTexts.contains(questionIndex);
	}

	@Override
	public void addUsedQuestion(int usedText) {
		this.usedTexts.add(usedText);
	}
	
	@Override
	public int usedQuestionSize() {
		return this.usedTexts.size();
	}

}
