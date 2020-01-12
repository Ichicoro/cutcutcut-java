package actors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.FileUtils;

/**
 * A {@link FileSplitter} that splits {@link File}s by part count
 */
public class FileSplitterByPartCount extends Action implements FileSplitter {
	public static final long DEFAULT_COUNT = 1;
	
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
	 * The amount of parts that the source {@link File} will be split into.
	 */
	protected long partCount;
	
	/**
	 * Retuns the part count
	 * @return The part count
	 */
	public long getPartCount() { return partCount; }
	
	/**
	 * Sets the part count
	 * @param count the amount of parts
	 * @return {@code true} if successful, {@code false} if not
	 */
	public boolean setPartCount(long count) {
		if (count >= 1) {
			partCount = count;
			return true;
		}
		return false;
	}
	
	/**
	 * Constructor that takes in a {@link File}
	 * @param inputFile The the input {@link File}
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartCount(File inputFile) throws FileNotFoundException { this(inputFile, DEFAULT_COUNT); }
	
	/**
	 * Constructor that takes in a {@code String} path
	 * @param inputFilePath The path of the input {@link File}
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartCount(String inputFilePath) throws FileNotFoundException { this(inputFilePath, DEFAULT_COUNT); }
	
	/**
	 * Constructor that takes in a {@code String} path and a {@code partCount}
	 * @param inputFilePath The path of the input {@link File}
	 * @param partCount The amount of partitions
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartCount(String inputFilePath, long partCount) throws FileNotFoundException { this(new File(inputFilePath), partCount); } 
	
	/**
	 * Constructor that takes in a {@link File} and a {@code partCount}
	 * @param inputFile The input {@link File}
	 * @param partCount The amount of partitions
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartCount(File inputFile, int partCount) throws FileNotFoundException { this(inputFile, (long) partCount); }
	
	/**
	 * Constructor that takes in a {@link File} and a {@code partCount}
	 * @param inputFile The input {@link File}
	 * @param partCount The amount of partitions
	 * @throws FileNotFoundException
	 */
	public FileSplitterByPartCount(File inputFile, long partCount) throws FileNotFoundException {
		super(inputFile);
		setPartCount(partCount);
	}

	@Override
	public int split() {
		// If the file doesn't exist anymore, return an error
		if (!file.exists() || !file.canRead()) { 
			setStatus(Status.ERROR);
			return SplitResult.MISSING_FILE.ordinal();
		}
		setStatus(Status.PROCESSING);
		
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
            	System.out.println("part: " + i + "/" + parts + "; len: " + len + "; offset: " + ((i-1)*partSize));
            	
            	FileUtils.transfer(inputStream, outputStream, len);
                outputStream.close();
            }
            
    		inputStream.close();
		} catch (IOException e) {
			System.out.println("ERROR ;-;");
			setStatus(Status.ERROR);
			e.printStackTrace();
			return SplitResult.GENERIC_ERROR.ordinal();
		}
		setStatus(Status.FINISHED);
		return SplitResult.OK.ordinal();
	}
	
}
