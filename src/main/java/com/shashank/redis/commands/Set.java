package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.Storage;

public class Set extends CommandHandler {
	
	public Set(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String key = args[1];
		String value = args[2];
		
		Storage.set(key, value);
		
		return objectFactory.getProtocolEncoder().simpleString("OK");
	}
	
}
