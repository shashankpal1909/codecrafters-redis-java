package com.shashank.redis;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
	
	public static void main(String[] args) throws IOException {
		System.out.println("Redis Starting Up...");
		
		int port = 6379;
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			serverSocket.setReuseAddress(true);
			while (true) new ClientHandler(serverSocket.accept()).start();
		}
	}
	
}
