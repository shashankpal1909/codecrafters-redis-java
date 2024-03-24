package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.storage.DataType;
import com.shashank.redis.storage.DataTypeStorage;

public class Type extends CommandHandler {
	
	public Type(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String key = args[1];
		DataType dataType = DataTypeStorage.get(key);
		
		if (dataType == null) return objectFactory.getProtocolEncoder().simpleString("none");
		else return objectFactory.getProtocolEncoder().simpleString(dataType.toString().toLowerCase());
	}
	
}
