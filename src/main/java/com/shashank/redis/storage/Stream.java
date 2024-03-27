package com.shashank.redis.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stream {
	
	private final Map<Long, StreamEntry> entries;
	private final List<Long> timestamps;
	
	public Stream() {
		this.entries = new ConcurrentHashMap<>();
		this.timestamps = new ArrayList<>();
	}
	
	public StreamEntry get(Long timestamp) {
		return entries.get(timestamp);
	}
	
	public Map<Long, StreamEntry> getEntries() {
		return entries;
	}
	
	public synchronized void addEntry(String key, String val) {
		addEntry(System.currentTimeMillis(), key, val);
	}
	
	public synchronized void addEntry(long timestamp, String key, String val) {
		StreamEntry streamEntry = entries.computeIfAbsent(timestamp, k -> {
			timestamps.add(k);
			return new StreamEntry(k);
		});
		
		if (timestamp == 0 && streamEntry.get(1) == null) streamEntry.addStreamEntry(1L, key, val);
		else streamEntry.addStreamEntry(key, val);
	}
	
	public synchronized void addEntry(long timestamp, long id, String key, String val) {
		StreamEntry streamEntry = entries.computeIfAbsent(timestamp, k -> {
			timestamps.add(k);
			return new StreamEntry(k);
		});
		
		streamEntry.addStreamEntry(id, key, val);
	}
	
	public StreamEntry getLastEntry() {
		return timestamps.isEmpty() ? null : entries.get(timestamps.getLast());
	}
	
	public List<StreamEntry> getEntries(long startTimestamp, long endTimestamp) {
		int startIndex = lowerBound(timestamps, startTimestamp);
		int endIndex = upperBound(timestamps, endTimestamp);
		
		return timestamps.subList(startIndex, endIndex + 1).stream().map(entries::get).toList();
	}
	
	public static int lowerBound(List<Long> list, long target) {
		int s = 0;
		int e = list.size();
		while (s != e) {
			int mid = s + e >> 1;
			if (list.get(mid) < target) {
				s = mid + 1;
			} else {
				e = mid;
			}
		}
		if (s == list.size()) {
			return 0;
		}
		return s;
	}
	
	public static int upperBound(List<Long> list, long target) {
		int s = 0;
		int e = list.size();
		while (s != e) {
			int mid = s + e >> 1;
			if (list.get(mid) <= target) {
				s = mid + 1;
			} else {
				e = mid;
			}
		}
		if (s == list.size()) {
			return list.size() - 1;
		}
		return s;
	}
	
	public int upperBoundIndex(long target) {
		int low = 0;
		int high = timestamps.size() - 1;
		
		while (low < high) {
			int mid = (low + high + 1) / 2;
			if (timestamps.get(mid) <= target) {
				low = mid;
			} else {
				high = mid - 1;
			}
		}
		return high;
	}
	
	public int lowerBoundIndex(long target) {
		int low = 0;
		int high = timestamps.size() - 1;
		
		while (low < high) {
			int mid = (low + high) / 2;
			if (timestamps.get(mid) >= target) {
				high = mid;
			} else {
				low = mid + 1;
			}
		}
		return low;
	}
	
}
