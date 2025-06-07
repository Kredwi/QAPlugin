package ru.kredwi.qa.config;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface QAConfig {
	
	String getAsString(@NonNull String path);
	boolean getAsBoolean(@NonNull String path);
	int getAsInt(@NonNull String path);
	double getAsDouble(@NonNull String path);
	Sound getAsSound(@NonNull String path);
	Particle getAsParticle(@NonNull String path);
	Type getAsFireworkType(@NonNull String path);
	Color getAsBukkitColor(@NonNull String path);
	
	
	List<Color> getAsBukkitColorList(@NonNull String path);
	List<String> getAsStringList(@NonNull String path);
	void setConfig(@NonNull FileConfiguration config);
}
