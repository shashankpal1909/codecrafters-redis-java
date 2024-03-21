package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

import java.util.List;

public class Config extends CommandHandler {
	
	public Config(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String param = args[1].toLowerCase();
		
		if (param.equals("get")) {
			switch (args[2]) {
				case "dir" -> {
					return objectFactory.getProtocolEncoder().array(List.of("dir",
							objectFactory.getNodeConfig().getRDBFileDir()));
				}
				case "dbfilename" -> {
					return objectFactory.getProtocolEncoder().array(List.of("dbfilename",
							objectFactory.getNodeConfig().getRDBFileName()));
				}
				default -> throw new RuntimeException(String.format("Invalid param: %s", args[2]));
			}
		} else {
			throw new RuntimeException(String.format("Invalid param: %s", param));
		}
		
	}
	
}
