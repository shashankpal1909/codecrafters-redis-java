package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

import java.util.List;

public class ReplConf extends CommandHandler {
	
	public ReplConf(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String param = args[1].toLowerCase();
		
		switch (param) {
			case "listening-port" -> {
				try {
					int _port = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					throw new RuntimeException(String.format("Invalid port number: %s", args[2]));
				}
			}
			case "capa" -> {
				if (!java.util.Set.of("psync2", "eof").contains(args[2])) {
					throw new RuntimeException(String.format("Invalid parameter: %s", args[2]));
				}
			}
			case "getack" -> {
				return objectFactory.getProtocolEncoder().array(List.of("REPLCONF", "ACK", "0"));
			}
			default -> throw new IllegalStateException("Unexpected value: " + param);
		}
		
		return objectFactory.getProtocolEncoder().simpleString("OK");
	}
}
