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
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import actors.Action.Status;

public class EncryptedFileMerger extends Action implements FileMerger {
	public EncryptedFileMerger(String fileName, String key) throws FileNotFoundException, InvalidKeyException {
		this(new File(fileName), key);
	}
	
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
	
	private String key;
	private Cipher cipher;
	public String getKey() { return key; }
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

	public enum MergeResult {
		OK,
		MISSING_FILE,
		IO_ERROR,
		SIZE_TOO_BIG,
		DECRYPTION_ERROR,
		GENERIC_ERROR
	}
	
	public ArrayList<File> getFiles() {
		if (!file.exists() || !file.canRead()) {
			if (getStatus() != Status.ERROR)
				setStatus(Status.ERROR);
			return null;
		}
		ArrayList<File> files = new ArrayList<File>();
		
		String parentPath = getFile().getParent();
		String readyFilename = getFile().getName().replaceAll("(?=(.+).\\b)\\d{3}\\b", "");
		System.out.println(readyFilename);
		System.out.println(file.getPath());
		for (File f : new File(parentPath).listFiles()) {
			if (f.isFile() && f.getName().startsWith(readyFilename)) {
				System.out.println(f + " (isFile: " + f.isFile() + ", path: " + f.getPath() + ")");
				files.add(f);
			}
		}
		
		files.sort(new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		for (File f : files)
			System.out.println(f);
		
		System.out.println(getFile().getPath());
		
		return files;
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
		
		// outputStream.write(b, off, len);
		
		setStatus(Status.FINISHED);
		return MergeResult.OK.ordinal();
	}

}
