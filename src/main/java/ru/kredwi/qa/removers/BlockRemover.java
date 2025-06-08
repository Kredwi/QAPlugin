package ru.kredwi.qa.removers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockRemover implements IRemover {

	private final Block block;
	
	/**
	 * Block attributes: `Text Display` and other
	 * 
	 * @author Kredwi
	 * */
	private List<IRemover> attributes = new ArrayList<>();
	
	public BlockRemover(Block block) {
		this.block = block;
	}
	
	/**
	 * Delete block attributes and delete this block from the world.
	 * 
	 * @author Kredwi
	 * */
	@Override
	public void remove() {
		for (IRemover attribute : attributes) {
			attribute.remove();
		}
		block.setType(Material.AIR);
	}
	
	public Block getBlock() {
		return block;
	}
	
	public void addAttribute(IRemover attribute) {
		Objects.requireNonNull(attribute);
		
		this.attributes.add(attribute);
	}

}
