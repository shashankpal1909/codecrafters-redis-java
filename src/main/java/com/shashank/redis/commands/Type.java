package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.Storage;

public class Type extends CommandHandler {
	
	public Type(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String key = args[1];
		String value = Storage.get(key);
		
		if (value == null) return objectFactory.getProtocolEncoder().simpleString("none");
		else return objectFactory.getProtocolEncoder().simpleString("string");
	}
	
}
