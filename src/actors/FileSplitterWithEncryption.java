package actors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A {@link FileSplitter} that splits {@link File}s by part size and encrypts each part
 */
public class FileSplitterWithEncryption extends FileSplitterByPartSize {
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
	 * The key used for decryption
	 */
	private String key;
	
	/**
	 * The {@code Cipher} used for decryption
	 */
	private Cipher cipher;
	
	
	/**
	 * Gets the {@code key}
	 * @return The {@code key}
	 */
	public String getKey() { return key; }
	
	/**
	 * Sets the {@code key}
	 * @param key The {@code key}
	 */
	public boolean setKey(String key) {
		if (key != null)
			this.key = key;
		return (key != null);
	}
	
	/**
	 * Constructor that takes in a {@link File} and a key
	 * @param inputFile The the input {@link File}
	 * @param key The key
	 * @throws FileNotFoundException
	 */
	public FileSplitterWithEncryption(File inputFile, String key) throws FileNotFoundException, InvalidKeyException { this(inputFile, key, DEFAULT_BUFFER_SIZE); }
	
	/**
	 * Constructor that takes in a path and a key
	 * @param inputFilePath The path of the input {@link File}
	 * @param key The key
	 * @throws FileNotFoundException
	 */
	public FileSplitterWithEncryption(String inputFilePath, String key) throws FileNotFoundException, InvalidKeyException { this(inputFilePath, key, DEFAULT_BUFFER_SIZE); }
	
	/**
	 * Constructor that takes in a path, a key and a partition size
	 * @param inputFilePath The path of the input {@link File}
	 * @param key The key
	 * @param partSize The size of each partition
	 * @throws FileNotFoundException
	 */
	public FileSplitterWithEncryption(String inputFilePath, String key, long partSize) throws FileNotFoundException, InvalidKeyException { this(new File(inputFilePath), key, partSize); } 
	
	/**
	 * Constructor that takes in a {@link File}, a key and a partition size
	 * @param inputFile The input {@link File}
	 * @param key The key
	 * @param partSize The size of each partition
	 * @throws FileNotFoundException
	 */
	public FileSplitterWithEncryption(File inputFile, String key, int partSize) throws FileNotFoundException, InvalidKeyException { this(inputFile, key, (long) partSize); }
	
	/**
	 * Constructor that takes in a {@link File}, a key and a partition size
	 * @param inputFile The input {@link File}
	 * @param key The key
	 * @param partSize The size of each partition
	 * @throws FileNotFoundException
	 */
	public FileSplitterWithEncryption(File inputFile, String key, long partSize) throws FileNotFoundException, InvalidKeyException {
		super(inputFile);
		if (key == null) {
			setStatus(Status.ERROR);
			throw new InvalidKeyException();
		}
		setKey(key);
		setPartSize(partSize);

		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(key.toCharArray(), "password".getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(new byte[16]));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException e) {
			setStatus(Status.ERROR);
			e.printStackTrace();
		}
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
		
		String basePath = file.getAbsolutePath() + ".epart";
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
                
                outputStream.write(cipher.doFinal(buffer));
                outputStream.close();
            }
            
    		inputStream.close();
		} catch (IOException e) {
			setStatus(Status.ERROR);
			System.out.println("ERROR ;-;");
			e.printStackTrace();
			return SplitResult.GENERIC_ERROR.ordinal();
		} catch (IllegalBlockSizeException e) {
			setStatus(Status.ERROR);
			e.printStackTrace();
		} catch (BadPaddingException e) {
			setStatus(Status.ERROR);
			e.printStackTrace();
		}
		setStatus(Status.FINISHED);
		return SplitResult.OK.ordinal();
	}
	
}
