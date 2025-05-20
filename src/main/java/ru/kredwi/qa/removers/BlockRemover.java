package ru.kredwi.qa.removers;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockRemover implements IRemover {

	private final Block block;
	
	public BlockRemover(Block block) {
		this.block = block;
	}
	
	@Override
	public void remove() {
		block.setType(Material.AIR);
	}

}
