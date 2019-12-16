package actors;

import java.io.File;

public abstract class Action {
	enum Status {
		WAITING,
		PROCESSING,
		FINISHED,
		ERROR
	}
	protected File file;
	
	public boolean setFile(String filePath) {
		if (filePath == null) return false;
		File f = new File(filePath);
		if (!f.exists() || !f.canRead()) return false;
		
		file = f;
		return true;
	}
	
	public boolean setFile(File f) {
		if (f == null || !f.exists()) return false;
		
		file = f;
		return true;
	}
	
	public File getFile() {
		return file;
	}
	
	
}
