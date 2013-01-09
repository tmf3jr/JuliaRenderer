package tmf3jr.android.images;


/**
 * Simple gradation color table.
 */
public class DefaultColorTableGenerator extends ColorTableGenerator {

	//constructors ------------------------------------------------------------
	/**
	 * Creates a DefaultColorTableGenerator object
	 * @param size
	 */
	public DefaultColorTableGenerator(int size) {
		super(size);
	}

	
	//override methods --------------------------------------------------------
	@Override
	protected void generateColorTable(int[] colorTable) {
		int depth = colorTable.length;
		for (int i = 0; i < depth; i++) {
			byte element = (byte)((0xff * (depth - i) / depth) & 0xff);
			colorTable[i] = 0xff000000 | (element << 16) & 0x00ff0000 | (element << 8) & 0x0000ff00 | 0xff;
		}
	}

}
