package com.shashank.redis.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class StreamEntry {
	
	private final long timestamp;
	
	private final Map<Long, Map<String, String>> entries;
	private final AtomicLong nextSequenceNumber;
	
	public StreamEntry(long timestamp) {
		this.timestamp = timestamp;
		this.nextSequenceNumber = new AtomicLong(0);
		this.entries = new ConcurrentHashMap<>();
	}
	
	public long getNextSequenceNumber() {
		return nextSequenceNumber.get();
	}
	
	public void addStreamEntry(String key, String val) {
		long id = nextSequenceNumber.getAndIncrement();
		entries.computeIfAbsent(id, k -> new ConcurrentHashMap<>()).put(key, val);
	}
	
	public void addStreamEntry(long sequenceNumber, String key, String val) {
		nextSequenceNumber.set(sequenceNumber + 1);
		entries.computeIfAbsent(sequenceNumber, k -> new ConcurrentHashMap<>()).put(key, val);
	}
	
	public Map<Long, Map<String, String>> getStreamEntries() {
		return entries;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
}
