package de.matzefratze123.api.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import de.matzefratze123.api.command.transform.BlockDataTransformer;
import de.matzefratze123.api.command.transform.BlockDataTransformer.BlockData;
import de.matzefratze123.api.command.transform.BooleanTransformer;
import de.matzefratze123.api.command.transform.DefaultTransformer;
import de.matzefratze123.api.command.transform.DoubleTransformer;
import de.matzefratze123.api.command.transform.IntegerTransformer;
import de.matzefratze123.api.command.transform.PlayerTransformer;
import de.matzefratze123.api.command.transform.Transformer;

public class Argument<T> {

	public static Map<Class<?>, Transformer<?>> transformers;
	
	static {
		if (transformers == null) {
			transformers = new HashMap<Class<?>, Transformer<?>>();
			transformers.put(BlockData.class, new BlockDataTransformer());
			transformers.put(Player.class, new PlayerTransformer());
			transformers.put(Double.class, new DoubleTransformer());
			transformers.put(Integer.class, new IntegerTransformer());
			transformers.put(Boolean.class, new BooleanTransformer());
			transformers.put(String.class, new DefaultTransformer());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <V> Transformer<V> getTransformer(Class<V> clazz) {
		for (Entry<Class<?>, Transformer<?>> entry : transformers.entrySet()) {
			if (entry.getKey() == clazz) {
				return (Transformer<V>)entry.getValue();
			}
		}
		
		return null;
	}
	
	private T value;
	
	public Argument(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
}
