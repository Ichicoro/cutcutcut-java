package actors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.Progress;

public class FileSplitterByPartCount extends Action implements FileSplitter {
	public static final long DEFAULT_COUNT = 1;
	
	public enum SplitResult {
		OK,
		MISSING_FILE,
		SIZE_TOO_BIG,
		GENERIC_ERROR
	}
	
	protected long partCount;
	public long getPartCount() { return partCount; }
	public void setPartCount(long count) { partCount = count; }
	
	public FileSplitterByPartCount(File inputFile) throws FileNotFoundException { this(inputFile, DEFAULT_COUNT); }
	public FileSplitterByPartCount(String inputFilePath) throws FileNotFoundException { this(inputFilePath, DEFAULT_COUNT); }
	
	public FileSplitterByPartCount(String inputFilePath, long partCount) throws FileNotFoundException { this(new File(inputFilePath), partCount); } 
	
	public FileSplitterByPartCount(File inputFile, int partCount) throws FileNotFoundException { this(inputFile, (long) partCount); }
	public FileSplitterByPartCount(File inputFile, long partCount) throws FileNotFoundException {
		if (!setFile(inputFile)) {
			throw new FileNotFoundException();
		}
		setPartCount(partCount);
	}

	@Override
	public int split() {
		// If the file doesn't exist anymore, return an error
		if (!file.exists() || !file.canRead()) return SplitResult.MISSING_FILE.ordinal();
		
		long parts = partCount;
		long partSize = file.length()/partCount;
		FileInputStream inputStream = null;
		
		String basePath = file.getAbsolutePath() + ".dpart";
		try {
			inputStream = new FileInputStream(getFile());
			
			int extLength = String.valueOf(parts - 1).length();
			extLength = extLength < 3 ? 3 : extLength;
            
            for (int i = 1; i<=parts; i++) {
                File outputFile = new File(basePath + String.format("%0" + extLength + "d", i));
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                
                long len = partSize;
                byte[] buffer = new byte[(int) len];
            	System.out.println("part: " + i + "/" + parts + "; len: " + len + "; offset: " + ((i-1)*partSize));
            	
                inputStream.read(buffer, 0, (int) len);
                
                outputStream.write(buffer);
                outputStream.close();
            }
            
    		inputStream.close();
		} catch (IOException e) {
			System.out.println("ERROR ;-;");
			e.printStackTrace();
			return SplitResult.GENERIC_ERROR.ordinal();
		}
		return SplitResult.OK.ordinal();
	}
	
}
