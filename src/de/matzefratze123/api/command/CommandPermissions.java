package de.matzefratze123.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandPermissions {
	
	/**
	 * The permissions in form of a string array
	 */
	String[] value();
	
}
