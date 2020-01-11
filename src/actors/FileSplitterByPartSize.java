package actors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.FileUtils;

/**
 * A {@link FileSplitter} that splits {@link File}s by part size
 */
public class FileSplitterByPartSize extends Action implements FileSplitter {
	public static final long DEFAULT_BUFFER_SIZE = 1024*1024*1;
	
	/**
	 * An {@code enum} that defines the possible results of a split
	 */
	public enum SplitResult {
		OK,
		MISSING_FILE,
		SIZE_TOO_BIG,
		GENERIC_ERROR
	}
	
	/**
	 * The size of each partition.
	 */
	protected long partSize;
	
	/**
	 * Gets the size of each partition.
	 * @return The size of each partition
	 */
	public long getPartSize() { return partSize; }
	
	/**
	 * Sets the size of each partition.
	 * @param size The size of each partition
	 * @return {@code true} if successful, {@code false} if not
	 */
	public boolean setPartSize(long size) { 
		if (size > 0) {
			partSize = size; 
			return true;
		}
		return false;
	}
	
	/**
	 * Constructor that takes in a {@link File}
	 * @param inputFile The the input {@link File}
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartSize(File inputFile) throws FileNotFoundException { this(inputFile, DEFAULT_BUFFER_SIZE); }
	
	/**
	 * Constructor that takes in a path
	 * @param inputFilePath The path of the input {@link File}
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartSize(String inputFilePath) throws FileNotFoundException { this(inputFilePath, DEFAULT_BUFFER_SIZE); }
	
	/**
	 * Constructor that takes in a path and a partition size
	 * @param inputFilePath The path of the input {@link File}
	 * @param partSize The size of each partition
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartSize(String inputFilePath, long partSize) throws FileNotFoundException { this(new File(inputFilePath), partSize); } 
	
	/**
	 * Constructor that takes in a {@link File} and a partition size
	 * @param inputFile The input {@link File}
	 * @param partSize The size of each partition
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartSize(File inputFile, int partSize) throws FileNotFoundException { this(inputFile, (long) partSize); }
	
	/**
	 * Constructor that takes in a {@link File} and a partition size
	 * @param inputFile The input {@link File}
	 * @param partSize The size of each partition
	 * @throws FileNotFoundException
	 */
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
            	System.out.println("part: " + i + "/" + parts + "; len: " + len + "; offset: " + ((i-1)*partSize));
            	
            	FileUtils.transfer(inputStream, outputStream, len);
                
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
