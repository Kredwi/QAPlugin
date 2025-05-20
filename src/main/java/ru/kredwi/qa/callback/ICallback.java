package ru.kredwi.qa.callback;

public interface ICallback {
	
	/**
	 * Callback runner for ANY event
	 * @param o is ANY event data method (IF DATA IS NOT NEEDED IN THE METHOD IS OBJECT IS NULL!!!)
	 * @author Kredwi
	 * */
	public void run(Object o);
}
