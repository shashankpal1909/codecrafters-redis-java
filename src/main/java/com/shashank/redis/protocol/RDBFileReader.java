package com.shashank.redis.protocol;

import com.ning.compress.lzf.LZFDecoder;
import com.shashank.redis.storage.Data;
import com.shashank.redis.storage.Storage;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RDBFileReader {
	
	private final String filePath;
	private final Map<String, Data> dataMap;
	private int rdbVersion;
	
	public RDBFileReader(String filePath) {
		this.filePath = filePath;
		this.dataMap = new ConcurrentHashMap<>();
		
		parse();
	}
	
	public Map<String, Data> getDataMap() {
		return dataMap;
	}
	
	public void parse() {
		try (FileInputStream fileInputStream = new FileInputStream(this.filePath); DataInputStream stream =
				new DataInputStream(fileInputStream)) {
			
			verifyMagicString(stream);
			verifyVersion(stream);
			
			skipToDatabaseSection(stream);
			
			int databaseHashTableSize = readLengthEncodedInt(stream);
			System.out.println("Database Hash Table Size = " + databaseHashTableSize);
			
			int expiryHashTableSize = readLengthEncodedInt(stream);
			System.out.println("Expiry Hash Table Size = " + expiryHashTableSize);
			
			processKeyValues(stream);
			
		} catch (IOException ex) {
			System.out.println("IOException: " + ex.getMessage());
		}
	}
	
	private void verifyMagicString(DataInputStream stream) throws IOException {
		String magicString = new String(stream.readNBytes(5));
		if (!magicString.equals("REDIS")) {
			throw new RuntimeException("Unexpected start of file: " + magicString);
		}
	}
	
	private void verifyVersion(DataInputStream stream) throws IOException {
		int version = Integer.parseInt(new String(stream.readNBytes(4)));
		if (version < 0 || version > 11) {
			throw new RuntimeException("Unsupported version of rdb file: " + version);
		}
		this.rdbVersion = version;
	}
	
	private void skipToDatabaseSection(DataInputStream stream) throws IOException {
		byte nextByte;
		do {
			nextByte = stream.readByte();
		} while (nextByte != (byte) 0xFB);
	}
	
	private int readLengthEncodedInt(DataInputStream stream) throws IOException {
		byte nextByte = stream.readByte();
		int length = 0;
		
		if ((nextByte & 0xC0) == 0x00) {
			length = nextByte & 0x3F;
		} else if ((nextByte & 0xC0) == 0x40) {
			length = ((nextByte & 0x3F) << 8) | (stream.readByte() & 0xFF);
		} else if ((nextByte & 0xC0) == 0x80) {
			length = stream.readInt();
		} else if ((nextByte & 0xC0) == 0xC0) {
			switch (nextByte & 0x3F) {
				case 0x00:
					length = stream.readByte();
					break;
				case 0x01:
					length = stream.readShort() & 0xFFFF;
					break;
				case 0x02:
					length = stream.readInt();
					break;
				case 0x03:
					int clen = readLengthEncodedInt(stream);
					int len = readLengthEncodedInt(stream);
					byte[] compressedData = stream.readNBytes(clen);
					byte[] decompressedData = LZFDecoder.decode(compressedData);
					length = decompressedData.length;
					break;
				default:
					throw new RuntimeException("Invalid length encoding");
			}
		} else {
			throw new RuntimeException("Invalid length encoding");
		}
		
		return length;
	}
	
	private String readLengthEncodedString(DataInputStream stream) throws IOException {
		byte nextByte = stream.readByte();
		StringBuilder result = new StringBuilder();
		
		if ((nextByte & 0xC0) == 0x00) {
			int length = nextByte & 0x3F;
			byte[] bytes = stream.readNBytes(length);
			result.append(new String(bytes));
		} else if ((nextByte & 0xC0) == 0x40) {
			int length = ((nextByte & 0x3F) << 8) | (stream.readByte() & 0xFF);
			byte[] bytes = stream.readNBytes(length);
			result.append(new String(bytes));
		} else if ((nextByte & 0xC0) == 0x80) {
			int length = stream.readInt();
			byte[] bytes = stream.readNBytes(length);
			result.append(new String(bytes));
		} else if ((nextByte & 0xC0) == 0xC0) {
			switch (nextByte & 0x3F) {
				case 0x00:
					int length = stream.readByte();
					byte[] bytes1 = stream.readNBytes(length);
					result.append(new String(bytes1));
					break;
				case 0x01:
					int length2 = stream.readShort() & 0xFFFF;
					byte[] bytes2 = stream.readNBytes(length2);
					result.append(new String(bytes2));
					break;
				case 0x02:
					int length3 = stream.readInt();
					byte[] bytes3 = stream.readNBytes(length3);
					result.append(new String(bytes3));
					break;
				case 0x03:
					int clen = readLengthEncodedInt(stream);
					int len = readLengthEncodedInt(stream);
					byte[] compressedData = stream.readNBytes(clen);
					byte[] decompressedData = LZFDecoder.decode(compressedData);
					result.append(new String(decompressedData));
					break;
				default:
					throw new RuntimeException("Invalid length encoding");
			}
		} else {
			throw new RuntimeException("Invalid length encoding");
		}
		
		return result.toString();
	}
	
	private void processKeyValues(DataInputStream stream) throws IOException {
		byte nextByte;
		do {
			nextByte = stream.readByte();
			if (nextByte == 0) {
				String key = readLengthEncodedString(stream);
				System.out.println("Key = " + key);
				String value = readLengthEncodedString(stream);
				System.out.println("Value = " + value);
				this.dataMap.put(key, new Data(value, Instant.MAX));
				Storage.set(key, value);
			}
		} while (nextByte != -1);
	}
	
	public int getRdbVersion() {
		return rdbVersion;
	}
}
