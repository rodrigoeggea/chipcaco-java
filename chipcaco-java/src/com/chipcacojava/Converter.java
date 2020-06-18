package com.chipcacojava;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Converter {
	private static final Logger log = Logger.getLogger(Converter.class.getName());
	private boolean writeAudio=false;
	private String srcVideoPath;
	private String dstVideoPath;
	private String dstAudioPath;
	private RandomAccessFile inputFile;
	private RandomAccessFile outVideoFile;
	private RandomAccessFile outAudioFile;
	private int width, height;
	private List<Integer> frameTimeList = new ArrayList<Integer>();
	private int hxvfCounter = 0;
	private int hxafCounter = 0;
	private int frameCount = 0;
	private int videoLength = 0;
	private int SPScounter = 0;
	private int dataSize = 0;
	private float framerate = 0;
	private Level logLevel = Level.OFF;
	private int iSliceCounter=0;
	private int pSliceCounter=0;

	public boolean isWriteAudio() {
		return writeAudio;
	}

	public void setWriteAudio(boolean writeAudio) {
		this.writeAudio = writeAudio;
	}

	public String getSrcVideoPath() {
		return srcVideoPath;
	}

	public void setSrcVideoPath(String srcVideoPath) {
		this.srcVideoPath = srcVideoPath;
	}

	public String getDstVideoPath() {
		return dstVideoPath;
	}

	public void setDstVideoPath(String dstVideoPath) {
		this.dstVideoPath = dstVideoPath;
	}

	public String getDstAudioPath() {
		return dstAudioPath;
	}

	public void setDstAudioPath(String dstAudioPath) {
		this.dstAudioPath = dstAudioPath;
	}

	public Level getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(Level logLevel) {
		this.logLevel = logLevel;
		configLog();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<Integer> getFrameTimeList() {
		return frameTimeList;
	}

	public int getHxvfCounter() {
		return hxvfCounter;
	}

	public int getHxafCounter() {
		return hxafCounter;
	}

	public int getFrameCount() {
		return frameCount;
	}

	public int getVideoLength() {
		return videoLength;
	}

	public int getSPScounter() {
		return SPScounter;
	}

	public int getDataSize() {
		return dataSize;
	}

	public float getFramerate() {
		return framerate;
	}
	
	public String getVideoHourMinSec() {
		return TimeUtil.convertMillisToHourMinSec(videoLength);
	}
	
	private void configLog() {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s%n");
		log.setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(logLevel);
		log.addHandler(handler);
		log.setUseParentHandlers(false);
	}

	public Converter(String srcVideoFile, String dstVideoFile, String dstAudioFile) {
		configLog();	
		this.srcVideoPath = srcVideoFile;
		this.dstVideoPath = dstVideoFile;
		this.dstAudioPath = dstAudioFile;
	}
	
	public void exec() throws IOException {	
		// Load Files
		inputFile    = new RandomAccessFile(this.srcVideoPath, "r");
		outVideoFile = new RandomAccessFile(this.dstVideoPath, "rw");
		if(writeAudio) outAudioFile = new RandomAccessFile(this.dstAudioPath, "rw");
		
		// get HXVS header
		byte[] header = new byte[16];
		inputFile.read(header);
		String str = new String(header);

		// Verify file format
		if (str.substring(0, 4).equals("HXVS")) {
			log.info("Correct format HXVS");
		} else {
			throw new IOException("Invalid file format.");
		}
		// get width and height from HXVS header
		width = BinUtil.getIntfromLE(header, 4);
		height = BinUtil.getIntfromLE(header, 8);

		// Skip 44 bytes reserved for WAV header in output audio file
		if(writeAudio) outAudioFile.seek(44);

		// Converting
		log.info("Converting...");
		while (inputFile.getFilePointer() < inputFile.length()) {
			inputFile.read(header);
			str = new String(header);
			log.info("-------------------------------");
			/************************************************
			 *              EXTRACT VIDEO
			 ************************************************/
			if (str.substring(0, 4).equals("HXVF")) {
				// verify video header
				log.info("Header HXVF found");
				hxvfCounter++;
				log.info("Header Counter=" + hxvfCounter);

				// get video datasize
				int datasize = BinUtil.getIntfromLE(header, 4);
				int timestamp = BinUtil.getIntfromLE(header, 8);
				
				// frameType=1 (Metadata frame only, no image in frame)
				// frameType=2 (Frame with image data)
				int frameType = BinUtil.getIntfromLE(header, 12);
				dataSize += datasize;

				log.info("Video block datasize=" + datasize);
				log.info("Frame timestamp=" + timestamp);
				frameTimeList.add(timestamp);
				log.fine("frameType=" + frameType);

				// write video data block to output file
				byte[] videodata = new byte[datasize];
				inputFile.read(videodata, 0, datasize);
				outVideoFile.write(videodata, 0, datasize);

				// read h264 NALU (NAL Unit to get NAL type)
				// nal_unit_type = 5 (IDR = full frame)
				// nal_unit_type = 1 (SLICE = partial frame)
				byte nal_unit_type = (byte) (videodata[4] & 0x1f);
				log.fine(String.format("nal_unit_type=%02x", nal_unit_type));
				if ((nal_unit_type == 7)) {
					SPScounter++;
				}
				if((nal_unit_type == 5)) {
					iSliceCounter++;
					frameCount++;
				}
				if ((nal_unit_type == 1)) {
					pSliceCounter++;
					frameCount++;
				}
				/******************************************
				 *       EXTRACT AUDIO (EXPERIMENTAL)
				 ******************************************/
			} else if ( str.subSequence(0, 4).equals("HXAF")) {			
				// verify audio header
				log.info("Header HXAF found");
				hxafCounter++;

				// get audio datasize
				int datasize = BinUtil.getIntfromLE(header, 4) - 4; // Skip 4 unkown bytes {0x0001,0x5000}
				log.info("Audio Datasize=" + datasize);
				log.info("Audio block datasize=" + datasize);
				dataSize+=datasize;

				// Write data block to output file
				byte[] audiodata = new byte[datasize];
				inputFile.skipBytes(4); // Skip 4 unkown bytes {0x0001,0x5000}
				inputFile.read(audiodata, 0, datasize);
				if(writeAudio)outAudioFile.write(audiodata, 0, datasize);

			} else if (str.subSequence(0, 4).equals("HXFI")) {
				log.info("Header HXFI found");
				videoLength = BinUtil.getIntfromLE(header, 8);
				break;
			} else {
				log.severe("HEADER ERROR - CCODE=" + str.substring(0, 4));
			}
		}
		// Write audio file
		if(writeAudio) writeWavHeader(outAudioFile);
		
		// Close files
		log.info("Closing files");
		outVideoFile.close();
		if(writeAudio) outAudioFile.close();
		
		// Print file information
		log.info("-------------------------------");
		log.info("Input video file  = " + srcVideoPath);
		log.info("Output video file = " + dstVideoPath);
		if(writeAudio)log.info("Output audio file = " + dstAudioPath);
		
		framerate = frameCount/ (videoLength / 1000F);
		log.info("Video length = " + videoLength + " ms");
		log.info("Resolution   = " + width + " x " + height);
		
		log.info("I-Frames     = " + iSliceCounter);
		log.info("P-Frames     = " + pSliceCounter);		
		log.info("I+P Frames   = " + frameCount);
		
		float keyFrameInterval=((pSliceCounter/iSliceCounter) + 1);
		log.info("Keyframe interval = " + keyFrameInterval + " frames");
		
		float keyFrameIntervalMs=keyFrameInterval*(float)videoLength/frameCount;
		log.info("Keyframe interval = " + keyFrameIntervalMs + " ms");
		
		float eachFrameTime=keyFrameIntervalMs/keyFrameInterval;
		log.info("Frame time = " + eachFrameTime + " ms");
		
		log.info("Framerate  =  " + framerate + " FPS");
		log.info("HXVF headers= " + hxvfCounter);
		log.info("HXAF headers= " + hxafCounter);
		log.info("-------------------------------");
	}

	/**
	 * Write WAV header (44 bytes) at the beginning of the file
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void writeWavHeader(RandomAccessFile file) throws IOException {
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
	}
}
