package actors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;


/**
 * The abstract class {@code Action} is the superclass of all the classes that modify
 * {@link File}.
 */
public abstract class Action {
	
	/**
	 * A {link Status} representing the status of the {@link Action}
	 *
	 */
	public enum Status {
		WAITING,
		PROCESSING,
		FINISHED,
		ERROR
	}
	
	/**
	 * Constructor that takes in a {@link File}
	 * @param f The input file
	 * @throws FileNotFoundException
	 */
	public Action(File f) throws FileNotFoundException {
		if (!setFile(f)) {
			setStatus(Status.ERROR);
			throw new FileNotFoundException();
		}
	}
	
	/**
	 * The current {@link Status} of the {@link Action}
	 */
	protected Status status = Status.WAITING;
	
	/**
	 * Returns the {@link Action}'s current status
	 * @return
	 */
	public Status getStatus() { return status; }
	
	/**
	 * Sets the {@link Action}'s current status
	 * @param s The new {@link Status}
	 */
	public void setStatus(Status s) { 
		status = s;
	}
	
	/**
	 * The {@link Action}'s file
	 */
	protected File file;
	
	/**
	 * Sets the {@link Action}'s {@link File} as a path
	 * @param filePath The new file's path
	 * @return
	 */
	public boolean setFile(String filePath) {
		if (filePath == null) return false;
		File f = new File(filePath);
		if (!f.exists() || !f.canRead()) return false;
		
		file = f;
		return true;
	}
	
	/**
	 * Sets the {@link Action}'s {@link File}
	 * @param f The new {@link File}'s path
	 * @return
	 */
	public boolean setFile(File f) {
		if (f == null || !f.exists()) return false;
		
		file = f;
		return true;
	}
	
	/**
	 * Returns the {@link Action}'s {@link File}
	 * @return
	 */
	public File getFile() {
		return file;
	}
	
	
}
