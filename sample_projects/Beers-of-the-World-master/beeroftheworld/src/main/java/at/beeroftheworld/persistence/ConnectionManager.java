package at.beeroftheworld.persistence;


import java.util.HashMap;

import org.jcouchdb.db.Database;

public class ConnectionManager {
	
	private static final ConnectionManager DEFAULT_INSTANCE = new ConnectionManager();
	
	public static ConnectionManager getInstance() { 
		return DEFAULT_INSTANCE;
	}
	
	private HashMap<Long, Database> connectionTable;
	
	private ConnectionManager() {
		connectionTable = new HashMap<Long, Database>();
	}
	
	public Database getDefaultConnection() {
		long threadId = getCurrentThreadId();
		if(connectionTable.containsKey(threadId))
			return connectionTable.get(threadId);
		throw new PersistenceException("No connection available.");
	}
	
	public void buildDefaultConnection(String server, String database, int port) {
		Database connection = new Database(server, port, database);
		connectionTable.put(getCurrentThreadId(), connection);
	}
	
	private long getCurrentThreadId() {
		return Thread.currentThread().getId();
	}
}
