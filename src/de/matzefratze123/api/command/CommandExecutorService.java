package de.matzefratze123.api.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.matzefratze123.api.command.transform.BlockDataTransformer;
import de.matzefratze123.api.command.transform.BlockDataTransformer.BlockData;
import de.matzefratze123.api.command.transform.BooleanTransformer;
import de.matzefratze123.api.command.transform.DefaultTransformer;
import de.matzefratze123.api.command.transform.DoubleTransformer;
import de.matzefratze123.api.command.transform.IntegerTransformer;
import de.matzefratze123.api.command.transform.PlayerTransformer;
import de.matzefratze123.api.command.transform.Transformer;

public class CommandExecutorService implements CommandExecutor {
	
	private TransformerMap transformers;
	
	private final String rootCommand;
	private final JavaPlugin plugin;
	
	private RootCommandExecutor rootCommandExecutor;
	private final Map<CommandListener, ExecuteableMethod[]> listeners;
	
	public CommandExecutorService(String rootCommand, JavaPlugin plugin) {
		this.rootCommand = rootCommand;
		this.plugin = plugin;
		this.listeners = new HashMap<CommandListener, ExecuteableMethod[]>();
		
		final PluginCommand cmd = plugin.getCommand(rootCommand);
		Validate.notNull(cmd, "Plugin " + plugin.getName() + " does not declare command " + rootCommand);
		
		cmd.setExecutor(this);
		
		initTransformers();
	}
	
	private void initTransformers() {
		transformers = new TransformerMap();
		transformers.put(BlockData.class, new BlockDataTransformer());
		transformers.put(Player.class, new PlayerTransformer());
		transformers.put(Double.class, new DoubleTransformer());
		transformers.put(Integer.class, new IntegerTransformer());
		transformers.put(Boolean.class, new BooleanTransformer());
		transformers.put(String.class, new DefaultTransformer());
	}
	
	public <V> Transformer<V> getTransformer(Class<V> clazz) {
		return transformers.get(clazz);
	}
	
	public <V> void registerTransformer(Class<V> clazz, Transformer<V> transformer) {
		transformers.put(clazz, transformer);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase(rootCommand)) {
			//Catch other commands which were registered by other plugins on this executor
			return true;
		}
		
		String[] argsCut = new String[args.length == 0 ? 0 : args.length - 1];
		for (int i = 1; i < args.length; i++) {
			argsCut[i - 1] = args[i];
		}
		
		if (args.length == 0) {
			if (rootCommandExecutor != null) {
				rootCommandExecutor.execute(sender);
			}
		} else {
			for (Entry<CommandListener, ExecuteableMethod[]> entry : listeners.entrySet()) {
				ExecuteableMethod[] methods = entry.getValue();
				
				for (ExecuteableMethod method : methods) {
					System.out.println(method.getMethod().getName());
					System.out.println(args[0]);
					if (!method.getName().equalsIgnoreCase(args[0])) {
						continue;
					}
					
					method.execute(sender, argsCut, transformers);
				}
			}
		}
		
		return true;
	}
	
	public void registerListener(CommandListener listener) {
		Validate.notNull(listener, "listener cannot be null");
		
		if (listeners.containsKey(listener)) {
			return;
		}
		
		ExecuteableMethod[] methods = ExecuteableMethod.findListenerMethods(listener);
		listeners.put(listener, methods);
	}
	
	public void unregister(CommandListener listener) {
		if (!listeners.containsKey(listener)) {
			return;
		}
		
		listeners.remove(listener);
	}
	
	public void setRootCommandExecutor(RootCommandExecutor executor) {
		this.rootCommandExecutor = executor;
	}
	
	public String getRootCommand() {
		return rootCommand;
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public static class TransformerMap {
		
		private HashMap<Class<?>, Transformer<?>> map;
		
		public TransformerMap() {
			map = new HashMap<Class<?>, Transformer<?>>();
		}
		
		@SuppressWarnings("unchecked")
		public <V> Transformer<V> put(Class<V> clazz, Transformer<V> transformer) {
			return (Transformer<V>) map.put(clazz, transformer);
		}
		
		@SuppressWarnings("unchecked")
		public <V> Transformer<V> get(Class<V> clazz) {			
			return (Transformer<V>)map.get(clazz);
		}
		
		public boolean contains(Class<?> clazz) {
			return map.containsKey(clazz);
		}
		
	}

}
