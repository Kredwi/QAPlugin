package ru.kredwi.qa.utils;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.ConfigAs;

public class AsObjectUtils<T extends Enum<T>> {
	
	private ConfigAs config;
	
	public AsObjectUtils(ConfigAs config) {
		this.config = config;
	}
	
	public T getAsGeneric(String path, Class<T> enumClass, T defaultValue) {
		String value =config.getAsString(path);
		try {
			return Enum.valueOf(enumClass, value);
		} catch (NullPointerException | IllegalArgumentException e) {
			QAPlugin.getQALogger().warning(value + " is not found in Bukkit API. Used default " + defaultValue.name());
			return defaultValue;
		}
	}
}