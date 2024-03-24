package com.shashank.redis.storage;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataTypeStorage {
	
	private static final Map<String, Data<DataType>> cache = new ConcurrentHashMap<>();
	
	public DataTypeStorage() {}
	
	public static void set(String key, DataType type) {
		cache.put(key, new Data<>(type, Instant.MAX));
	}
	
	public static void set(String key, DataType type, Long expiresIn) {
		cache.put(key, new Data<>(type, Instant.now().plusMillis(expiresIn)));
	}
	
	public static DataType get(String key) {
		Data<DataType> data = cache.get(key);
		
		if (data == null) return null;
		
		if (Instant.now().isAfter(data.expiry())) {
			remove(key);
			return null;
		}
		
		return data.value();
	}
	
	private static void remove(String key) {
		cache.remove(key);
	}
	
	
}
