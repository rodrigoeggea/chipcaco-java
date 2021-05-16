package com.chipcaco.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
	
	/**
	 * Return time in hour:min:sec
	 * @param milliseconds
	 * @return
	 */
	public static String convertMillisToHourMinSec(int milliseconds) {
		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
		int days    = (int) ((milliseconds / (1000 * 60 * 60)));
		int months  = (int) ((float)days / 30.4368499f); 
		int years   = (int) ((float)days / 365.242199f);
		String formatedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds); 
		return formatedTime;
	}

	/**
	 * Return time in hour:minutes:meconds
	 * @param milliseconds
	 * @return
	 */
	public static String convertMsToHMS(int milliseconds) {
		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        
		String formatedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds); 
		return formatedTime;		
	}
	
	/**
	 * Return time in hour:min:sec
	 * @param seconds
	 * @return Formated time in <b>hour : min : sec</b> 
	 */
	public static String convertToHourMinSec(int seconds) {
		int sec     = (seconds % 60);
		int min     = (seconds / 60) % 60;
		int hours   = (seconds / 3600) % 24;
		String formatedTime = String.format("%02d:%02d:%02d", hours, min, sec); 
		return formatedTime;
	}
}
