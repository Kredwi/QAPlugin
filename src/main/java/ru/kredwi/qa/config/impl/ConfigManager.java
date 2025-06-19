package ru.kredwi.qa.config.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import ru.kredwi.qa.QAPlugin;
import ru.kredwi.qa.config.ConfigKeys;
import ru.kredwi.qa.config.QAConfig;
import ru.kredwi.qa.utils.EnumConverter;

public class ConfigManager implements QAConfig {

	private FileConfiguration config;
	
	public ConfigManager(FileConfiguration config) {
		this.config  = config;
	}

	@Override
	public String getAsString(@Nonnull String path) {
		return config.getString(path);
	}

	@Override
	public boolean getAsBoolean(@Nonnull String path) {
		return config.getBoolean(path);
	}

	@Override
	public int getAsInt(@Nonnull String path) {
		return config.getInt(path);
	}

	@Override
	public double getAsDouble(@Nonnull String path) {
		return config.getDouble(path);
	}

	@Override
	public Sound getAsSound(@Nonnull String path) {
		return new EnumConverter<Sound>(this)
				.getAsGeneric(path, Sound.class, Sound.BLOCK_LEVER_CLICK);
	}

	@Override
	public Particle getAsParticle(@Nonnull String path) {
		return new EnumConverter<Particle>(this)
				.getAsGeneric(path, Particle.class, Particle.DRIP_LAVA);
	}

	@Override
	public Type getAsFireworkType(@Nonnull String path) {
		return new EnumConverter<Type>(this)
				.getAsGeneric(path, Type.class, Type.STAR);
	}

	@Override
	public Color getAsBukkitColor(@Nonnull String path) {
		return config.getColor(path);
	}
	
	@Override
	public List<String> getAsStringList(@Nonnull String path) {
		return config.getStringList(path);
	}

	public List<Color> getAsBukkitColorList(@Nonnull String path) {
		List<Color> colors = new ArrayList<>();
		
		for (Object item : config.getList(path)) {
			if (item instanceof List<?>) {
				List<?> list = (List<?>) item;
				if (list.size() == 3
						&& list.get(0) instanceof Integer
						&& list.get(1) instanceof Integer
						&& list.get(2) instanceof Integer) {
					int red = (int) list.get(0);
					int green = (int) list.get(1);
					int blue = (int) list.get(2);
					colors.add(Color.fromRGB(red, green, blue));
				} else {
					if (getAsBoolean(ConfigKeys.DEBUG)) {
						QAPlugin.getQALogger().warning("Error rbg syntax invalid!");
					}
				}
			} else {
				if (getAsBoolean(ConfigKeys.DEBUG)) {
					QAPlugin.getQALogger().warning("Error rbg type invalid! Number is not number");
				}
			}
		}
		
		if (colors.isEmpty()) return List.of(getDefaultBukkitColor("all colors"));
		
		return colors;
	}
	
	private Color getDefaultBukkitColor(String name) {
		final Color defaultColor = Color.BLACK;
		if (getAsBoolean(ConfigKeys.DEBUG)) {
			QAPlugin.getQALogger().info(name + " is not found in Bukkit API. Used default Color.BLACK");
		}
		return defaultColor;
	}

	@Override
	public void setConfig(@Nonnull FileConfiguration config) {
		this.config = config;
	}
	

	
}
