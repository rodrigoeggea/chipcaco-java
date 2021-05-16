package com.chipcaco.mp4parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

import org.mp4parser.Container;
import org.mp4parser.muxer.FileDataSourceImpl;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.tracks.AACTrackImpl;
import org.mp4parser.muxer.tracks.h264.H264TrackImpl;

public class Mp4Muxer {
	private static final Logger logger = Logger.getGlobal();
	
	/**
	 * Only works with AAC audio files.
	 * 
	 * @param videofile
	 * @param aacAudiofile
	 * @param mp4file
	 * @param framerate
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void muxAudioVideo(String videofile, String aacAudiofile, String mp4file, long framerate)
			throws FileNotFoundException, IOException {
		Movie movie = new Movie();

		// try-with-resources
		try (H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl(videofile), "eng", framerate, 1);
				AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl(aacAudiofile));) {
			movie.addTrack(h264Track);
			movie.addTrack(aacTrack);
			Container container = new DefaultMp4Builder().build(movie);

			try (FileOutputStream fos = new FileOutputStream(new File(mp4file)); FileChannel fc = fos.getChannel();) {
				container.writeContainer(fc);
			}
		}
	}

	public static void muxVideo(String videofile, String mp4file, long framerate)
			throws FileNotFoundException, IOException {
		H264TrackImpl h264Track = null;
		FileOutputStream fos = null;
		FileChannel fc = null;
		FileDataSourceImpl fds = null;
		Movie movie = new Movie();
		Container container;

		try {
			fds = new FileDataSourceImpl(videofile);
			h264Track = new H264TrackImpl(fds, "eng", framerate, 1);  // Bug: Locking file
			movie.addTrack(h264Track);
			container = new DefaultMp4Builder().build(movie);
			fos = new FileOutputStream(new File(mp4file));
			fc = fos.getChannel();
			container.writeContainer(fc);
		} finally {
			// CLOSE NOT WORKING, FILE STILL LOCKED.
			if(h264Track != null) h264Track.close(); 
			if(fds != null) fds.close();
			if(fos != null) fos.close();
			if(fc != null) fc.close();
			// FORCE MP4PARSER TO RELEASE FILE
			h264Track = null;
			fds = null;
			fc = null;
			movie = null;
			container = null;
			System.gc();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
