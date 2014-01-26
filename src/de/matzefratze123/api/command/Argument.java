package de.matzefratze123.api.command;

public class Argument<T> {
	
	private T value;
	
	public Argument(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
}
