package com.chipcacojava;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinUtil {
	/**
	 * Converts a word (two bytes) to an Integer using Little-Endian representation).
	 * @param buffer Two bytes to be swapped.
	 * @param offset Number of bytes to skip before read the integer.
	 * @return
	 */
	public static int getIntfromLE(byte buffer[], int offset) {
		int result = (buffer[offset] & 0xFF) | (buffer[offset + 1] & 0xFF) << 8 | (buffer[offset + 2] & 0xFF) << 16
				| (buffer[offset + 3] & 0xFF) << 24;
		return result;
	}

	/**
	 * Converts a word (two bytes) to an Integer using Little-Endian representation) using ByteBuffer.
	 * @param buffer Two bytes to be swapped.
	 * @param offset Number of bytes to skip before read the integer.
	 * @return
	 */
	public static int getIntegerfromLE(byte buffer[], int offset) {
		ByteBuffer headerBuffer = ByteBuffer.allocateDirect(buffer.length);
		headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
		headerBuffer.put(buffer);
		int result = headerBuffer.getInt(offset);
		return result;
	}
	
	/**
	 * Print Bytes in Hex format.
	 * @param b
	 * @param size
	 */
	public static void hexprint(byte b[], int size) {
		for (int i = 0; i < size; i++) {
			System.out.print(String.format("%02x", b[i]) + " ");
		}
		System.out.println("");
	}
}
