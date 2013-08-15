package dtool.tests;

import java.util.regex.Pattern;


public class MiscDeeTestUtils {
	
	public static final Pattern LINE_SPLITTER = Pattern.compile("\n|(\r\n)|\r");
	
	public static String[] splitLines(String exclusionsFile) {
		return LINE_SPLITTER.split(exclusionsFile);
	}
	
}