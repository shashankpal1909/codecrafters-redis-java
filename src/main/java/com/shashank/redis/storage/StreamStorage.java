package com.shashank.redis.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamStorage {
	
	private static final Map<String, Stream> streams = new ConcurrentHashMap<>();
	
	private StreamStorage() {
	}
	
	public static void addEntry(String streamName, long timestamp, String key, String val) {
		Stream stream = streams.computeIfAbsent(streamName, k -> {
			createStream(k);
			return streams.get(k);
		});
		stream.addEntry(timestamp, key, val);
	}
	
	public static void createStream(String name) {
		streams.putIfAbsent(name, new Stream());
		DataTypeStorage.set(name, DataType.STREAM);
	}
	
	public static void addEntry(String streamName, long timestamp, long id, String key, String val) {
		if (!streams.containsKey(streamName)) {
			createStream(streamName);
		}
		
		Stream stream = streams.get(streamName);
		stream.addEntry(timestamp, id, key, val);
	}
	
	public static void addEntry(String streamName, String key, String val) {
		if (!streams.containsKey(streamName)) {
			createStream(streamName);
		}
		
		Stream stream = streams.get(streamName);
		stream.addEntry(key, val);
	}
	
	public static Stream get(String streamKey) {
		return streams.get(streamKey);
	}
}
