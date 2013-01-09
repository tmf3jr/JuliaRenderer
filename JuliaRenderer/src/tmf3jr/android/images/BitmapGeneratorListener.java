package tmf3jr.android.images;

/**
 * Event listener of generating bitmap images
 */
public interface BitmapGeneratorListener {
	/**
	 * Invoked when bitmap generation started
	 */
	public void onGenerationStarted();
	
	/**
	 * Invoked when bitmap generation finished
	 * @param elapsedTime elapsed time from bitmap generation started
	 */
	public void onGenerationCompleted(long elapsedTime);
}
