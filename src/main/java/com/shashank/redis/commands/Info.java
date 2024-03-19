package com.shashank.redis.commands;

import com.shashank.redis.config.NodeConfig;
import com.shashank.redis.config.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

public class Info extends CommandHandler {
	
	public Info(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String param = args[1].toLowerCase();
		return switch (param) {
			case "replication" -> objectFactory.getProtocolEncoder().bulkString(getReplicationInfo());
			default -> throw new RuntimeException(String.format("Unknown parameter: %s", param));
		};
	}
	
	private String getReplicationInfo() {
		List<String> info = new ArrayList<>();
		NodeConfig nodeConfig = objectFactory.getNodeConfig();
		String role = nodeConfig.getRole();
		info.add(String.format("role:%s", role));
		if (role.equals("master")) {
			info.add(String.format("master_replid:%s", nodeConfig.getReplicationId()));
			info.add(String.format("master_repl_offset:%s", nodeConfig.getReplicationOffSet()));
		}
		return String.join("\n", info);
	}
	
}
