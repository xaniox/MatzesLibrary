package de.matzefratze123.api.reflect;

import java.lang.reflect.Constructor;

public class ClassAdapter {
	
	private boolean validated;
	
	private Class<?> clazz;
	private String name;
	
	public ClassAdapter(MCPackage pack, String name) {
		this(pack + "." + name);
	}
	
	public ClassAdapter(String name) {
		this.name = name;
		
		try {
			clazz = Class.forName(name);
			validated = true;
		} catch (Exception e) {
			validated = false;
		}
	}
	
	public ClassAdapter(Class<?> clazz) {
		this.name = clazz.getSimpleName();
		this.clazz = clazz;
		
		validated = true;
	}
	
	public String getClassName() {
		return name;
	}

	public ObjectInstance newInstance(Object... paramArgs) {
		validateState();
		
		Class<?>[] paramClasses = new Class<?>[paramArgs.length];
		for (int i = 0; i < paramArgs.length; i++) {
			paramClasses[i] = paramArgs[i].getClass();
		}
		
		try {
			Constructor<?> con = clazz.getConstructor(paramClasses);
			
			Object instance = con.newInstance(paramArgs);
			return new ObjectInstance(instance);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Unable to find class constructor. Invalid arguments: " + e.getMessage());
		} catch (SecurityException e) {
			throw new IllegalStateException("Unable to access class " + name + ": " + e.getMessage());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create a new instance of " + clazz.getName() + ": " + e.getMessage());
		}
	}

	public Class<?> getRawClass() {
		return clazz;
	}
	
	public boolean isValidated() {
		return validated;
	}

	private void validateState() {
		if (!validated) {
			throw new IllegalStateException("Class " + name + " doesn't exists!");
		}
	}
	
}
