package de.matzefratze123.api.reflect;

public class ReflectException extends RuntimeException {

	private static final long	serialVersionUID	= -6570598503332273027L;
	private String exactMessage;
	
	public ReflectException(String message) {
		super(message);
		
		this.exactMessage = message;
	}
	
	@Override
	public String getMessage() {
		return exactMessage;
	}

}
