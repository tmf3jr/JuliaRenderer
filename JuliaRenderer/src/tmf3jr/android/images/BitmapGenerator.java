package tmf3jr.android.images;

import android.graphics.Bitmap;


/**
 * Generate a bitmap in specified size. This class also has functionality of
 * event notification on start or complete a bitmap generation via
 * {@link BitmapGeneratorListener} interface.
 * This class is abstract, so sub classes have to override
 * {@link BitmapGenerator#generateBitmap(int[])} method.
 */
public abstract class BitmapGenerator {
	//field definition --------------------------------------------------------
	//bitmap generation related fields
	/** Bitmap generation event listener */
	private BitmapGeneratorListener listener = null;
	/** bottom left coordinate of the bitmap */
	private double left;
	/** bottom left coordinate of the bitmap */
	private double bottom;
	/** width of the bitmap */
	private double width;
	/** height of the bitmap */
	private double height;
	/** number of pixels in width of the bitmap  */
	private int screenWidth;
	/** number of pixels in height of the bitmap  */
	private int screenHeight;
	/** current state of generation */
	private boolean generating;

	//constructors ------------------------------------------------------------
	/**
	 * Default constructor
	 */
	public BitmapGenerator() {
		this.generating = false;
	}

	/**
	 * Create a new BitmapGenerator
	 * @param left
	 * @param bottom
	 * @param width
	 * @param height
	 * @param colorTableGenerator
	 */
	public BitmapGenerator(double left, double bottom, double width, double height,
			int resolutionX, int resolutionY) {
		this.left = left;
		this.bottom = bottom;
		this.width = width;
		this.height = height;
		this.screenWidth = resolutionX;
		this.screenHeight = resolutionY;
	}
	
	//public methods ----------------------------------------------------------
	/**
	 * Generates and returns bitmap.
	 * {@link BitmapGeneratorListener} is invoked if set.
	 * @return generated bitmap
	 */
	public Bitmap generate() {
		//sanity check
		if (this.screenWidth <= 0 || this.screenHeight <= 0 ||
			this.width <= 0 || this.height <= 0) {
			throw new IllegalStateException("Resolution, width and height must be set");
		}
		//start drawing
		Bitmap result = null;
		this.generating = true;
		try {
			int[] buffer = new int[this.screenWidth * this.screenHeight];
			//notify start event
			long startedAt = System.currentTimeMillis();
			if (this.listener != null) {
				this.listener.onGenerationStarted();
			}
			//generate bitmap
			this.generateBitmap(buffer);
			result = Bitmap.createBitmap(buffer, this.screenWidth, this.screenHeight, Bitmap.Config.ARGB_8888);
			//notify complete event
			long finishedAt = System.currentTimeMillis();
			if (this.listener != null) {
				this.listener.onGenerationCompleted(finishedAt - startedAt);
			}
		}finally{
			this.generating = false;
		}
		return result;
	}
	
	//abstract methods --------------------------------------------------------
	/**
	 * Generate bitmap and updates buffer.
	 * @param bitmap buffer of the bitmap
	 */
	protected abstract void generateBitmap(int[] bitmap);
	
	//access methods ----------------------------------------------------------
	public boolean isGenerating() {
		return generating;
	}

	public BitmapGeneratorListener getListener() {
		return listener;
	}
	
	public void setListener(BitmapGeneratorListener listener) {
		this.listener = listener;
	}

	public double getLeft() {
		return left;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public double getBottom() {
		return bottom;
	}

	public void setBottom(double bottom) {
		this.bottom = bottom;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}


	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}


}
