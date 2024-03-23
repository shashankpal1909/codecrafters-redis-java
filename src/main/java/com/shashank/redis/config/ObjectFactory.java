package com.shashank.redis.config;

import com.shashank.redis.protocol.RDBFileReader;
import com.shashank.redis.commands.CommandFactory;
import com.shashank.redis.protocol.ProtocolDecoder;
import com.shashank.redis.protocol.ProtocolEncoder;
import com.shashank.redis.replica.CommandReplicator;

import java.lang.reflect.InvocationTargetException;

public class ObjectFactory {
	
	private final NodeConfig nodeConfig;
	private ProtocolDecoder protocolDecoder;
	private ProtocolEncoder protocolEncoder;
	private CommandFactory commandFactory;
	private CommandReplicator commandReplicator;
	private RDBFileReader rdbFileReader;
	
	public ObjectFactory(NodeConfig nodeConfig) throws InvocationTargetException, NoSuchMethodException,
			InstantiationException, IllegalAccessException {
		this.nodeConfig = nodeConfig;
		init();
	}
	
	private void init() throws NoSuchMethodException, InvocationTargetException, InstantiationException,
			IllegalAccessException {
		protocolDecoder = new ProtocolDecoder();
		protocolEncoder = new ProtocolEncoder();
		commandFactory = new CommandFactory(this);
		commandReplicator = new CommandReplicator(this);
		this.rdbFileReader =
				new RDBFileReader(this.nodeConfig.getRDBFileDir() + "/" + this.nodeConfig.getRDBFileName());
	}
	
	public ProtocolDecoder getProtocolDecoder() {
		return protocolDecoder;
	}
	
	public ProtocolEncoder getProtocolEncoder() {
		return protocolEncoder;
	}
	
	public CommandFactory getCommandFactory() {
		return commandFactory;
	}
	
	public NodeConfig getNodeConfig() {
		return nodeConfig;
	}
	
	public CommandReplicator getCommandReplicator() {
		return commandReplicator;
	}
	
	public RDBFileReader getRdbFileReader() {
		return rdbFileReader;
	}
}
