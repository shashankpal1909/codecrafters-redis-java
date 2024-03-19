package com.shashank.redis;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler extends Thread {
	
	private final Socket socket;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
		System.out.println("client socket = " + socket + " connected!");
	}
	
	@Override
	public void run() {
		try (DataInputStream inputStream = new DataInputStream(socket.getInputStream()); OutputStream outputStream = socket.getOutputStream()) {
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
				if (socket != null) {
					socket.close();
					System.out.println("client socket = " + socket + " disconnected!");
				}
			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
			}
		}
	}
}
