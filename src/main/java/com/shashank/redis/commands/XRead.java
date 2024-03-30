package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.StreamStorage;

import java.util.ArrayList;
import java.util.List;

public class XRead extends CommandHandler {
	public XRead(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		if (args[1].equalsIgnoreCase("streams")) {
			List<Object> objects = new ArrayList<>();
			
			for (int i = 2, j = i + (args.length - 2) / 2; j < args.length; i++, j++) {
				String streamKey = args[i];
				String[] id = args[j].split("-");
				
				long timestamp = Long.parseLong(id[0]);
				long sequenceNumber = Long.parseLong(id[1]);
				
				var stream = StreamStorage.get(streamKey);
				var result = stream.xRead(timestamp, sequenceNumber);
				objects.add(result);
			}
			
			return objectFactory.getProtocolEncoder().nestedArray(objects);
		} else {
			throw new RuntimeException(String.format("Unexpected argument: %s", args[1]));
		}
	}
}
