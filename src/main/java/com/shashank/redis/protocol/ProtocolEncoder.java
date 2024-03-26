package com.shashank.redis.protocol;

import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class ProtocolEncoder {
	
	private static final String CRLF = "\r\n";
	
	public byte[] nestedArray(List<Object> values) {
		byte[] response = String.format("*%s%s", values.size(), CRLF).getBytes();
		List<byte[]> bulkStrings = values.stream().map(item -> {
			if (item instanceof String) {
				return bulkString((String) item);
			} else if (item instanceof List) {
				return nestedArray((List<Object>) item);
			}
			return null;
		}).toList();
		for (byte[] bulkString : bulkStrings) {
			response = ArrayUtils.addAll(response, bulkString);
		}
		return response;
	}
	
	public byte[] array(List<String> values) {
		byte[] response = String.format("*%s%s", values.size(), CRLF).getBytes();
		List<byte[]> bulkStrings = values.stream().map(this::bulkString).toList();
		for (byte[] bulkString : bulkStrings) {
			response = ArrayUtils.addAll(response, bulkString);
		}
		return response;
	}
	
	public byte[] bulkString(String value) {
		if (value == null) return String.format("$-1%s", CRLF).getBytes();
		return String.format("$%s%s%s%s", value.length(), CRLF, value, CRLF).getBytes();
	}
	
	public byte[] integer(Integer value) {
		return String.format(":%s%s", value, CRLF).getBytes();
	}
	
	public byte[] simpleString(String value) {
		return String.format("+%s%s", value, CRLF).getBytes();
	}
	
	public byte[] simpleError(String value) {
		return String.format("-%s%s", value, CRLF).getBytes();
	}
	
}
