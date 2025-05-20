package ru.kredwi.qa.removers;

import org.bukkit.entity.Display;

public class DisplayRemover implements IRemover {

	private Display display;
	
	public DisplayRemover(Display display) {
		this.display = display;
	}
	
	@Override
	public void remove() {
		display.remove();
	}

}
