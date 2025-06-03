package ru.kredwi.qa.sql;

import java.util.UUID;

public interface DatabaseActions {
	void addPlayerWinCount(UUID uuid);
	void setPlayerLastPlayedNow(UUID uuid);
	void addPlayerIfNonExists(UUID uuid);
}
