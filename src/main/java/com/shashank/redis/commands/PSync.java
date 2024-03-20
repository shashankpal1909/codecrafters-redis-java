package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

import java.util.Base64;

public class PSync extends CommandHandler {
	
	public PSync(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String replicationId = objectFactory.getNodeConfig().getReplicationId();
		Long replicationOffSet = objectFactory.getNodeConfig().getReplicationOffSet();
		
		return objectFactory.getProtocolEncoder().simpleString(String.format("FULLRESYNC %s %s", replicationId, replicationOffSet));
	}
}
