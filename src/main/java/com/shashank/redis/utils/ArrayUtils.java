package com.shashank.redis.utils;

public class ArrayUtils {
	
	public static byte[] addAll(byte[] array1, byte[] array2) {
		byte[] joinedArray = new byte[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}
	
}
