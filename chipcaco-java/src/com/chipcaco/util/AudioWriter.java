package com.chipcaco.util;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Utility class to write WAV Audio file.
 * 
 * @author Rodrigo Eggea
 *
 */
public class AudioWriter {
	
	private AudioWriter() {};
	
	/**
	 * Write WAV header (44 bytes) at the beginning of the file and close file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void writeHeader(RandomAccessFile file) throws IOException {
		// Goto the begin of audio file
		file.seek(0);
		int abytes = ((int) file.length() - 44); // Audio bytes = fileSize - 44 bytes (wav header)

		// RIFF Header
		String chunkID = "RIFF";           // contains RIFF
		int chunkSize = (abytes + 44 - 8); // abytes + 44 bytes (wav_header) - 8 (unkown bytes);
		String format = "WAVE";            // contains "WAVE"

		// Format Header
		String subchunk1ID = "fmt ";
		int subchunk1size = 16;   // Should be 16 for PCM
		short audioFormat = 6;    // audio format
		short numChannels = 1;    // Mono=1, Stereo=2
		int sampleRate = 8000;    // 8000, 44100, etc..
		int byteRate = 8000;      // SampleRate * NumChannels * BitsPerSample/8
		short blockAlign = 2;     // NumChannels * BitsPerSample/8
		short bitsPerSample = 16; // 8-bits = 8, 16-bits= 16

		// Writing the header
		file.writeBytes(chunkID);
		file.writeInt(ByteSwapper.swap(chunkSize));
		file.writeBytes(format);
		file.writeBytes(subchunk1ID);
		file.writeInt(ByteSwapper.swap(subchunk1size));
		file.writeShort(ByteSwapper.swap(audioFormat));
		file.writeShort(ByteSwapper.swap(numChannels));
		file.writeInt(ByteSwapper.swap(sampleRate));
		file.writeInt(ByteSwapper.swap(byteRate));
		file.writeShort(ByteSwapper.swap(blockAlign));
		file.writeShort(ByteSwapper.swap(bitsPerSample));

		// Write audio data to file
		file.writeBytes("data"); // contains "data"
		file.writeInt(ByteSwapper.swap(abytes)); // number of bytes in data
		
		// Close audio file
		file.close();
	}
}
