package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.StreamStorage;

public class XAdd extends CommandHandler {
	public XAdd(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String streamKey = args[1];
		String id = args[2];
		
		for (int i = 3; i < args.length; i += 2) {
			String key = args[i];
			String val = args[i + 1];
			
			StreamStorage.addEntry(streamKey, id, key, val);
		}
		
		return objectFactory.getProtocolEncoder().bulkString(id);
	}
}
