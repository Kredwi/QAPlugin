package ru.kredwi.qa.callback.data;

import org.bukkit.entity.Player;

public record PlayerAnswerData(Player player, String text) {
	public int getWordLength() {
		return text.length();
	}
}
