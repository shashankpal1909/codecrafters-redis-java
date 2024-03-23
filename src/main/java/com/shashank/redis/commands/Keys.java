package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

public class Keys extends CommandHandler {
	public Keys(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		return objectFactory.getProtocolEncoder().array(objectFactory.getRdbFileReader().getDataMap().keySet().stream().toList());
	}
}
