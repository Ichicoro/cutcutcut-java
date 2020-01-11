package actors;

import java.io.File;
import java.util.ArrayList;

/**
 * All types of File mergers inherit from this interface.
 * It exposes a {@code merge()} method and a {@code getFiles()} method.
 */
public interface FileMerger {
	/**
	 * Merges the {@link File}
	 * @return A status code implemented in the mergers
	 */
	int merge();
	
	/**
	 * Gets the list of {@link File}s that the {@link FileMerger} will merge together
	 * @return
	 */
	public ArrayList<File> getFiles();
}
