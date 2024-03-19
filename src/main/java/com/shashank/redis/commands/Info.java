package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

public class Info extends CommandHandler {
	
	public Info(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String param = args[1].toLowerCase();
		return switch (param) {
			case "replication" ->
					objectFactory.getProtocolEncoder().bulkString(String.format("role:%s", objectFactory.getNodeConfig().getRole()));
			default -> throw new RuntimeException(String.format("Unknown parameter: %s", param));
		};
	}
	
}
