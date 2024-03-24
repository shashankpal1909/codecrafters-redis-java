package com.shashank.redis.storage;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueStorage {
	
	private static final Map<String, Data<String>> cache = new ConcurrentHashMap<>();
	
	public KeyValueStorage() {}
	
	public static void set(String key, String value) {
		cache.put(key, new Data<>(value, Instant.MAX));
		DataTypeStorage.set(key, DataType.STRING);
	}
	
	public static void set(String key, String value, Long expiresIn) {
		cache.put(key, new Data<>(value, Instant.now().plusMillis(expiresIn)));
		DataTypeStorage.set(key, DataType.STRING, expiresIn);
	}
	
	public static String get(String key) {
		Data<String> data = cache.get(key);
		
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
