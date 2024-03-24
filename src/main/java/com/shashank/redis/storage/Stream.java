package com.shashank.redis.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stream {
	
	private final Map<String, String> entries;
	private final String name;
	private final String id;
	
	public Stream(String name) {
		this.id = String.valueOf(generateUniqueID());
		this.name = name;
		this.entries = new ConcurrentHashMap<>();
	}
	
	private long generateUniqueID() {
		return System.currentTimeMillis();
	}
	
	public Stream(String id, String name) {
		this.id = id;
		this.name = name;
		this.entries = new ConcurrentHashMap<>();
	}
	
	public Map<String, String> getEntries() {
		return entries;
	}
	
	public String getId() {
		return id;
	}
	
	public void addEntry(String key, String val) {
		entries.put(key, val);
	}
	
	public String getName() {
		return name;
	}
	
}
