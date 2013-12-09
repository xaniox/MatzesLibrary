package de.matzefratze123.api.sql;

import java.util.Iterator;
import java.util.Map;

class SQLUtils {

	static final char TICK = '\'';
	
	static String parseWhereClause(Map<?, ?> where) {
		if (where == null)
			return null;
		
		StringBuilder builder = new StringBuilder();
		Iterator<?> iter = where.keySet().iterator();
		builder.append(" WHERE ");
		
		while(iter.hasNext()) {
			Object next = iter.next();
			Object value = where.get(next);
			
			builder.append(next).append("=").append(TICK).append(value).append(TICK);
			if (iter.hasNext())
				builder.append(" AND ");
		}
		

		return builder.toString();
	}
	
	public static String toFriendlyString(Iterable<?> iterable, String seperator) {
		Iterator<?> iter = iterable.iterator();
		StringBuilder builder = new StringBuilder();
		
		while(iter.hasNext()) {
			Object next = iter.next();
			
			builder.append(next);
			if (iter.hasNext())
				builder.append(seperator);
		}
		
		return builder.toString();
	}
	
	public static String toFriendlyString(Object[] o, String seperator) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < o.length; i++) {
			Object next = o[i];
			
			builder.append(next);
			if (o.length >= i + 2)
				builder.append(seperator);
		}
		
		return builder.toString();
	}

}
