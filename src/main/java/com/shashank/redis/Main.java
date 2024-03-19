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
			System.out.println("received byte = " + inputStream.readByte());
			
			OutputStream outputStream = clientSocket.getOutputStream();
			outputStream.write("+PONG\r\n".getBytes(StandardCharsets.UTF_8));
			
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
