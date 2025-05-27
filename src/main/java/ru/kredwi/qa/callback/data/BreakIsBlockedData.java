package ru.kredwi.qa.callback.data;

import org.bukkit.Location;
import org.bukkit.block.Block;

public record BreakIsBlockedData(
			Block block,
			Location location
		) {

}
