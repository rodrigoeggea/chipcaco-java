package com.chipcaco.test;

import com.chipcaco.cmdline.Cmdline;

public class TestConverter {
	public static void main(String args[]) throws Exception {
		//usage();
		version();
		//withDebug();
		//withoutDebug();
		//multipleFiles();
	}
	
	static void usage() throws Exception {
		 // No args show usage
		String[] args = {}; 
		Cmdline.main(args);
	}
	
	static void version() throws Exception {
		// show version
		String[] args = {"-version"};
		Cmdline.main(args);
	}
	
	static void withDebug() throws Exception {
		// With debug
		String filename ="C:\\TPTEK\\sample2.264";
		String[] args = {"-d",filename};
		Cmdline.main(args);
	}

	static void withoutDebug() throws Exception {
		// Without debug
		String filename ="C:\\TPTEK\\sample1.264";
		String[] args = {filename};
		Cmdline.main(args);
	}
	
	static void multipleFiles() throws Exception {
		// Without debug
		String[] args = {"*.*"};
		Cmdline.main(args);
	}
}
