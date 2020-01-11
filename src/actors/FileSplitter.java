package actors;

/**
 * All types of File splitters inherit from this interface.
 * It exposes a {@code split()} method.
 */
public interface FileSplitter {
	/**
	 * Splits the {@link File}
	 * @return A status code implemented in the mergers
	 */
	public int split();
}
