package ru.kredwi.qa.entity.displaytext;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface IDisplayText {
	public void createTextOnBlock(Block block, char symbol, Location targetLocation);
}
