package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtils {
	public static final long DEFAULT_BUFFER_SIZE = 1024*1024*1;
	
	public boolean transfer(FileInputStream fis, FileOutputStream fos, long bufferSize) {
		return true;
	}
}
