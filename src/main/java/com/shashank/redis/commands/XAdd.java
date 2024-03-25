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
		
		String timestampStr = combinedId.split("-")[0];
		String sequenceNumberStr = combinedId.split("-")[1];
		
		long timestamp, sequenceNumber;
		
		if (combinedId.equals("*")) {
			timestamp = System.currentTimeMillis();
			
			String error = validateTimestampAndSequenceNumber(streamKey, timestamp);
			
			if (error != null) {
				return objectFactory.getProtocolEncoder().simpleError(String.format("ERR %s", error));
			}
			
			addStreamEntries(streamKey, timestamp, args);
			
			Stream stream = StreamStorage.get(streamKey);
			StreamEntry streamEntry = stream.get(timestamp);
			
			return objectFactory.getProtocolEncoder().bulkString(String.format("%s-%s", timestamp,
					streamEntry.getNextSequenceNumber() - 1));
		} else if (sequenceNumberStr.equals("*")) {
			timestamp = Long.parseLong(timestampStr);
			
			String error = validateTimestampAndSequenceNumber(streamKey, timestamp);
			
			if (error != null) {
				return objectFactory.getProtocolEncoder().simpleError(String.format("ERR %s", error));
			}
			
			addStreamEntries(streamKey, timestamp, args);
			
			Stream stream = StreamStorage.get(streamKey);
			StreamEntry streamEntry = stream.get(timestamp);
			
			return objectFactory.getProtocolEncoder().bulkString(String.format("%s-%s", timestamp,
					streamEntry.getNextSequenceNumber() - 1));
		} else {
			timestamp = Long.parseLong(timestampStr);
			sequenceNumber = Long.parseLong(sequenceNumberStr);
			
			String error = validateTimestampAndSequenceNumber(streamKey, timestamp, sequenceNumber);
			
			if (error != null) {
				return objectFactory.getProtocolEncoder().simpleError(String.format("ERR %s", error));
			}
			
			addStreamEntries(streamKey, timestamp, sequenceNumber, args);
			return objectFactory.getProtocolEncoder().bulkString(String.format("%s-%s", timestamp, sequenceNumber));
		}
	}
	
	private String validateTimestampAndSequenceNumber(String streamKey, long timestamp) {
		Stream stream = StreamStorage.get(streamKey);
		
		if (timestamp <= 0) {
			return "The ID specified in XADD must be greater than 0-0";
		}
		
		if (stream == null) {
			return null;
		}
		
		StreamEntry streamEntry = stream.getLastEntry();
		if (streamEntry == null) {
			return null;
		}
		
		if (timestamp < streamEntry.getTimestamp()) {
			return "The ID specified in XADD is equal or smaller than the target stream top item";
		}
		
		return null;
	}
	
	private void addStreamEntries(String streamKey, long timestamp, String[] args) {
		for (int i = 3; i < args.length; i += 2) {
			String key = args[i];
			String val = args[i + 1];
			
			StreamStorage.addEntry(streamKey, timestamp, key, val);
		}
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
		
		if (timestamp < streamEntry.getTimestamp() || (timestamp == streamEntry.getTimestamp() && sequenceNumber < streamEntry.getNextSequenceNumber())) {
			return "The ID specified in XADD is equal or smaller than the target stream top item";
		}
		
		return null;
	}
	
	private void addStreamEntries(String streamKey, long timestamp, long sequenceNumber, String[] args) {
		for (int i = 3; i < args.length; i += 2) {
			String key = args[i];
			String val = args[i + 1];
			
			StreamStorage.addEntry(streamKey, timestamp, sequenceNumber, key, val);
		}
	}
	
	private void addStreamEntries(String streamKey, String[] args) {
		for (int i = 3; i < args.length; i += 2) {
			String key = args[i];
			String val = args[i + 1];
			
			StreamStorage.addEntry(streamKey, key, val);
		}
	}
}
