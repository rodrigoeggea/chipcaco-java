package com.chipcacojava;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.chipcaco.util.AudioWriter;
import com.chipcaco.util.BinUtil;
import com.chipcaco.util.LogUtil;

public class Converter {
	private Logger logger = LogUtil.getSimpleLogger();
	private String srcVideoPath;
	private String dstVideoPath;
	private String dstAudioPath;
	private RandomAccessFile inputFile;
	private RandomAccessFile outVideoFile;
	private RandomAccessFile outAudioFile;
	private ConvertionInfo convertionInfo = new ConvertionInfo();

	public ConvertionInfo getConvertionInfo() {
		return convertionInfo;
	}
	
	// Empty constructor
	public Converter() {}
	
	public Converter(String srcVideoFile, String dstVideoFile, String dstAudioFile) {
		this.srcVideoPath = srcVideoFile;
		this.dstVideoPath = dstVideoFile;
		this.dstAudioPath = dstAudioFile;
	}
	
	public void execute() throws IOException {	
		List<Integer> frameTimeList = new ArrayList<Integer>();
		int hxvfCounter = 0, hxafCounter = 0;
		int frameCount = 0;
		int videoLength = 0;
		int SPScounter = 0;
		int dataSize = 0;
		int iSliceCounter = 0, pSliceCounter = 0;
		int width, height = 0;
		
		// Load Files
		inputFile    = new RandomAccessFile(this.srcVideoPath, "r");
		outVideoFile = new RandomAccessFile(this.dstVideoPath, "rw");
		outAudioFile = new RandomAccessFile(this.dstAudioPath, "rw");
		
		// get HXVS header
		byte[] header = new byte[16];
		inputFile.read(header);
		String str = new String(header);

		// Verify file format
		if (str.substring(0, 4).equals("HXVS")) {
			logger.info("Correct format HXVS");
		} else {
			inputFile.close();
			outVideoFile.close();
			outAudioFile.close();
			FileUtils.deleteQuietly(new File(dstVideoPath));
			FileUtils.deleteQuietly(new File(dstAudioPath));
			throw new IOException("Invalid file format.");
		}
		// get width and height from HXVS header
		width = BinUtil.getIntfromLE(header, 4);
		height = BinUtil.getIntfromLE(header, 8);

		// Skip 44 bytes reserved for WAV header in output audio file
		outAudioFile.seek(44);

		// Converting
		logger.info("Converting...");
		while (inputFile.getFilePointer() < inputFile.length()) {
			inputFile.read(header);
			str = new String(header);
			logger.info("-------------------------------");
			/************************************************
			 *              EXTRACT VIDEO
			 ************************************************/
			if (str.substring(0, 4).equals("HXVF")) {
				// verify video header
				logger.info("Header HXVF found");
				hxvfCounter++;
				logger.info("Header Counter=" + hxvfCounter);

				// get video datasize
				int datasize = BinUtil.getIntfromLE(header, 4);
				int timestamp = BinUtil.getIntfromLE(header, 8);
				
				// frameType=1 (Metadata frame only, no image in frame)
				// frameType=2 (Frame with image data)
				int frameType = BinUtil.getIntfromLE(header, 12);
				dataSize += datasize;

				logger.info("Video block datasize=" + datasize);
				logger.info("Frame timestamp=" + timestamp);
				frameTimeList.add(timestamp);
				logger.fine("frameType=" + frameType);

				// write video data block to output file
				byte[] videodata = new byte[datasize];
				inputFile.read(videodata, 0, datasize);
				outVideoFile.write(videodata, 0, datasize);

				// read h264 NALU (NAL Unit to get NAL type)
				// nal_unit_type = 5 (IDR = full frame)
				// nal_unit_type = 1 (SLICE = partial frame)
				byte nal_unit_type = (byte) (videodata[4] & 0x1f);
				logger.fine(String.format("nal_unit_type=%02x", nal_unit_type));
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
				logger.info("Header HXAF found");
				hxafCounter++;

				// get audio datasize
				int datasize = BinUtil.getIntfromLE(header, 4) - 4; // Skip 4 unkown bytes {0x0001,0x5000}
				logger.info("Audio Datasize=" + datasize);
				logger.info("Audio block datasize=" + datasize);
				dataSize+=datasize;

				// Write data block to output file
				byte[] audiodata = new byte[datasize];
				inputFile.skipBytes(4); // Skip 4 unkown bytes {0x0001,0x5000}
				inputFile.read(audiodata, 0, datasize);
				outAudioFile.write(audiodata, 0, datasize);

			} else if (str.subSequence(0, 4).equals("HXFI")) {
				logger.info("Header HXFI found");
				videoLength = BinUtil.getIntfromLE(header, 8);
				break;
			} else {
				logger.severe("HEADER ERROR - CCODE=" + str.substring(0, 4));
			}
		}
		
		// Write audio file only if have data
		 if(outAudioFile.length() > 0) {
			 AudioWriter.writeHeader(outAudioFile);
		 }
		 
			// Close files
			logger.info("Closing files");
			inputFile.close();
			outVideoFile.close();
			outAudioFile.close();
			
		/******************************************
		 *    STORE CONVERTION RESULT
		 ******************************************/
		 convertionInfo.setDataSize(dataSize);
		 convertionInfo.setFrameCount(frameCount);
		 convertionInfo.setFrameTimeList(frameTimeList);
		 convertionInfo.setHxCounters(hxvfCounter, hxafCounter);
		 convertionInfo.setSize(width, height);
		 convertionInfo.setSliceCounters(iSliceCounter, pSliceCounter);
		 convertionInfo.setSPScounter(SPScounter);
		 convertionInfo.setVideoLength(videoLength);
		
		// Convertion Report
		printConvertionReport();
	}

	public void printConvertionReport() {
		// Print file information
		logger.info("---------------------------------------------");
		logger.info("Input video file  = " + srcVideoPath);
		logger.info("Output video file = " + dstVideoPath);
		logger.info("Output audio file = " + dstAudioPath);
		logger.info(convertionInfo.toString());
		logger.info("---------------------------------------------");
	}
}
