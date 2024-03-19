package com.shashank.redis.config;

public class NodeConfig {
	
	private int port = 6379;
	private Role role = Role.MASTER;
	private ReplicaConfig replicaConfig;
	
	private String replicationId;
	private Long replicationOffSet;
	
	public NodeConfig(String[] args) {
		parseArgs(args);
		if (role == Role.MASTER) {
			setInitialMasterConfig();
		}
	}
	
	private void setInitialMasterConfig() {
		this.replicationId = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb";
		this.replicationOffSet = 0L;
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
	
	private enum Role {
		MASTER, SLAVE;
	}
	
}
