package ru.kredwi.qa.commands.base;

import org.bukkit.plugin.Plugin;

import ru.kredwi.qa.game.IMainGame;

public interface ICommandController {
	public Plugin getPlugin();
	public IMainGame getMainGame();
}
