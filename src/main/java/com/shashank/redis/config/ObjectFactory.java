package com.shashank.redis.config;

import com.shashank.redis.commands.CommandFactory;
import com.shashank.redis.protocol.ProtocolDecoder;
import com.shashank.redis.protocol.ProtocolEncoder;

import java.lang.reflect.InvocationTargetException;

public class ObjectFactory {
	private ProtocolDecoder protocolDecoder;
	private ProtocolEncoder protocolEncoder;
	private CommandFactory commandFactory;
	
	public ObjectFactory() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		protocolDecoder = new ProtocolDecoder();
		protocolEncoder = new ProtocolEncoder();
		commandFactory = new CommandFactory(this);
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
	
}
