package com.chipcacojava;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.chipcaco.util.TimeUtil;

public class ConvertionInfo {
	private List<Integer> frameTimeList = new ArrayList<Integer>();
	private int hxvfCounter = 0;
	private int hxafCounter = 0;
	private int frameCount = 0;
	private int videoLength = 0;
	private int SPScounter = 0;
	private int dataSize = 0;
	private int iSliceCounter = 0;
	private int pSliceCounter = 0;
	private int width, height;
	

	public int getHxvfCounter() {
		return hxvfCounter;
	}

	public int getHxafCounter() {
		return hxafCounter;
	}

	/**
	 * Return the total numer of Frames in video.
	 * @return
	 */
	public int getFrameCount() {
		return frameCount;
	}

	/**
	 * Return the duration of video in milliseconds.
	 * @return
	 */
	public int getVideoLength() {
		return videoLength;
	}

	public int getSPScounter() {
		return SPScounter;
	}

	public int getDataSize() {
		return dataSize;
	}

	public int getiSliceCounter() {
		return iSliceCounter;
	}

	public int getpSliceCounter() {
		return pSliceCounter;
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

	void setHxCounters(int hxvfCounter, int hxafCounter) {
		this.hxvfCounter = hxvfCounter;
		this.hxafCounter = hxafCounter;
	}

	void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}

	void setVideoLength(int videoLength) {
		this.videoLength = videoLength;
	}

	void setSPScounter(int SPScounter) {
		this.SPScounter = SPScounter;
	}

	void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	void setSliceCounters(int iSliceCounter, int pSliceCounter) {
		this.iSliceCounter = iSliceCounter;
		this.pSliceCounter = pSliceCounter;
	}

	void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	void setFrameTimeList(List<Integer> frameTimeList) {
		this.frameTimeList = frameTimeList;
	}

	//******************* CUSTOM GETTERS ****************************//
	
	/**
	 * Returns the video duration formatted in <b><i> hours : minutes : seconds </i></b> 
	 * @return
	 */
	public String getVideoHourMinSec() {
		return TimeUtil.convertMillisToHourMinSec(videoLength);
	}

	/**
	 * Returns the calculated KeyFrame Interval. A KeyFrame interval is the interval between two key frames (I-frame).
	 * A key frame (I frame) is a frame encoded in its entirety , much like a JPEG image. 
	 * Other frames (P and B) are "predicted", ie, they contain only the changes from frame to frame. 
	 * @return interval in number of frames
	 */
	public float getKeyFrameInterval() {
		float keyFrameInterval = pSliceCounter/iSliceCounter + 1; 
		return keyFrameInterval;
	}
	
	/**
	 * Returns the calculated KeyFrame Interval. A KeyFrame interval is the interval between two key frames (I-frame).
	 * A key frame (I frame) is a frame encoded in its entirety , much like a JPEG image. 
	 * Other frames (P and B) are "predicted", ie, they contain only the changes from frame to frame. 
	 * @return interval in milliseconds.
	 */
	public float getKeyFrameIntervalMs() {
		float keyFrameIntervalMs = getKeyFrameInterval() * (float) videoLength / frameCount;
		return keyFrameIntervalMs;
	}
	
	/**
	 * Returns the calculated Framerate from the video.
	 * @return
	 */
	public float getFramerate() {
		float framerate = frameCount/ (videoLength / 1000F);
		return framerate;
	}
	
	/**
	 * Returna the duration of each frame.
	 * @return
	 */
	public float getFrameDuration() {
		float eachFrameTime = getKeyFrameIntervalMs() / getKeyFrameInterval();
		return eachFrameTime;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append(MessageFormat("Input  video file = {0}", srcVideoPath));
//		sb.append(MessageFormat("Output video file = {0}", dstVideoPath));
//		sb.append(MessageFormat("Output audio file = {0}", dstAudioPath));
		sb.append(MessageFormat.format("Video length = {0} ms    \n", videoLength));
		sb.append(MessageFormat.format("Resolution   = {0} x {1} \n", width, height));
		sb.append(MessageFormat.format("I-Frames     = {0}       \n", iSliceCounter));
		sb.append(MessageFormat.format("P-Frames     = {0}       \n", pSliceCounter));		
		sb.append(MessageFormat.format("I+P Frames   = {0}       \n", frameCount));
		sb.append(MessageFormat.format("Keyframe interval = {0}  \n", getKeyFrameInterval()));
		sb.append(MessageFormat.format("Keyframe interval = {0} ms\n",getKeyFrameIntervalMs()));
		sb.append(MessageFormat.format("Frame duration = {0}     \n", getFrameDuration()));
		sb.append(MessageFormat.format("Framerate  = {0}         \n", getFramerate()));
		sb.append(MessageFormat.format("HXVF headers= {0}        \n", hxvfCounter));
		sb.append(MessageFormat.format("HXAF headers= {0}        \n", hxafCounter));
		return sb.toString();
	}          
}
