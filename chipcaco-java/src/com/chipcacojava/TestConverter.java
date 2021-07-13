package com.chipcacojava;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

import org.apache.commons.io.FilenameUtils;

public class TestConverter {
	public static void main(String args[]) throws IOException {
		String filename;
		filename="C:\\TPTEK\\samples\\tptek.264";
		Main.main(new String[]{"-d",filename});
	}
}
