package de.matzefratze123.api.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Set;

import org.bukkit.plugin.Plugin;

public abstract class AbstractDatabase {
	
	protected Connection connection;
	protected DatabaseState state;
	protected Plugin plugin;
	
	public AbstractDatabase(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract void connect();
	
	public Connection getConnection() {
		return connection;
	}
	
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			connection = null;
		}
	}
	
	public Table createTable(String name, Map<String, Field> columns) {
		name = name.toLowerCase();
		
		String parts[] = new String[columns.size()];
		
		Set<String> keys = columns.keySet();
		
		int c = 0;
		for (String key : keys) {
			Field field = columns.get(key);
			
			parts[c] = key + " " + field.toString();
			c++;
		}
		
		String columnsString = SQLUtils.toFriendlyString(parts, ", ");
		
		try {
			connect();
			
			Statement statement = getConnection().createStatement();
			String update = "CREATE TABLE IF NOT EXISTS " + name + " (" + columnsString + ")";
			statement.executeUpdate(update);
			return getTable(name);
		} catch (SQLException e) {
			plugin.getLogger().severe("Cannot create table " + name + " on " + getHost() + ": " + e.getMessage());
			e.printStackTrace();
		} finally {
			close();
		}
		
		return null;
	}
	
	public abstract boolean hasTable(String name);
	
	public Table getTable(String name) {
		if (!hasTable(name)) {
			return null;
		}
		
		return new Table(plugin, this, name);
	}
	
	public void deleteTable(String name) {
		name = name.toLowerCase();
		
		try {
			connect();
			
			Statement statement = getConnection().createStatement();
			statement.executeUpdate("DROP TABLE IF EXISTS " + name);
		} catch (SQLException e) {
			plugin.getLogger().severe("Cannot delete table " + name + " on " + getHost() + ": " + e.getMessage());
			e.printStackTrace();
		} finally {
			close();
		}
	}
	
	public abstract String getHost();
	
	public enum DatabaseState {
		
		NO_DRIVERS,
		FAILED_TO_CONNECT,
		SUCCESS;
		
	}
	
}
