package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
	public static boolean verifyMergeFilename(String filename) {
		return (Pattern.compile("(?=\\b)[d,e,c](?=part0+1\\b)").matcher(filename).find());
		//return filename.matches("(?=\\b)[d,e,c]part0+1\\b");//(".[d,e,c]part0+1\\b");
	}
	
	public static char getMergeFileType(String filename) {
		Pattern p = Pattern.compile("(?=\\b)[d,e,c](?=part0+1\\b)");
		Matcher m = p.matcher(filename);
		
		if (m.find())
			return m.group(0).charAt(0);
		return 0;
	}
}
