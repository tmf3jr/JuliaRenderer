package tmf3jr.android.images;

import android.graphics.Bitmap;

/**
 * This class generates a color table. Color table is cached in an object.<br/>
 * This class is abstract, so sub classes have to override
 * {@link ColorTableGenerator#generateColorTable(int[])} method.
 * 
 */
public abstract class ColorTableGenerator {
	private static final int BITMAP_SIDE_LENGTH = 1;
	
	/** size of color table */
	private int size;
	
	/** color table buffer*/
	private int[] colorTable;
	
	//constructors ------------------------------------------------------------
	/**
	 * Creates a ColorTableGenerator with specified table size.
	 * @param size
	 */
	public ColorTableGenerator(int size) {
		this.setSize(size);
	}
	
	//public methods ----------------------------------------------------------	
	/**
	 * Generate a new color table 
	 */
	public void refresh() {
		this.colorTable = new int[this.size];
		this.generateColorTable(colorTable);
	}
	
	/**
	 * Returns bitmap of color table
	 * @param horizontal width is same length of color table size, otherwise height is same as size
	 * @return bitmap of color table
	 */
	public Bitmap createBitmap(boolean horizontal) {
		int width;
		int height;
		if (horizontal) {
			width = this.size;
			height = BITMAP_SIDE_LENGTH;
		}else{
			width = BITMAP_SIDE_LENGTH;
			height = this.size;			
		}
		Bitmap result = Bitmap.createBitmap(this.colorTable, width, height, Bitmap.Config.ARGB_8888);
		return result;
	}

	//abstract methods --------------------------------------------------------
	/**
	 * Generate a new color table
	 */
	protected abstract void generateColorTable(int[] colorTable);
	
	//access methods ----------------------------------------------------------
	/**
	 * Returns color table
	 * @return color table
	 */
	public int[] getColorTable() {
		return this.colorTable;
	}

	/**
	 * Returns size of color table
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Set size of color table. Refresh() is invoked internally.
	 * @param size size of color table, must be positive integer
	 */
	public void setSize(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("size must be positive integer");
		}
		this.size = size;
		this.refresh();
	}
	
}
