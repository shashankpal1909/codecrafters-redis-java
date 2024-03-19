package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.Storage;

public class Set extends CommandHandler {
	
	public Set(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String key = args[1], value = args[2];
		
		if (args.length > 3) {
			String param = args[3];
			switch (param) {
				case "px" -> {
					Long expiresIn = Long.parseLong(args[4]);
					Storage.set(key, value, expiresIn);
				}
				default -> throw new RuntimeException(String.format("Invalid parameter: %s", param));
			}
		} else {
			Storage.set(key, value);
		}
		
		return objectFactory.getProtocolEncoder().simpleString("OK");
	}
	
}