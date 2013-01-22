package tmf3jr.android.images;

public class GalacticColorTableGenerator extends ColorTableGenerator {

	//constructors ------------------------------------------------------------
	/**
	 * Creates a DefaultColorTableGenerator object
	 * @param size
	 */
	public GalacticColorTableGenerator(int size) {
		super(size);
	}

	
	//override methods --------------------------------------------------------

	@Override
	protected void generateColorTable(int[] colorTable) {
		//Basically, each RGB colors are calculated by the function below
		//d = colorTable.length
		//fn = -128 * cos(c*n - a) + b
		//Red: Ra=0, Rb=128, Rc=PI/d
		//Green: Ga=0, Gb=128, Gc=PI/d
		//Blue: Ba=PI/3, Bb=128, Bc=PI/d
		double d = colorTable.length;
		for (int n = 0; n < colorTable.length; n++) {
			int r = (int) (-128 * Math.cos(Math.PI/d*n) + 128);
			int g = (int) (-128 * Math.cos(Math.PI/d*n) + 128);
			int b = (int) (-128 * Math.cos(Math.PI/d*n + Math.PI/3) + 128);
			r = Math.min(0xFF, r);
			r = Math.max(0x00, r);
			g = Math.min(0xFF, g);
			g = Math.max(0x00, g);
			b = Math.min(0xFF, b);
			b = Math.max(0x00, b);
			colorTable[n] = 0xFF000000 | (r << 16) | (g << 8) | b;
		}
	}

}
