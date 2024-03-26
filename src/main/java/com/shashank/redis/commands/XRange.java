package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.StreamEntry;
import com.shashank.redis.storage.StreamStorage;

import java.util.ArrayList;
import java.util.List;

public class XRange extends CommandHandler {
	
	public XRange(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String streamKey = args[1];
		
		String[] start = args[2].split("-");
		String[] end = args[3].split("-");
		
		long startTimestamp = Long.parseLong(start[0]);
		long endTimestamp = Long.parseLong(end[0]);
		
		long startSequenceNumber = -1;
		long endSequenceNumber = -1;
		
		if (start.length > 1) startSequenceNumber = Long.parseLong(start[1]);
		if (end.length > 1) endSequenceNumber = Long.parseLong(end[1]);
		
		var stream = StreamStorage.get(streamKey);
		
		var streamEntries = stream.getEntries(startTimestamp, endTimestamp);
		
		List<Object> result = new ArrayList<>();
		
		List<Object> streamEntryList = new ArrayList<>();
		result.add(streamEntryList);
		
		for (StreamEntry streamEntry : streamEntries) {
			List<Object> temp = new ArrayList<>();
			streamEntryList.add(temp);
			
			for (var sequenceNumber : streamEntry.getStreamEntries().keySet()) {
				
				if (startTimestamp == streamEntry.getTimestamp() && sequenceNumber < startSequenceNumber) {
					continue;
				}
				
				if (endTimestamp == streamEntry.getTimestamp() && sequenceNumber > endSequenceNumber) {
					break;
				}
				
				temp.add(String.format("%s-%s", streamEntry.getTimestamp(), sequenceNumber));
				
				for (String key : streamEntry.get(sequenceNumber).keySet()) {
					temp.add(key);
					temp.add(streamEntry.get(sequenceNumber).get(key));
				}
			}
		}
		
		return objectFactory.getProtocolEncoder().nestedArray(result);
	}
	
}