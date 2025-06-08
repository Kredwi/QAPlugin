package ru.kredwi.qa.entity.displaytext;

import org.bukkit.Location;

import ru.kredwi.qa.removers.BlockRemover;

public interface IDisplayText {
	public void createTextOnBlock(BlockRemover blockRemover, char symbol, Location targetLocation);
}
