package de.matzefratze123.api.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectInstance {
	
	private Object instance;
	private Class<?> clazz;
	
	protected ObjectInstance(Object instance) {
		this.instance = instance;
		this.clazz = instance.getClass();
	}
	
	public Object getObjectInstance() {
		return instance;
	}
	
	public void setFieldValue(String name, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			boolean accessible = field.isAccessible();
			
			field.setAccessible(true);
			field.set(instance, value);
			field.setAccessible(accessible);
		} catch (NoSuchFieldException e) {
			throw new ReflectException("Field " + name + " doesn't exists");
		} catch (Exception e) {
			throw new ReflectException("Failed to set field " + name + ": " + e.getMessage());
		}
	}
	
	public Object getFieldValue(String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			
			boolean accessible = field.isAccessible();
			
			field.setAccessible(true);
			Object value = field.get(instance);
			field.setAccessible(accessible);
			
			return value;
		} catch (NoSuchFieldException e) {
			throw new ReflectException("Field " + name + " is not declared in type " + clazz.getSimpleName());
		} catch (Exception e) {
			throw new ReflectException("Failed to get field " + name + ": " + e.getMessage());
		}
	}
	
	public Object invokeMethod(String name, Object... paramArgs) {
		Class<?>[] paramClasses = new Class<?>[paramArgs.length];
		for (int i = 0; i < paramArgs.length; i++) {
			paramClasses[i] = paramArgs[i].getClass();
		}
		
		try {
			Method method = clazz.getDeclaredMethod(name, paramClasses);
			
			boolean accessible = method.isAccessible();
			
			method.setAccessible(true);
			Object returnObject = method.invoke(instance, paramArgs);
			method.setAccessible(accessible);
			
			return returnObject;
		} catch (NoSuchMethodException e) {
			throw new ReflectException("Method " + name + " is not declared in type " + clazz.getSimpleName());
		} catch (Exception e) {
			throw new ReflectException("Failed to invoke method " + name + ": " + e.getMessage());
		}
	}

}
