package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

public class Ping extends CommandHandler {
	
	public Ping(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		return objectFactory.getProtocolEncoder().simpleString("PONG");
	}
	
}
