package com.shashank.redis.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamStorage {
	
	private final static Map<String, Stream> streams = new ConcurrentHashMap<>();
	
	public StreamStorage() {
	}
	
	public static void addEntry(String streamName, String id, String key, String val) {
		if (!streams.containsKey(streamName)) {
			createStream(streamName);
		}
		
		Stream stream = streams.get(streamName);
		stream.addEntry(key, val);
	}
	
	public static void createStream(String name) {
		Stream stream = new Stream(name);
		streams.put(name, stream);
		DataTypeStorage.set(name, DataType.STREAM);
	}
	
}
