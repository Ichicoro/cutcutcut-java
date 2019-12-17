package actors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

public class DefaultFileMerger extends Action implements FileMerger {
	public DefaultFileMerger(String fileName) throws FileNotFoundException {
		super(new File(fileName));
	}
	
	public DefaultFileMerger(File f) throws FileNotFoundException {
		super(f);
	}
	
	@Override
	public boolean setFile(File f) {
		if (!f.getName().endsWith(".dpart001"))
			return false;
		file = f;
		return true;
	}

	public enum MergeResult {
		OK,
		MISSING_FILE,
		IO_ERROR,
		SIZE_TOO_BIG,
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
			if (f.getName().startsWith(readyFilename)) {
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
		
		File outputFile = new File(getFile().getPath().replaceAll("(?=\\b)[d,e,c]part0+1\\b", ""));
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
				
				outputStream.write(buffer, (int) offset, buffer.length);
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
		}
		
		// outputStream.write(b, off, len);
		
		setStatus(Status.FINISHED);
		return MergeResult.OK.ordinal();
	}

}
