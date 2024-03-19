package com.shashank.redis.commands;

public interface Handler {
	
	byte[] execute(String[] args);
	
}
