package tmf3jr.android.images;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;



/**
 * Generate Bitmap with color table
 */
public abstract class ColorTableBitmapGenerator extends BitmapGenerator {
	public static final int COLOR_TABLE_HEIGHT_DEFAULT = 24;

	//bitmap generation related fields	
	/** rendering color table */
	private ColorTableGenerator colorTableGenerator;
	/** height of color table on bitmap */
	private int colorTableHeight;
	
	//constructors ------------------------------------------------------------
	/**
	 * Creates a new ColorTableBitmapGenerator
	 * @param left
	 * @param bottom
	 * @param width
	 * @param height
	 * @param resolutionX
	 * @param resolutionY
	 * @param colorTableGenerator
	 */
	public ColorTableBitmapGenerator(double left, double bottom, double width, double height,
			int resolutionX, int resolutionY,
			ColorTableGenerator colorTableGenerator) {
		super(left, bottom, width, height, resolutionX, resolutionY);
		this.setColorTableGenerator(colorTableGenerator);
		this.colorTableHeight = COLOR_TABLE_HEIGHT_DEFAULT;
	}
	
	//override BitmapGenerator ------------------------------------------------
	/* (non-Javadoc)
	 * @see tmf3jr.images.BitmapGenerator#generate()
	 */
	public Bitmap generate() {
		//prepare bitmap and related canvas
		Bitmap bitmap = Bitmap.createBitmap(getScreenWidth(), getScreenHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		//draw color table
		Bitmap colorTable = this.colorTableGenerator.createBitmap(true);
		Matrix matrix = new Matrix();
		RectF srcRect = new RectF(0, 0, colorTable.getWidth(), colorTable.getHeight());
		RectF dstRect = new RectF(0, 0, canvas.getWidth(), this.colorTableHeight);
		matrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.FILL);
		canvas.drawBitmap(colorTable, matrix, null);
		//calculate bitmap area
		int maxScreenWidth = this.getScreenWidth();
		int maxScreenHeight = this.getScreenHeight();
		int actualScreenWidth = this.getScreenWidth();
		int actualScreenHeight = this.getScreenHeight() - this.colorTableHeight;
		double ratioWidth = actualScreenWidth / this.getWidth();
		double ratioHeight = actualScreenHeight / this.getHeight();
		double ratioMin = Math.min(ratioWidth, ratioHeight);
		actualScreenWidth = (int)(this.getWidth() * ratioMin);
		actualScreenHeight = (int)(this.getHeight() * ratioMin);
		this.setScreenWidth(actualScreenWidth);
		this.setScreenHeight(actualScreenHeight);
		//generated bitmap
		Bitmap generatedBitmap = super.generate();
		//restore canvas size
		this.setScreenWidth(maxScreenWidth);
		this.setScreenHeight(maxScreenHeight);
		//draw bitmap
		matrix = new Matrix();
		srcRect = new RectF(0, 0, generatedBitmap.getWidth(), generatedBitmap.getHeight());
		dstRect = new RectF(0, this.colorTableHeight, canvas.getWidth(), canvas.getHeight());
		matrix.setRectToRect(srcRect, dstRect, Matrix.ScaleToFit.CENTER);
		canvas.drawBitmap(generatedBitmap, matrix, null);		
		return bitmap;
	}
	
	
	//access methods ----------------------------------------------------------
	public ColorTableGenerator getColorTableGenerator() {
		return colorTableGenerator;
	}

	public void setColorTableGenerator(ColorTableGenerator colorTableGenerator) {
		this.colorTableGenerator = colorTableGenerator;
	}

	public int getColorTableHeight() {
		return colorTableHeight;
	}

	public void setColorTableHeight(int colorTableHeight) {
		this.colorTableHeight = colorTableHeight;
	}
	
	
	
}
