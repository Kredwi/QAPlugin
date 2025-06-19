package ru.kredwi.qa.config;

import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public interface QAConfig {
	
	String getAsString(@Nonnull String path);
	boolean getAsBoolean(@Nonnull String path);
	int getAsInt(@Nonnull String path);
	double getAsDouble(@Nonnull String path);
	Sound getAsSound(@Nonnull String path);
	Particle getAsParticle(@Nonnull String path);
	Type getAsFireworkType(@Nonnull String path);
	Color getAsBukkitColor(@Nonnull String path);
	
	
	List<Color> getAsBukkitColorList(@Nonnull String path);
	List<String> getAsStringList(@Nonnull String path);
	void setConfig(@Nonnull FileConfiguration config);
}
