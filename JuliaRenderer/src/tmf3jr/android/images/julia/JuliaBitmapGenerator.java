package tmf3jr.android.images.julia;

import tmf3jr.android.images.ColorTableBitmapGenerator;
import tmf3jr.android.images.ColorTableGenerator;
import tmf3jr.android.images.DefaultColorTableGenerator;



/**
 * Generate Bitmap of Julia fractal set
 */
public abstract class JuliaBitmapGenerator extends ColorTableBitmapGenerator {
	public static final double CONST_MAX = 2.0;
	public static final double CONST_MIN = -2.0;
	public static final double CONST_LEN = CONST_MAX - CONST_MIN;
	public static final double CONST_ = -2.0;
	public static final double WIDTH_MAX = 4.0;
	public static final double HEIGHT_MAX = 4.0;
	public static final double COORD_MIN = -2.0;
	public static final double COORD_MAX = 2.0;	
	public static final double COORD_DISTANCE_MAX = 4.0;
	public static final int RESOLUTION_DEFAULT = 400;
	public static final int DEPTH_DEFAULT = 32;
	
	//Julia fractal definition
	/** Cx of the coordinate */
	private double cx;
	/** Cy of the coordinate */
	private double cy;
	

	//constructors ------------------------------------------------------------
	public JuliaBitmapGenerator(double left, double bottom, double width, double height,
			int resolutionX, int resolutionY, double cx, double cy,
			ColorTableGenerator colorTableGenerator) {
		super(left, bottom, width, height, resolutionX, resolutionY, colorTableGenerator);
		this.setCx(cx);
		this.setCy(cy);
	}
	
	public JuliaBitmapGenerator() {
		this(CONST_MIN, CONST_MIN, WIDTH_MAX, HEIGHT_MAX, RESOLUTION_DEFAULT, RESOLUTION_DEFAULT,
				0, 0, new DefaultColorTableGenerator(DEPTH_DEFAULT));
	}
	
	//access methods ----------------------------------------------------------
	public double getCx() {
		return cx;
	}

	public void setCx(double cx) {
		if (cx < CONST_MIN || CONST_MAX < cx) {
			throw new IllegalArgumentException("constant must be between " +
					CONST_MIN + " and " + CONST_MAX + ": actual " + cx);
		}
		this.cx = cx;
	}

	public double getCy() {
		return cy;
	}

	public void setCy(double cy) {
		if (cy < CONST_MIN || CONST_MAX < cy) {
			throw new IllegalArgumentException("constant must be between " +
					CONST_MIN + " and " + CONST_MAX + ": actual " + cy);
		}
		this.cy = cy;
	}

}
