package actors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

public abstract class Action {
	enum Status {
		WAITING,
		PROCESSING,
		FINISHED,
		ERROR
	}
	
	public Action(File f) throws FileNotFoundException {
		if (!setFile(f)) {
			setStatus(Status.ERROR);
			throw new FileNotFoundException();
		}
	}
	
	protected Status status = Status.WAITING;
	public Status getStatus() { return status; }
	protected void setStatus(Status s) { 
		status = s;
		if (statusChanged != null)
			statusChanged.actionPerformed(new ActionEvent(this, status.ordinal(), "status_changed " + status.ordinal()));
	}
	
	ActionListener statusChanged = null;
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
