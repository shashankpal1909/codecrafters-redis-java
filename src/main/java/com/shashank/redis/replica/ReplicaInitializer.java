package com.shashank.redis.replica;

import com.shashank.redis.commands.CommandFactory;
import com.shashank.redis.commands.Handler;
import com.shashank.redis.commands.PSync;
import com.shashank.redis.commands.Replicable;
import com.shashank.redis.config.NodeConfig;
import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.config.ReplicaConfig;
import com.shashank.redis.protocol.ProtocolDecoder;
import com.shashank.redis.protocol.ProtocolEncoder;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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
		
		try (Socket masterSocket = new Socket(replicaConfig.host(), replicaConfig.port())) {
			DataInputStream inputStream = new DataInputStream(masterSocket.getInputStream());
			OutputStream outputStream = masterSocket.getOutputStream();
			
			initializeReplica(inputStream, outputStream);
			
			while (true) {
				String commandString = protocolDecoder.decode(inputStream);
				System.out.printf("[master] command received: %s\n", commandString);
				
				String[] args = commandString.split(" ");
				String command = args[0].toUpperCase();
				Handler handler = commandFactory.getCommandHandler(command);
				byte[] response = handler.execute(args);
				
				// outputStream.write(response);
				// outputStream.flush();
				//
				// System.out.printf("[master] response sent: %s\n", new String(response));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void initializeReplica(DataInputStream inputStream, OutputStream outputStream) throws IOException {
		// Send a PING to master node
		byte[] command = protocolEncoder.array(List.of("PING"));
		outputStream.write(command);
		String response = protocolDecoder.decode(inputStream);
		if (!response.equalsIgnoreCase("PONG")) {
			System.out.printf("Unexpected response for PING: %s\n", response);
		} else {
			System.out.printf("Received response for PING: %s\n", response);
		}
		
		// REPLCONF listening-port <PORT>
		command = protocolEncoder.array(List.of("REPLCONF", "listening-port", String.valueOf(nodeConfig.getPort())));
		outputStream.write(command);
		response = protocolDecoder.decode(inputStream);
		if (!response.equalsIgnoreCase("OK")) {
			System.out.printf("Unexpected response for REPLCONF listening-port: %s\n", response);
		} else {
			System.out.printf("Received response for REPLCONF listening-port: %s\n", response);
		}
		
		// REPLCONF capa psync2
		command = protocolEncoder.array(List.of("REPLCONF", "capa", "psync2"));
		outputStream.write(command);
		response = protocolDecoder.decode(inputStream);
		if (!response.equalsIgnoreCase("OK")) {
			System.out.printf("Unexpected response for REPLCONF capa: %s\n", response);
		} else {
			System.out.printf("Received response for REPLCONF capa: %s\n", response);
		}
		
		// PSYNC ? -1
		command = protocolEncoder.array(List.of("PSYNC", "?", "-1"));
		outputStream.write(command);
		response = protocolDecoder.decode(inputStream);
		if (!response.startsWith("FULLRESYNC")) {
			System.out.printf("Unexpected response for PSYNC: %s\n", response);
		} else {
			System.out.printf("Received response for PSYNC: %s\n", response);
		}
		
		String rDbFile = protocolDecoder.decodeRDbFile(inputStream);
		System.out.printf("Received RDB File: %s", rDbFile);
		System.out.println("Replica Initialized...");
	}
	
}
