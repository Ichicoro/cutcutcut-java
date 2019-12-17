package actors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.Progress;

public class FileSplitterByPartSize extends Action implements FileSplitter {
	public static final long DEFAULT_BUFFER_SIZE = 1024*1024*1;
	
	public enum SplitResult {
		OK,
		MISSING_FILE,
		SIZE_TOO_BIG,
		GENERIC_ERROR
	}
	
	protected long partSize;
	public long getPartSize() { return partSize; }
	public void setPartSize(long size) { partSize = size; }
	
	public FileSplitterByPartSize(File inputFile) throws FileNotFoundException { this(inputFile, DEFAULT_BUFFER_SIZE); }
	public FileSplitterByPartSize(String inputFilePath) throws FileNotFoundException { this(inputFilePath, DEFAULT_BUFFER_SIZE); }
	
	public FileSplitterByPartSize(String inputFilePath, long partSize) throws FileNotFoundException { this(new File(inputFilePath), partSize); } 
	
	public FileSplitterByPartSize(File inputFile, int partSize) throws FileNotFoundException { this(inputFile, (long) partSize); }
	public FileSplitterByPartSize(File inputFile, long partSize) throws FileNotFoundException {
		super(inputFile);
		setPartSize(partSize);
	}

	@Override
	public int split() {
		// If the file doesn't exist anymore, return an error
		if (!file.exists() || !file.canRead()) {
			setStatus(Status.ERROR);
			return SplitResult.MISSING_FILE.ordinal();
		}
		setStatus(Status.PROCESSING);
		
		long parts = (file.length() + partSize - 1) / partSize;
        long lastPartSize = file.length() - (partSize * (parts - 1));
		FileInputStream inputStream = null;
		
		String basePath = file.getAbsolutePath() + ".dpart";
		try {
			inputStream = new FileInputStream(getFile());
			
			int extLength = String.valueOf(parts - 1).length();
			extLength = extLength < 3 ? 3 : extLength;
            
            for (int i = 1; i<=parts; i++) {
                File outputFile = new File(basePath + String.format("%0" + extLength + "d", i));
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                
                long len = (i<parts) ? partSize : lastPartSize;
                byte[] buffer = new byte[(int) len];
            	System.out.println("part: " + i + "/" + parts + "; len: " + len + "; offset: " + ((i-1)*partSize));
            	
                inputStream.read(buffer, 0, (int) len);
                
                outputStream.write(buffer);
                outputStream.close();
            }
            
    		inputStream.close();
		} catch (IOException e) {
			setStatus(Status.ERROR);
			System.out.println("ERROR ;-;");
			e.printStackTrace();
			return SplitResult.GENERIC_ERROR.ordinal();
		}
		setStatus(Status.FINISHED);
		return SplitResult.OK.ordinal();
	}
	
}
