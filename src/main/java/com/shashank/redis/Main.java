package com.shashank.redis;

import com.shashank.redis.config.NodeConfig;
import com.shashank.redis.config.ObjectFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;

public class Main {
	
	public static void main(String[] args) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		System.out.println("Redis Starting Up...");
		
		NodeConfig nodeConfig = new NodeConfig(args);
		ObjectFactory objectFactory = new ObjectFactory(nodeConfig);
		
		try (ServerSocket serverSocket = new ServerSocket(nodeConfig.getPort())) {
			serverSocket.setReuseAddress(true);
			while (true) new ClientHandler(serverSocket.accept(), objectFactory).start();
		}
	}
	
}
