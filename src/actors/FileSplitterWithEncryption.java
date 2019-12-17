package actors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import utils.Progress;

public class FileSplitterWithEncryption extends Action implements FileSplitter {
	public static final long DEFAULT_BUFFER_SIZE = 1024*1024*1;
	
	private String key;
	private Cipher cipher;
	public String getKey() { return key; }
	public boolean setKey(String key) {
		if (key != null)
			this.key = key;
		return (key != null);
	}
	
	public enum SplitResult {
		OK,
		MISSING_FILE,
		SIZE_TOO_BIG,
		GENERIC_ERROR
	}
	
	protected long partSize;
	public long getPartSize() { return partSize; }
	public void setPartSize(long size) { partSize = size; }
	
	public FileSplitterWithEncryption(File inputFile, String key) throws FileNotFoundException, InvalidKeyException { this(inputFile, key, DEFAULT_BUFFER_SIZE); }
	public FileSplitterWithEncryption(String inputFilePath, String key) throws FileNotFoundException, InvalidKeyException { this(inputFilePath, key, DEFAULT_BUFFER_SIZE); }
	
	public FileSplitterWithEncryption(String inputFilePath, String key, long partSize) throws FileNotFoundException, InvalidKeyException { this(new File(inputFilePath), key, partSize); } 
	
	public FileSplitterWithEncryption(File inputFile, String key, int partSize) throws FileNotFoundException, InvalidKeyException { this(inputFile, key, (long) partSize); }
	public FileSplitterWithEncryption(File inputFile, String key, long partSize) throws FileNotFoundException, InvalidKeyException {
		if (!setFile(inputFile)) {
			throw new FileNotFoundException();
		}
		if (key == null)
			throw new InvalidKeyException();
		setKey(key);
		setPartSize(partSize);

		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(key.toCharArray(), "password".getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int split() {
		// If the file doesn't exist anymore, return an error
		if (!file.exists() || !file.canRead()) return SplitResult.MISSING_FILE.ordinal();
		
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
			System.out.println("ERROR ;-;");
			e.printStackTrace();
			return SplitResult.GENERIC_ERROR.ordinal();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SplitResult.OK.ordinal();
	}
	
}