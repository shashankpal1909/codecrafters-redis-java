package com.shashank.redis.protocol;

public class ProtocolEncoder {
	
	private static final String CRLF = "\r\n";
	
	public byte[] simpleString(String target) {
		return String.format("+%s%s", target, CRLF).getBytes();
	}
	
	public byte[] bulkString(String target) {
		if (target == null) return String.format("$-1%s", CRLF).getBytes();
		return String.format("$%s%s%s%s", target.length(), CRLF, target, CRLF).getBytes();
	}
	
	
}
