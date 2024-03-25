package com.shashank.redis.storage;

public class Node<T> {
	
	private final long id;
	private final T val;
	
	Node<T> next;
	
	public Node(long id, T val) {
		this.id = id;
		this.val = val;
		this.next = null;
	}
	
	public Node(long id, T val, Node<T> next) {
		this.id = id;
		this.val = val;
		this.next = next;
	}
	
	public T getVal() {
		return val;
	}
	
	public long getId() {
		return id;
	}
}
