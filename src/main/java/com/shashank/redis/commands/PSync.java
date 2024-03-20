package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.utils.ArrayUtils;

import java.util.Base64;

public class PSync extends CommandHandler {
	
	private static final String EMPTY_RDB_FILE = "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog==";
	
	public PSync(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		String replicationId = objectFactory.getNodeConfig().getReplicationId();
		Long replicationOffSet = objectFactory.getNodeConfig().getReplicationOffSet();
		
		byte[] fullReSyncResponse = objectFactory.getProtocolEncoder().simpleString(String.format("FULLRESYNC %s %s", replicationId, replicationOffSet));
		byte[] emptyRDbFile = Base64.getDecoder().decode(EMPTY_RDB_FILE);
		byte[] prefix = String.format("$%s\r\n", emptyRDbFile.length).getBytes();
		byte[] response = ArrayUtils.addAll(fullReSyncResponse, prefix);
		
		return ArrayUtils.addAll(response, emptyRDbFile);
	}
}
