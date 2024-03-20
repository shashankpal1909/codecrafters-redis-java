package com.shashank.redis.replica;

import com.shashank.redis.commands.CommandFactory;
import com.shashank.redis.config.NodeConfig;
import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.config.ReplicaConfig;
import com.shashank.redis.protocol.ProtocolDecoder;
import com.shashank.redis.protocol.ProtocolEncoder;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class ReplicaInitializer extends Thread {
	
	private final NodeConfig nodeConfig;
	private final ProtocolDecoder protocolDecoder;
	private final ProtocolEncoder protocolEncoder;
	private final CommandFactory commandFactory;
	
	public ReplicaInitializer(ObjectFactory objectFactory) {
		this.nodeConfig = objectFactory.getNodeConfig();
		this.protocolDecoder = objectFactory.getProtocolDecoder();
		this.protocolEncoder = objectFactory.getProtocolEncoder();
		this.commandFactory = objectFactory.getCommandFactory();
	}
	
	@Override
	public void run() {
		ReplicaConfig replicaConfig = nodeConfig.getReplicaConfig();
		
		try (Socket masterSocket = new Socket(replicaConfig.host(), replicaConfig.port()); DataInputStream inputStream = new DataInputStream(masterSocket.getInputStream()); OutputStream outputStream = masterSocket.getOutputStream()) {
			initializeReplica(inputStream, outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void initializeReplica(DataInputStream inputStream, OutputStream outputStream) throws IOException {
		// Send a PING to master node
		byte[] command = protocolEncoder.array(List.of("ping"));
		outputStream.write(command);
		String response = protocolDecoder.decode(inputStream);
		if (!response.equalsIgnoreCase("pong")) {
			System.out.printf("Unexpected response for PING: %s\n", response);
		} else {
			System.out.printf("Received response for PING: %s\n", response);
		}
	}
	
}
