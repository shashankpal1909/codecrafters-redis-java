package com.shashank.redis;

import com.shashank.redis.commands.CommandFactory;
import com.shashank.redis.commands.Handler;
import com.shashank.redis.config.ObjectFactory;
import com.shashank.redis.exception.EndOfStreamException;
import com.shashank.redis.protocol.ProtocolDecoder;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
	
	private final Socket socket;
	private final ProtocolDecoder protocolDecoder;
	private final CommandFactory commandFactory;
	
	public ClientHandler(Socket socket, ObjectFactory objectFactory) {
		this.socket = socket;
		this.protocolDecoder = objectFactory.getProtocolDecoder();
		this.commandFactory = objectFactory.getCommandFactory();
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
				outputStream.write(response);
				outputStream.flush();
			}
		} catch (EndOfStreamException e) {
			System.out.println("End of input stream reached");
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
