package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class FileUtils {
	private final static int DEFAULT_BUFFER_LENGTH = 1024 * 1024 * 1;
	
	public static boolean verifyMergeFilename(String filename) {
		return (Pattern.compile("(?=\\b)([de])(?=part0+1\\b)").matcher(filename).find());
	}
	
	public static char getMergeFileType(String filename) {
		Matcher m = Pattern.compile("(?=\\b)([de])(?=part0+1\\b)").matcher(filename);
		if (m.find())
			return m.group(0).charAt(0);
		return 0;
	}
	
	public static void transfer(FileInputStream fis, FileOutputStream fos, long partLength) throws IOException {
        transfer(fis,fos,partLength, DEFAULT_BUFFER_LENGTH);
    }

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
