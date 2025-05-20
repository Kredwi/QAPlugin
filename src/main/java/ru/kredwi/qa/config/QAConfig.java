package ru.kredwi.qa.config;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ru.kredwi.qa.QAPlugin;

public enum QAConfig {
	
	VERSION("version"),
	
	DEBUG("debug"),
	ENABLED_BLOCKS("enabled-blocks"),
	QUESTIONS("questions"),
	DELETE_BLOCKS_WHEN_DISABLE("delete-blocks-when-disable"),
	
	TELEPORT_PLAYER_IN_PLACE("teleport-player-in-place"),
	CENTER_DIRECTION_IN_TELEPORT("center-direction-in-teleport"),
	
	WIN_SOUND("win-sound"),
	BLOCK_PLACE_SOUND("block-place-sound"),
	
	BUILD_PERIOD("build-period"),
	BUILD_DELAY("build-delay"),
	
	TEMPLATE_QUESTION("question-template"),
	QUESTIONS_ARE_OVER("questions-are-over"),
	
	NO_ARGS("no-args"),
	YOU_NOT_CONNECTED_TO_GAME("you-not-connected-to-game"),
	GAME_IS_NOT_STARTED("game-is-not-started"),
	ALREADY_ANSWER("already-answer"), 
	YOU_ANSWER("you-answer"),
	PLAYER_ANSWER("player-answer"),
	YOU_DONT_HAVE_GAME_REQUESTS("you-dont-have-game-requests"),
	THIS_GAME_IS_NOT_REQUESTED_YOU("this-game-is-not-requested-you"),
	YOU_CONNECTED_TO("you-connected-to"),
	COMMAND_ONLY_FOR_PLAYERS("command-only-for-player"),
	IN_ARGUMENT_NEEDED_NUMBER("in-argument-needed-number"),
	GAME_IS_CREATED("game-is-created"),
	IS_GAME_ALREADY_CREATED("is-game-already-created"),
	GAME_NOT_FOUND("game-not-found"),
	UNKOWN_PROBLEM_WITH_GAME_DELETE("unkown-problem-with-game-delete"),
	GAME_DELETE("game-delete"),
	YOU_DONT_GAME_OWNER("you-dont-game-owner"),
	IS_GAME_OWNER("is-game-owner"),
	IS_PLAYER_IS_NOT_FOUND("is-player-is-not-found"),
	YOU_HAVE_NEW_GAME_REQUESTS("you-have-new-game-requests"),
	IS_COMMAND_ONLYE_FOR_GAME_OWNER("is-command-only-for-game-owner"),
	IN_THE_GAME_NOT_FOUND_PATHS("in-the-game-not-found-paths"),
	PLAYERS_WIN_GAME("players-win-game"),
	PLAYER_WIN_GAME("player-win-game"),
	GAME_ADD_PLAYER_ARELADY_STARTED("game-add-player-already-started"),
	PATH_CREATED("path-created"),
	REQUESTS_SENDED("requests-sended"),
	YOU_ALREADY_CREATE_YOUR_GAME("you-already-create-your-game"),
	PLAYER_ACCEPTED_REQUESTS("player-accepted-requests");
	
	private final FileConfiguration config = JavaPlugin.getPlugin(QAPlugin.class).getConfig();
	private String path;
	
	private QAConfig(String path) {
		this.path = path.toLowerCase();
	}
	
	public String getAsString() {
		return config.getString(path);
	}
	
	public boolean getAsBoolean() {
		return config.getBoolean(path);
	}
	
	public int getAsInt() {
		return config.getInt(path);
	}
	
	public double getAsDouble() {
		return config.getDouble(path);
	}
	
	public Sound getAsSound() {
		String value =config.getString(path);
		Sound sound = Sound.valueOf(value);
		if (sound == null) {
			QAPlugin.getQALogger().info(value + " is not found in Bukkit API. Used default BLOCK_LEVER_CLICK");
			return Sound.BLOCK_LEVER_CLICK;
		}
		return sound;
	}
	
	public List<String> getAsStringList() {
		return config.getStringList(path);
	}
}
