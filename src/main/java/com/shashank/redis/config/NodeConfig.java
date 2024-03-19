package com.shashank.redis.config;

public class NodeConfig {
	
	private int port = 6379;
	
	public NodeConfig(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String param = args[i].toLowerCase().substring(2);
			switch (param) {
				case "port" -> port = Integer.parseInt(args[++i]);
				default -> throw new RuntimeException("Invalid parameter: " + param);
			}
		}
	}
	
	public int getPort() {
		return port;
	}
}
