package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

public class Echo extends CommandHandler {
	
	public Echo(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		return objectFactory.getProtocolEncoder().bulkString(args[1]);
	}
	
}
