package com.chipcaco.cmdline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.chipcaco.mp4parser.Mp4Muxer;
import com.chipcacojava.Converter;

public class ChipcacoExec {
	
	public ChipcacoExec(String srcViceo) {
		execute(srcViceo);
	}
	
	public void execute(String srcVideo) {
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter(sb);
		
		String basename = FilenameUtils.getFullPath(srcVideo) + FilenameUtils.getBaseName(srcVideo);
		String dstVideo  = basename + ".h264";
		String dstAudio  = basename + ".wav";
		String outputMp4 = basename + ".mp4";
		
		// Create new converter
		Converter converter = new Converter(srcVideo, dstVideo, dstAudio);
		
		
		// EXTRACTING H264 VIDEO FROM GARBLED FILE
		try {
			//System.out.println("Extracting file: " + srcVideo);
			fmt.format("Extracting file: %s \n", srcVideo);
			converter.execute();
		} catch (FileNotFoundException e) {
			System.out.println("File not found= " + srcVideo);
			System.exit(-1);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		// MUXING VIDEO WITH MP4PARSER MUXER LIBRARY
		//System.out.println("Converting to MP4...");
		fmt.format("Converting to MP4 \n");
		try {
			Mp4Muxer.muxVideo(dstVideo, outputMp4, (long) converter.getConvertionInfo().getFramerate());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}

		// Delete intermediate H264 video file
		FileUtils.deleteQuietly(new File(dstVideo));
		
		// Delete if audio file is empty
		File audioFile = new File(dstAudio);
		if(audioFile.length() == 0) {
			FileUtils.deleteQuietly(audioFile);
		}
		
		fmt.format("Output video file= %s \n", outputMp4);
		fmt.format(Locale.US, "File information: Video size=%dx%d  FPS=%.2f  Length=%s\n", 
		converter.getConvertionInfo().getWidth(),
		converter.getConvertionInfo().getHeight(),
		converter.getConvertionInfo().getFramerate(),
		converter.getConvertionInfo().getVideoHourMinSec());
		fmt.format("Done. \n");
		fmt.close();
		System.out.println(sb.toString());
	}
}
