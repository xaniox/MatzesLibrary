package de.matzefratze123.api.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandExecutorService implements CommandExecutor {
	
	private final String rootCommand;
	private final JavaPlugin plugin;
	
	private final Map<CommandListener, ExecuteableMethod[]> listeners;
	
	public CommandExecutorService(String rootCommand, JavaPlugin plugin) {
		this.rootCommand = rootCommand;
		this.plugin = plugin;
		this.listeners = new HashMap<CommandListener, ExecuteableMethod[]>();
		
		final PluginCommand cmd = plugin.getCommand(rootCommand);
		Validate.notNull(cmd, "Plugin " + plugin.getName() + " does not declare command " + rootCommand);
		
		cmd.setExecutor(this);
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
		
		for (Entry<CommandListener, ExecuteableMethod[]> entry : listeners.entrySet()) {
			ExecuteableMethod[] methods = entry.getValue();
			
			for (ExecuteableMethod method : methods) {
				if (!method.getName().equalsIgnoreCase(cmd.getName())) {
					continue;
				}
				
				method.execute(sender, argsCut);
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
	
	public String getRootCommand() {
		return rootCommand;
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}

}
