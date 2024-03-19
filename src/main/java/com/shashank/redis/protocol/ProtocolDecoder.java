package com.shashank.redis.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProtocolDecoder {
	
	public String decode(DataInputStream stream) {
		try {
			char ch = stream.readChar();
			
			return switch (ch) {
				case '*' -> decodeArray(stream);
				case '$' -> decodeBulkString(stream);
				case '+' -> decodeSimpleString(stream);
				default -> throw new RuntimeException(String.format("Unknown character: %s", ch));
			};
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String decodeArray(DataInputStream stream) throws IOException {
		int arrayLength = readDigits(stream);
		
		return IntStream.range(0, arrayLength).mapToObj(i -> decode(stream)).collect(Collectors.joining(" "));
	}
	
	private String decodeBulkString(DataInputStream stream) throws IOException {
		int stringLength = readDigits(stream);
		
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < stringLength; i++) {
			stringBuilder.append((char) stream.readByte());
		}
		
		// Skip `/r/n`
		stream.readByte();
		stream.readByte();
		
		return stringBuilder.toString();
	}
	
	private String decodeSimpleString(DataInputStream stream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		for (char ch = (char) stream.readByte(); ch != '\r'; ch = (char) stream.readByte()) {
			stringBuilder.append(ch);
		}
		
		// Skip `\n`
		stream.readByte();
		
		return stringBuilder.toString();
	}
	
	
	private int readDigits(DataInputStream stream) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		for (char ch = (char) stream.readByte(); ch != '\r'; ch = (char) stream.readByte()) {
			stringBuilder.append(ch);
		}
		
		// Skip `\n`
		stream.readByte();
		
		return Integer.parseInt(stringBuilder.toString());
	}
	
}
