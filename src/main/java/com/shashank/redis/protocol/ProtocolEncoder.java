package com.shashank.redis.protocol;

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
			response = addAll(response, bulkString);
		}
		return response;
	}
	
	private byte[] addAll(byte[] array1, byte[] array2) {
		byte[] joinedArray = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
	
}
