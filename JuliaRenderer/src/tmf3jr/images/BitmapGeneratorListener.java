package tmf3jr.images;

/**
 * Event listener of generating bitmap images
 */
public interface BitmapGeneratorListener {
	/**
	 * Invoked when bitmap generation started
	 */
	public void generateStarted();
	
	/**
	 * Invoked when bitmap generation finished
	 * @param elapsedTime elapsed time from bitmap generation started
	 */
	public void generateCompleted(long elapsedTime);
}
