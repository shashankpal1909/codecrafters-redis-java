package com.shashank.redis.protocol;

import com.shashank.redis.utils.ArrayUtils;

import java.util.List;

public class ProtocolEncoder {
	
	private static final String CRLF = "\r\n";
	
	public byte[] simpleString(String value) {
		return String.format("+%s%s", value, CRLF).getBytes();
	}
	
	public byte[] bulkString(String value) {
		if (value == null) return String.format("$-1%s", CRLF).getBytes();
		return String.format("$%s%s%s%s", value.length(), CRLF, value, CRLF).getBytes();
	}
	
	public byte[] array(List<String> values) {
		byte[] response = String.format("*%s%s", values.size(), CRLF).getBytes();
		List<byte[]> bulkStrings = values.stream().map(this::bulkString).toList();
		for (byte[] bulkString : bulkStrings) {
			response = ArrayUtils.addAll(response, bulkString);
		}
		return response;
	}
	
}
