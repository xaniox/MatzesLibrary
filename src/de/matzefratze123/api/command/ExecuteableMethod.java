package de.matzefratze123.api.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.matzefratze123.api.command.CommandExecutorService.TransformerMap;
import de.matzefratze123.api.command.transform.TransformException;
import de.matzefratze123.api.command.transform.Transformer;

public class ExecuteableMethod {
	
	private static final String USAGE_VARIABLE = "%usage%";
	private static final String DESCRIPTION_VARIABLE = "%description%";
	
	private final Class<?>[] parameterArgTypes;
	
	private Method method;
	private CommandListener instance;
	
	private String name;
	private int minArgs;
	private boolean onlyIngame;
	
	private String[] permissions;
	
	private String usage;
	private String description;
	private String usageStyle;
	
	public ExecuteableMethod(CommandListener listener, Method method) {
		if (!method.isAnnotationPresent(Command.class))
			throw new IllegalArgumentException("No Command annotation at " + method.getName() + " in type " + method.getClass().getSimpleName() + " present!");
		
		Command cmdAnnotation = method.getAnnotation(Command.class);
		Class<?>[] parameterTypes = method.getParameterTypes();
		parameterArgTypes = new Class<?>[parameterTypes.length - 1];
		
		for (int i = 1; i < parameterTypes.length; i++) {
			parameterArgTypes[i - 1] = parameterTypes[i];
		}
		
		this.instance = listener;
		this.method = method;
		this.name = cmdAnnotation.value();
		this.minArgs = cmdAnnotation.minArgs();
		this.onlyIngame = cmdAnnotation.onlyIngame();
		
		if (method.isAnnotationPresent(CommandPermissions.class)) {
			CommandPermissions permAnnotation = method.getAnnotation(CommandPermissions.class);
			
			this.permissions = permAnnotation.value();
		}
		
		if (method.isAnnotationPresent(CommandHelp.class)) {
			CommandHelp helpAnnotation = method.getAnnotation(CommandHelp.class);
			
			this.usage = helpAnnotation.usage();
			this.description = helpAnnotation.description();
			this.usageStyle = helpAnnotation.usageStyle();
		}
	}
	
	public static ExecuteableMethod[] findListenerMethods(CommandListener listener) {
		List<ExecuteableMethod> executeableMethods = new ArrayList<ExecuteableMethod>();
		
		for (Method method : listener.getClass().getMethods()) {
			if (!method.isAnnotationPresent(Command.class)) {
				continue;
			}
			
			ExecuteableMethod em = new ExecuteableMethod(listener, method);
			executeableMethods.add(em);
		}
		
		return executeableMethods.toArray(new ExecuteableMethod[executeableMethods.size()]);
	}
	
	public void execute(CommandSender sender, String[] args, TransformerMap transformers) {
		if (minArgs > 0 && args.length < minArgs) {
			sender.sendMessage(usage == null ? ChatColor.RED + "Too few arguments!" : usage);
			return;
		}
		
		if (onlyIngame && !(sender instanceof Player)) {
			sender.sendMessage("This command can only be used by a player");
			return;
		}
		
		if (permissions != null) {
			for (String permission : permissions) {
				if (!sender.hasPermission(permission)) {
					sender.sendMessage(ChatColor.RED + "You don't have permission.");
					return;
				}
			}
		}
		
		if (args.length > 0 && (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help") && usage != null && description != null)) {
			String helpLine = usageStyle.replace(USAGE_VARIABLE, usage).replace(DESCRIPTION_VARIABLE, description);
			
			sender.sendMessage(helpLine);
			return;
		}
		
		Argument<?>[] arguments = parseArguments(transformers, args);
		Object[] values = new Object[arguments.length + 1];
		
		values[0] = sender;
		for (int i = 1; i < values.length; i++) {
			values[i] = arguments[i - 1] == null ? null : arguments[i - 1].getValue();
		}
		
		try {
			method.invoke(instance, values);
		} catch (Exception e) {
			String detailMessage = e.getMessage();
			
			if (e instanceof IllegalAccessException) {
				detailMessage = "Unable to access method";
			} else if (e instanceof IllegalArgumentException) {
				detailMessage = "Illegal parameter types";
			} else if (e instanceof InvocationTargetException) {
				detailMessage = e.getMessage();
			}
			
			Bukkit.getLogger().severe("Cannot execute command " + name + " in method " + method.getName() + " of type " + method.getDeclaringClass().getName() + ": " + e + ": " + detailMessage);
			e.printStackTrace();
		}
	}
	
	private Argument<?>[] parseArguments(TransformerMap transformers, String[] args) {
		Argument<?>[] argsArray = new Argument<?>[parameterArgTypes.length]; 
		
		for (int i = 0; i < args.length && i < parameterArgTypes.length; i++) {
			String arg = args[i];
			Transformer<?> transformer = null;
			
			if (i < parameterArgTypes.length) {
				transformer = transformers.get(parameterArgTypes[i]);
				
				if (transformer == null) {
					//There is no transformer for this parameter typ
					argsArray[i] = null;
					continue;
				}
				
				try {
					Argument<?> argument = createArgument(transformer, arg);
					
					argsArray[i] = argument;
				} catch (TransformException e) {
					//Transform failed, argument null
					argsArray[i] = null;
					continue;
				}
			}
		}
		
		return argsArray;
	}
	
	private <V> Argument<V> createArgument(Transformer<V> t, String arg) throws TransformException {
		V transformed = t.transform(arg);
		Argument<V> argument = new Argument<V>(transformed);
		
		return argument;
	}

	public String getName() {
		return name;
	}

	public int getMinArgs() {
		return minArgs;
	}

	public boolean getOnlyIngame() {
		return onlyIngame;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public String getUsage() {
		return usage;
	}

	public String getDescription() {
		return description;
	}
	
	Method getMethod() {
		return method;
	}

}
