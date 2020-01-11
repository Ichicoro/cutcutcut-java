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
import java.util.ArrayList;
import java.util.Comparator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A {@link FileMerger} that can decrypt encryped splits.
 */
public class EncryptedFileMerger extends DefaultFileMerger implements FileMerger {
	/**
	 * An {@code enum} that defines the possible results of a merge
	 */
	public enum MergeResult {
		OK,
		MISSING_FILE,
		IO_ERROR,
		SIZE_TOO_BIG,
		GENERIC_ERROR,
		DECRYPTION_ERROR
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
	 * Constructor that takes in a {@code fileName}
	 * @param fileName The path of the input {@link File}
	 * @param key The "password" to the {@code File}
	 * @throws FileNotFoundException
	 * @throws InvalidKeyException
	 */
	public EncryptedFileMerger(String fileName, String key) throws FileNotFoundException, InvalidKeyException {
		this(new File(fileName), key);
	}
	
	/**
	 * Constructor that takes in a {@code fileName}
	 * @param f The input {@link File}
	 * @param key The "password" to the {@code File}
	 * @throws FileNotFoundException
	 * @throws InvalidKeyException
	 */
	public EncryptedFileMerger(File f, String key) throws FileNotFoundException, InvalidKeyException {
		super(f);
		if (key == null) {
			setStatus(Status.ERROR);
			throw new InvalidKeyException();
		}
		setKey(key);
		
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec spec = new PBEKeySpec(key.toCharArray(), "password".getBytes(), 65536, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(new byte[16]));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException| NoSuchPaddingException | InvalidAlgorithmParameterException e) {
			setStatus(Status.ERROR);
			e.printStackTrace();
		}
	}
	
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
	
	@Override
	public boolean setFile(File f) {
		if (!f.getName().endsWith(".epart001"))
			return false;
		file = f;
		return true;
	}
	
	@Override
	public int merge() {
		if (!file.exists() || !file.canRead()) { 
			System.out.println("Selected file: " + file.getAbsolutePath());
			setStatus(Status.ERROR);
			return MergeResult.MISSING_FILE.ordinal();
		}
		setStatus(Status.PROCESSING);
		
		File outputFile = new File(getFile().getPath().replaceAll("(?=\\b)\\.epart0+1\\b", ""));
		System.out.println(outputFile.getPath());
		
		ArrayList<File> inputFiles = getFiles();
		int parts = inputFiles.size();
		int i = 1;
		
		long offset = 0;
		
		try {
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			
			for (File f : inputFiles) {
				FileInputStream inputStream = new FileInputStream(f);
				byte[] buffer = new byte[(int) f.length()];
				
				// We can't use the transfer function here: it'd screw up the padding
				inputStream.read(buffer);
				outputStream.write(cipher.doFinal(buffer));
				inputStream.close();
				
				System.out.println("part: " + i + "/" + parts + "; len: " + buffer.length + "; offset: " + offset);
				offset += buffer.length;
				i++;
			}
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			setStatus(Status.ERROR);
			return MergeResult.IO_ERROR.ordinal();
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			setStatus(Status.ERROR);
			return MergeResult.DECRYPTION_ERROR.ordinal();
		}
		
		setStatus(Status.FINISHED);
		return MergeResult.OK.ordinal();
	}

}
