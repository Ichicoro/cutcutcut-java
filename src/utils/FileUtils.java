package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
	public static boolean verifyMergeFilename(String filename) {
		return (Pattern.compile("(?=\\b)([dec])(?=part0+1\\b)").matcher(filename).find());
	}
	
	public static char getMergeFileType(String filename) {
		Matcher m = Pattern.compile("(?=\\b)([dec])(?=part0+1\\b)").matcher(filename);
		if (m.find())
			return m.group(0).charAt(0);
		return 0;
	}
}
