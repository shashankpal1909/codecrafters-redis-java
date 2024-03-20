package com.shashank.redis;

import com.shashank.redis.commands.CommandFactory;
import com.shashank.redis.commands.Handler;
import com.shashank.redis.commands.PSync;
import com.shashank.redis.commands.Replicable;
import com.shashank.redis.config.NodeConfig;
import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.exception.EndOfStreamException;
import com.shashank.redis.protocol.ProtocolDecoder;
import com.shashank.redis.replica.CommandReplicator;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
	
	private final Socket socket;
	private final NodeConfig nodeConfig;
	private final ProtocolDecoder protocolDecoder;
	private final CommandFactory commandFactory;
	private final CommandReplicator commandReplicator;
	private boolean isReplicaSocket;
	
	public ClientHandler(Socket socket, ObjectFactory objectFactory) {
		this.isReplicaSocket = false;
		this.socket = socket;
		this.nodeConfig = objectFactory.getNodeConfig();
		this.protocolDecoder = objectFactory.getProtocolDecoder();
		this.commandFactory = objectFactory.getCommandFactory();
		this.commandReplicator = objectFactory.getCommandReplicator();
		System.out.println("client socket = " + socket + " connected!");
	}
	
	@Override
	public void run() {
		try (DataInputStream inputStream = new DataInputStream(socket.getInputStream()); OutputStream outputStream = socket.getOutputStream()) {
			while (true) {
				String commandString = protocolDecoder.decode(inputStream);
				
				String[] args = commandString.split(" ");
				String command = args[0].toUpperCase();
				Handler handler = commandFactory.getCommandHandler(command);
				byte[] response = handler.execute(args);
				
				if (handler instanceof PSync) {
					isReplicaSocket = true;
					
					nodeConfig.addReplica(this.socket);
					System.out.printf("Replica with port %s has been added%n\n", socket.getPort());
					
					outputStream.write(response);
					outputStream.flush();
					
					break;
				}
				
				if (handler instanceof Replicable) {
					commandReplicator.replicateCommand(commandString);
				}
				
				outputStream.write(response);
				outputStream.flush();
			}
		} catch (EndOfStreamException e) {
			System.out.println("End of input stream reached");
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		} finally {
			try {
				if (!isReplicaSocket) {
					socket.close();
					System.out.println("client socket = " + socket + " disconnected!");
				}
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
			}
		}
	}
}
