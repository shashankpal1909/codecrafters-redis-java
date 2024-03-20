package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

public class Wait extends CommandHandler {
	
	public Wait(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		return objectFactory.getProtocolEncoder().integer(0);
	}
	
}
