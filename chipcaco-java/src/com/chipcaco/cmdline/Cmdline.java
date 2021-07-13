package com.chipcaco.cmdline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.chipcaco.util.LogUtil;

/**
 * Java-chipcaco (Chinese IP Camera Converter) is a Java application for
 * converting .264 files produced by some Chinese IP cameras that can't be
 * played or converted by common applications like vlc or ffmpeg. This is a port
 * of the C application by Ralph Spitzner (https://www.spitzner.org/kkmoon.html)
 * and also based on chipcaco written in node-JS by Sven Jacobs
 * (https://github.com/svenjacobs/chipcaco)
 * 
 * Example: java -jar chipcaco.jar camera_recording.264
 * 
 * Version: 1.0.4 Date: 16/05/2021
 * 
 * @author Rodrigo Eggea (rodrigo.eggea@gmail.com)
 * 
 */
public class Cmdline {
	public static final String VERSION = "1.0.4";
	private static final Logger logger = LogUtil.getSimpleLogger();
	
	public static void showUsage() {
		System.out.println(
				  " Java-chipcaco is a tool for converting recording files from Chinese cameras \n"
				+ " (from brands like TPTEK and ieGeek) from garbled H264 format to MP4 format  \n"
				+ " that can be played any video player with MP4 support. The audio file is     \n"
				+ " extracted to a separated WAV file when possible.                            \n"
				+ " To convert multiple files in current directory use asterisk and extension   \n"
				+ " of files like '*.264'                                                       \n"
				);
		System.out.println();
		System.out.println("Usage: java -jar chipcaco.jar [options] <filename>\n");
		System.out.println("Options:");
		System.out.println("  -d  debug information during conversion");		
		System.out.println("  -version  show software version");
	}

	public static void main(String args[]) throws FileNotFoundException, IOException {
		List<String> argList = Arrays.asList(args);
		String argString      = Arrays.toString(args);
						
		// No args show usage
		if(args.length == 0 ) {
			showUsage();
			System.exit(0);
		}
		
		// Show version and exit
		if(argList.contains("-version")){
			System.out.println("Java-chipcaco " + VERSION);
			System.out.println("Developer: Rodrigo Eggea");	
			System.out.println("Contact: rodrigo.eggea@gmail.com");
			System.out.println("Website: https://github.com/rodrigoeggea/chipcaco-java ");
			System.exit(0);
		}
		
		// Allow multiple files in arguments
		// Filter only valid filenames
		List<String> filesToConvert = argList.stream()
				.filter(name -> !name.startsWith("-"))
				.collect(Collectors.toList());
		
		// Verify if all passed files really exists		
		for(String filename: filesToConvert) {
			File testFile = new File(filename);
			if(!testFile.exists() || !testFile.isFile()) {
				System.out.println("File not found: " + filename);
				System.exit(-1);
			}
		}
		
		// Error if no files to convert
		if(filesToConvert.size()==0) {
			System.out.println("No files to convert.");
			System.exit(-1);
		}
		
		// Enable debug
		if(argList.contains("-d")) {
			if(filesToConvert.size()>1) {
				System.out.println("Debug allowed only for a single file.");
				System.exit(-1);
			} else {
				logger.setLevel(Level.INFO);
			}
		} else {
			//logger.setLevel(Level.OFF);
			logger.setLevel(Level.OFF);
		}
		
		// Converting files
		System.out.format("Converting file: %s \n\n", filesToConvert);

		for(String fileToConvert : filesToConvert) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					new ChipcacoExec(fileToConvert);
				}
			}).start();
		}
	}
}

