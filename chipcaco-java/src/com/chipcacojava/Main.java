package com.chipcacojava;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

import org.apache.commons.io.FilenameUtils;
/**
 * Java-chipcaco (Chinese IP Camera Converter) is a Java application for converting .264 files 
 * produced by some Chinese IP cameras that can't be played or converted by common applications like vlc or ffmpeg.
 * This is a port of the C application by Ralph Spitzner  (https://www.spitzner.org/kkmoon.html)
 * and also based on chipcaco written in node-JS by Sven Jacobs (https://github.com/svenjacobs/chipcaco)
 * 
 * The produced file can be played on VLC, but to change the framerate must be processed by 
 * ffmpeg or similar applications.
 *  
 * Example: 
 * java -jar chipcaco.jar recording.264 
 * ffmpeg -framerate <desired framerate> -i recording.h264 recording.mp4
 * 
 * Version: 1.0.1
 * Date: 16/06/2020
 * 
 * @author Rodrigo Eggea (rodrigo.eggea@gmail.com) 
 * 
 */
public class Main {
	public static final String VERSION = "1.0.1";

	public static void showUsage() {
		System.out.println("Java chipcaco is a tool for converting recording files from Chinese cameras \n"
				+ "(from brands like TPTEK and ieGeek) from garbled H264 format to the standard H264 format \n"
				+ "that can be played on the VLC or any other video player with H264 support.");
		System.out.println("Usage: java -jar chipcaco.jar [options] <filename>");
		System.out.println("Options:");
		System.out.println("  -s extract sound to wav file (experimental)");
		System.out.println("  -d debug information during conversion");
		System.out.println("  -v software version");
	}
	
	public static void main(String args[]) {
		String str = Arrays.toString(args);
		if (args.length == 0) {
			showUsage();
		}
		if (args.length == 1) {
			if (str.contains("-v")) {
				System.out.println("Java chipcaco " + VERSION);
				System.out.println("Developer: Rodrigo Eggea (rodrigo.eggea@gmail.com)");
			} else if (str.contains("-")) {
				System.out.println("Invalid argument.");
				showUsage();
			} else {
				String srcVideo = args[0];
				chipcacoExec(srcVideo,false,Level.OFF);
			}
		}
		if (args.length == 2) {
			if (args[0].equals("-s")) {
				String srcVideo = args[1];
				chipcacoExec(srcVideo,true,Level.OFF);
			}
			if (args[0].equals("-d")) {
				String srcVideo = args[1];
				chipcacoExec(srcVideo,false,Level.ALL);
			}
		}
		if (args.length == 3) {
			if (str.contains("-d") && str.contains("-s")) {
				String srcVideo = args[2];
				chipcacoExec(srcVideo,true,Level.ALL);
			} else {
				System.out.println("Too many arguments");
			}
		}
		if (args.length > 3) {
			System.out.println("Too many arguments");
			showUsage();
		}
	}
	
	public static void chipcacoExec(String srcVideo, boolean writeAudio, Level logLevel) {
		String dstVideo = FilenameUtils.getFullPath(srcVideo) + FilenameUtils.getBaseName(srcVideo) + ".h264";
		String dstAudio = FilenameUtils.getFullPath(srcVideo) + FilenameUtils.getBaseName(srcVideo) + ".wav";
		Converter converter = new Converter(srcVideo, dstVideo, dstAudio);
		converter.setWriteAudio(writeAudio);
		converter.setLogLevel(logLevel);
		try {
			System.out.println("Converting...");
			converter.exec();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}		
		System.out.println("Done.");
		System.out.println("Output file=" + converter.getDstVideoPath());
		//System.out.println("File information:");		
		System.out.format(Locale.US,"File information: Video size=%dx%d  FPS=%.2f  Length=%s\n",
				converter.getWidth(),converter.getHeight(),converter.getFramerate(),converter.getVideoHourMinSec());
		System.out.println();
		System.out.println("Run ffmpeg to fix framerate and convert to MP4:");
		System.out.format(Locale.US, " ffmpeg -framerate %.2f -i %s output.mp4 \n",
				converter.getFramerate(), converter.getDstVideoPath());

	}

}
