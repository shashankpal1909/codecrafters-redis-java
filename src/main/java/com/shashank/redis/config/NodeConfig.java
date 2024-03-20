package com.shashank.redis.config;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NodeConfig {
	
	private final List<Socket> replicas;
	private int port = 6379;
	private Role role = Role.MASTER;
	private ReplicaConfig replicaConfig;
	private String replicationId;
	private Long replicationOffSet = 0L;
	
	public NodeConfig(String[] args) {
		this.replicas = new ArrayList<>();
		
		parseArgs(args);
		
		if (role == Role.MASTER) setInitialMasterConfig();
	}
	
	private void parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String param = args[i].toLowerCase().substring(2);
			switch (param) {
				case "port" -> port = Integer.parseInt(args[++i]);
				case "replicaof" -> {
					role = Role.SLAVE;
					replicaConfig = new ReplicaConfig(args[++i], Integer.parseInt(args[++i]));
				}
				default -> throw new RuntimeException("Invalid parameter: " + param);
			}
		}
	}
	
	private void setInitialMasterConfig() {
		this.replicationId = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
		this.replicationOffSet = 0L;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getRole() {
		return role.toString().toLowerCase();
	}
	
	public ReplicaConfig getReplicaConfig() {
		return replicaConfig;
	}
	
	public String getReplicationId() {
		return replicationId;
	}
	
	public Long getReplicationOffSet() {
		return replicationOffSet;
	}
	
	public void setReplicationOffSet(Long replicationOffSet) {
		this.replicationOffSet = replicationOffSet;
	}
	
	public boolean isMaster() {
		return role == Role.MASTER;
	}
	
	public boolean isReplica() {
		return role == Role.SLAVE;
	}
	
	public List<Socket> getReplicas() {
		return replicas;
	}
	
	public void addReplica(Socket socket) {
		replicas.add(socket);
	}
	
	private enum Role {
		MASTER, SLAVE
	}
	
}
