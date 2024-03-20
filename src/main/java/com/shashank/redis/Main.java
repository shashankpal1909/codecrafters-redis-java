package com.shashank.redis;

import com.shashank.redis.config.NodeConfig;
import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.replica.ReplicaInitializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;

public class Main {
	
	public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		NodeConfig nodeConfig = new NodeConfig(args);
		ObjectFactory objectFactory = new ObjectFactory(nodeConfig);
		
		try (ServerSocket serverSocket = new ServerSocket(nodeConfig.getPort())) {
			serverSocket.setReuseAddress(true);
			System.out.printf("Redis server running on port: %s\n", nodeConfig.getPort());
			
			if (nodeConfig.isReplica()) {
				System.out.println("Replica node initializing...");
				new ReplicaInitializer(objectFactory).start();
			} else {
				System.out.println("Master node started...");
			}
			
			while (true) new ClientHandler(serverSocket.accept(), objectFactory).start();
		}
	}
	
}
