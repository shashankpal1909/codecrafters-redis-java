package com.shashank.redis.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stream {
	
	private final String streamKey;
	private final Map<Long, StreamEntry> entries;
	private final List<Long> timestamps;
	
	public Stream(String streamKey) {
		this.streamKey = streamKey;
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
	
	public List<Object> xRead(long targetTimestamp, long targetSequenceNumber) {
		int index = upperBound(timestamps, targetTimestamp);
		
		List<Object> result = new ArrayList<>();
		
		// [
		//   [
		// 		"stream_key",
		//     [
		//       [
		// 		"0-1",
		//         [
		// 		"temperature",
		// 				"96"
		//         ]
		//       ]
		//     ]
		//   ]
		// ]
		
		List<Object> temp_1 = new ArrayList<>();
		temp_1.add(streamKey);
		
		for (int i = index; i < timestamps.size(); i++) {
			List<Object> temp_2 = new ArrayList<>();
			Long timestamp = timestamps.get(i);
			StreamEntry streamEntry = entries.get(timestamp);
			for (var sequenceNumber : streamEntry.getStreamEntries().keySet()) {
				if (timestamp == targetTimestamp && sequenceNumber <= targetSequenceNumber) {
					continue;
				}
				
				List<Object> temp_3 = new ArrayList<>();
				temp_3.add(String.format("%s-%s", timestamp, sequenceNumber));
				
				List<String> temp_4 = new ArrayList<>();
				for (String key : streamEntry.get(sequenceNumber).keySet()) {
					temp_4.add(key);
					temp_4.add(streamEntry.get(sequenceNumber).get(key));
				}
				
				temp_3.add(temp_4);
				temp_2.add(temp_3);
			}
			
			temp_1.add(temp_2);
		}
		
		ArrayList<Object> objects = new ArrayList<>();
		objects.add(temp_1);
		return objects;
	}
}
