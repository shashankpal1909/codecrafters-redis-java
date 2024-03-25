package com.shashank.redis.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Stream {
	
	private final Map<Long, Node<StreamEntry>> entries;
	private Node<StreamEntry> head;
	private Node<StreamEntry> tail;
	
	public Stream() {
		this.entries = new ConcurrentHashMap<>();
	}
	
	public StreamEntry get(Long timestamp) {
		return entries.get(timestamp).getVal();
	}
	
	public Map<Long, Node<StreamEntry>> getEntries() {
		return entries;
	}
	
	public void addEntry(String key, String val) {
		addEntry(System.currentTimeMillis(), key, val);
	}
	
	public void addEntry(long timestamp, String key, String val) {
		StreamEntry streamEntry = entries.computeIfAbsent(timestamp, k -> {
			Node<StreamEntry> node = new Node<>(k, new StreamEntry(k));
			updateTail(node);
			return node;
		}).getVal();
		
		streamEntry.addStreamEntry(key, val);
	}
	
	private void updateTail(Node<StreamEntry> node) {
		if (head == null) {
			head = node;
		} else {
			tail.next = node;
		}
		tail = node;
	}
	
	public void addEntry(long timestamp, long id, String key, String val) {
		StreamEntry streamEntry = entries.computeIfAbsent(timestamp, k -> {
			Node<StreamEntry> node = new Node<>(k, new StreamEntry(k));
			updateTail(node);
			return node;
		}).getVal();
		
		streamEntry.addStreamEntry(id, key, val);
	}
	
	public StreamEntry getLastEntry() {
		return tail != null ? tail.getVal() : null;
	}
}
