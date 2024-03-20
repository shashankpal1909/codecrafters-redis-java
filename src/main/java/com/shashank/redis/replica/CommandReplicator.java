package com.shashank.redis.replica;

import com.shashank.redis.config.ObjectFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class CommandReplicator {
	
	private final ObjectFactory objectFactory;
	
	public CommandReplicator(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	
	public void replicateCommand(String command) {
		objectFactory.getNodeConfig().getReplicas().forEach(replica -> replicate(replica, command));
	}
	
	public void replicate(Socket socket, String command) {
		try {
			System.out.printf("Replicating %s\n", command);
			OutputStream outputStream = socket.getOutputStream();
			byte[] request = objectFactory.getProtocolEncoder().array(List.of(command.split(" ")));
			outputStream.write(request);
			outputStream.flush();
		} catch (IOException e) {
			System.out.println("Replication error: " + e.getMessage());
		}
	}
	
}
