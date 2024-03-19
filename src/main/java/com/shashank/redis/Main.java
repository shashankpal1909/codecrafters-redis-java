package com.shashank.redis;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
	public static void main(String[] args) {
		System.out.println("Redis Starting Up...");
		ServerSocket serverSocket;
		Socket clientSocket = null;
		int port = 6379;
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			clientSocket = serverSocket.accept();
			
			DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
			OutputStream outputStream = clientSocket.getOutputStream();
			
			while (true) {
				byte[] bytes = new byte[256];
				int n = inputStream.read(bytes);
				
				if (n == -1) break;
				
				System.out.printf("received %s bytes: %s%n", n, new String(bytes));
				outputStream.write("+PONG\r\n".getBytes(StandardCharsets.UTF_8));
			}
			
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		} finally {
			try {
				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
			}
		}
	}
}
