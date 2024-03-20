package com.shashank.redis.protocol;

import com.shashank.redis.exception.EndOfStreamException;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.stream.IntStream;

public class ProtocolDecoder {
	
	public DecodedData<String> decode(DataInputStream stream) {
		try {
			char ch = (char) stream.readByte();
			
			var data = switch (ch) {
				case '*' -> decodeArray(stream);
				case '$' -> decodeBulkString(stream);
				case '+' -> decodeSimpleString(stream);
				default -> throw new RuntimeException(String.format("Unknown character: %s", ch));
			};
			
			return new DecodedData<>(data.data().trim(), data.bytesCount() + 1);
		} catch (EOFException e) {
			throw new EndOfStreamException();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private DecodedData<String> decodeArray(DataInputStream stream) throws IOException {
		DecodedData<Integer> decodedDigits = readDigits(stream);
		int arrayLength = decodedDigits.data();
		
		return IntStream.range(0, arrayLength).mapToObj(i -> decode(stream)).reduce(new DecodedData<>("",
				decodedDigits.bytesCount()), (first, second) -> new DecodedData<>(first.data() + " " + second.data(),
				first.bytesCount() + second.bytesCount()));
	}
	
	private DecodedData<String> decodeBulkString(DataInputStream stream) throws IOException {
		long bytesCount = 0L;
		
		DecodedData<Integer> decodedDigits = readDigits(stream);
		int stringLength = decodedDigits.data();
		
		bytesCount += decodedDigits.bytesCount();
		
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < stringLength; i++) {
			stringBuilder.append((char) stream.readByte());
			bytesCount++;
		}
		
		// Skip `/r/n`
		stream.readByte();
		stream.readByte();
		
		bytesCount += 2;
		
		return new DecodedData<>(stringBuilder.toString(), bytesCount);
	}
	
	private DecodedData<String> decodeSimpleString(DataInputStream stream) throws IOException {
		long bytesCount = 1L;
		
		StringBuilder stringBuilder = new StringBuilder();
		for (char ch = (char) stream.readByte(); ch != '\r'; ch = (char) stream.readByte()) {
			stringBuilder.append(ch);
			bytesCount++;
		}
		
		// Skip `\n`
		stream.readByte();
		
		bytesCount++;
		
		return new DecodedData<>(stringBuilder.toString(), bytesCount);
	}
	
	public String decodeRDbFile(DataInputStream stream) throws IOException {
		char ch = (char) stream.readByte();
		
		if (ch != '$') throw new RuntimeException(String.format("Unexpected start of RDB file: %s", ch));
		
		int stringLength = readDigits(stream).data();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < stringLength; i++) {
			stringBuilder.append((char) stream.readByte());
		}
		
		return stringBuilder.toString();
	}
	
	
	private DecodedData<Integer> readDigits(DataInputStream stream) throws IOException {
		long bytesCount = 1L;
		
		StringBuilder stringBuilder = new StringBuilder();
		for (char ch = (char) stream.readByte(); ch != '\r'; ch = (char) stream.readByte()) {
			stringBuilder.append(ch);
			bytesCount++;
		}
		
		// Skip `\n`
		stream.readByte();
		bytesCount++;
		
		return new DecodedData<>(Integer.parseInt(stringBuilder.toString()), bytesCount);
	}
	
}
