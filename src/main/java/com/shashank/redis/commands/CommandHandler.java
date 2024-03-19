package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

public abstract class CommandHandler implements Handler {
	
	protected ObjectFactory objectFactory;
	
	protected CommandHandler(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	
}
