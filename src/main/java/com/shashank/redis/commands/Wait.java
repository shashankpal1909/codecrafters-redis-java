package com.shashank.redis.commands;

import com.shashank.redis.config.ObjectFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Wait extends CommandHandler {
	
	private final AtomicInteger acknowledgedReplicaCount = new AtomicInteger();
	
	public Wait(ObjectFactory objectFactory) {
		super(objectFactory);
	}
	
	@Override
	public byte[] execute(String[] args) {
		int expectedReplicaCount;
		long timeOutMillis;
		
		try {
			expectedReplicaCount = Integer.parseInt(args[1]);
			timeOutMillis = Long.parseLong(args[2]);
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Invalid parameters for WAIT command");
		}
		
		// Get list of replica sockets
		List<Socket> replicas = objectFactory.getNodeConfig().getReplicas();
		
		// Map each replica to a CompletableFuture representing async task
		Stream<CompletableFuture<Void>> futures = replicas.stream()
				.map(replica -> CompletableFuture.runAsync(() -> getAcknowledgement(replica)));
		
		// If timeout is specified, set up futures to complete exceptionally after timeout
		if (timeOutMillis > 0) {
			futures = futures.map(future ->
					future.completeOnTimeout(null, timeOutMillis, TimeUnit.MILLISECONDS));
		}
		
		try {
			// Wait for all futures to complete
			CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		// Get count of acknowledged replicas and reset counter
		int ackCount = acknowledgedReplicaCount.intValue();
		acknowledgedReplicaCount.set(0);
		
		return objectFactory.getProtocolEncoder().integer(ackCount == 0 ? replicas.size() : ackCount);
	}
	
	private void getAcknowledgement(Socket replicaSocket) {
		try {
			DataInputStream inputStream = new DataInputStream(replicaSocket.getInputStream());
			OutputStream outputStream = replicaSocket.getOutputStream();
			
			byte[] ackCommand = objectFactory.getProtocolEncoder().array(List.of("REPLCONF", "GETACK", "*"));
			outputStream.write(ackCommand);
			System.out.printf("Ack command sent: %s\n", new String(ackCommand));
			
			String ackResponse = objectFactory.getProtocolDecoder().decode(inputStream).data();
			System.out.printf("Ack response received: %s\n", ackResponse);
			
		} catch (IOException e) {
			System.out.printf("Acknowledgement failed: %s\n", e.getMessage());
		}
	}
	
}
