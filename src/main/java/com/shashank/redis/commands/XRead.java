package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.StreamStorage;

public class XRead extends CommandHandler {
	public XRead(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		if (args[1].equalsIgnoreCase("streams")) {
			String streamKey = args[2];
			String[] id = args[3].split("-");
			
			long timestamp = Long.parseLong(id[0]);
			long sequenceNumber = Long.parseLong(id[1]);
			
			var stream = StreamStorage.get(streamKey);
			var result = stream.xRead(timestamp, sequenceNumber);
			
			return objectFactory.getProtocolEncoder().nestedArray(result);
		} else {
			throw new RuntimeException(String.format("Unexpected argument: %s", args[1]));
		}
	}
}
