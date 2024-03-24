package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.KeyValueStorage;

public class Get extends CommandHandler {
	
	public Get(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String value = KeyValueStorage.get(args[1]);
		return objectFactory.getProtocolEncoder().bulkString(value);
	}
	
}
