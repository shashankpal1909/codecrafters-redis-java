package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.Stream;
import com.shashank.redis.storage.StreamEntry;
import com.shashank.redis.storage.StreamStorage;

public class XAdd extends CommandHandler {
	
	public XAdd(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String streamKey = args[1];
		String combinedId = args[2];
		
		long timestamp = Long.parseLong(combinedId.split("-")[0]);
		long sequenceNumber = Long.parseLong(combinedId.split("-")[1]);
		
		String error = validateTimestampAndSequenceNumber(streamKey, timestamp, sequenceNumber);
		
		if (error != null) {
			return objectFactory.getProtocolEncoder().simpleError(String.format("ERR %s", error));
		}
		
		addStreamEntries(streamKey, args);
		
		return objectFactory.getProtocolEncoder().bulkString(combinedId);
	}
	
	private String validateTimestampAndSequenceNumber(String streamKey, long timestamp, long sequenceNumber) {
		Stream stream = StreamStorage.get(streamKey);
		
		if (timestamp <= 0 && sequenceNumber <= 0) {
			return "The ID specified in XADD must be greater than 0-0";
		}
		
		if (stream == null) {
			return null;
		}
		
		StreamEntry streamEntry = stream.getLastEntry();
		if (streamEntry == null) {
			return null;
		}
		
		if (timestamp < streamEntry.getTimestamp() ||
				(timestamp == streamEntry.getTimestamp() && sequenceNumber < streamEntry.getNextSequenceNumber())) {
			return "The ID specified in XADD is equal or smaller than the target stream top item";
		}
		
		return null;
	}
	
	private void addStreamEntries(String streamKey, String[] args) {
		for (int i = 3; i < args.length; i += 2) {
			String key = args[i];
			String val = args[i + 1];
			
			StreamStorage.addEntry(streamKey, key, val);
		}
	}
}
