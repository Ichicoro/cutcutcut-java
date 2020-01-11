package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 * A class with some utility functions for {@link File}s
 */
public class FileUtils {
	private final static int DEFAULT_BUFFER_LENGTH = 1024 * 1024 * 1;
	
	/**
	 * Verifies if a filename is in the pattern of {@code .[de]part001}
	 * @param filename The filename
	 * @return {@code true} if the filename matches the pattern, {@code false} otherwise
	 */
	public static boolean verifyMergeFilename(String filename) {
		return (Pattern.compile("(?=\\b)([de])(?=part0+1\\b)").matcher(filename).find());
	}
	
	/**
	 * Returns the type of merge required for a specified {@link File}
	 * @param filename The filename
	 * @return 0 if the file isn't mergeable, {@code d} if it's just split and {@code e} if it is also encrypted.
	 */
	public static char getMergeFileType(String filename) {
		Matcher m = Pattern.compile("(?=\\b)([de])(?=part0+1\\b)").matcher(filename);
		if (m.find())
			return m.group(0).charAt(0);
		return 0;
	}
	
	/**
	 * Transfers data between Streams using a buffer, so that RAM usage stays low. The maximum buffer length is 1MB
	 * @param fis The {@link FileInputStream}
	 * @param fos The {@link FileOutputStream}
	 * @param partLength The size of the chunk of data
	 * @throws IOException
	 */
	public static void transfer(FileInputStream fis, FileOutputStream fos, long partLength) throws IOException {
        transfer(fis,fos,partLength, DEFAULT_BUFFER_LENGTH);
    }

	/**
	 * Transfers data between Streams using a buffer, so that RAM usage stays low
	 * @param fis The {@link FileInputStream}
	 * @param fos The {@link FileOutputStream}
	 * @param partLength The size of the chunk of data
	 * @param maxBufferLength The maximum size of the buffer -- by default, 1MB
	 * @throws IOException
	 */
    public static void transfer(FileInputStream fis, FileOutputStream fos, long partLength, int maxBufferLength) throws IOException {
        int bufferLength;
        int lastBufferLength;
        byte[] buffer;
        long numberOfTransfers;
        
        if (partLength > maxBufferLength) {
        	// In case there's more than one part, we need to iter over each part with our buffer
            bufferLength = maxBufferLength;
            numberOfTransfers = (int) ((partLength - 1) / bufferLength) + 1;
            lastBufferLength = (int) (partLength - bufferLength * (numberOfTransfers - 1));
            buffer = new byte[bufferLength];
            
            for (long i = numberOfTransfers; i > 1; i--) {
                fis.read(buffer);
                fos.write(buffer);
            }
        } else {
        	// Else, it's okay to cast to int
            lastBufferLength = (int) partLength;
        }

        buffer = new byte[lastBufferLength];
        fis.read(buffer);
        fos.write(buffer);
    }
}
