package de.matzefratze123.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
	
	/**
	 * The name of the command
	 */
	String value();
	
	/**
	 * The minimum arguments of the command
	 */
	int minArgs() default -1;
	
	/**
	 * Wether this command can only be used ingame
	 */
	boolean onlyIngame() default false;
	
}
