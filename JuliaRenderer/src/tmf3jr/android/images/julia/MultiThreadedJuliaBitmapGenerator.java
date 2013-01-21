package tmf3jr.android.images.julia;

import android.util.Log;
import tmf3jr.android.images.ColorTableGenerator;

/**
 * Julia fractal generator. This class computes Julia fractal by multiple threads.
 */
public class MultiThreadedJuliaBitmapGenerator extends JuliaBitmapGenerator {
	/** Default number of threads */
	public static int DEFAULT_THREAD_COUNT = 1;
	
	/** actual number of threads */
	private int threadCount;
	
	static {
		DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
	}
	
	//constructors ------------------------------------------------------------
	/**
	 * Creates a new generator object with all parameters.
	 * @param left
	 * @param bottom
	 * @param width
	 * @param height
	 * @param resolutionX
	 * @param resolutionY
	 * @param cx
	 * @param cy
	 * @param colorTableGenerator
	 */
	public MultiThreadedJuliaBitmapGenerator(double left, double bottom, double width, double height,
			int resolutionX, int resolutionY, double cx, double cy,
			ColorTableGenerator colorTableGenerator, int numOfCPUs) {
		super(left, bottom, width, height, resolutionX, resolutionY, cx, cy, colorTableGenerator);
		this.threadCount = numOfCPUs;
	}
	
	/**
	 * Default constructor. parameters must be set before generating a bitmap.
	 */
	public MultiThreadedJuliaBitmapGenerator(int numOfCPUs) {
		super();
		this.threadCount = numOfCPUs;
	}

	//BitmapGenerator implementation ------------------------------------------
	@Override
	protected void generateBitmap(int[] bitmap) {
		//sanity check
		if (bitmap == null || bitmap.length <= 0) {
			throw new IllegalStateException("Invalid bitmap size");
		}
		if (this.getColorTableGenerator() == null) {
			throw new IllegalStateException("Color table generator is not specified");
		}
		if (this.threadCount <= 0) {
			throw new IllegalStateException("Thread count must be positive integer");			
		}
		//create worker threads
		Thread[] workers = new Thread[this.threadCount];
		double bottom = getBottom();	//for thread safety
		double left = getLeft();		//for thread safety
		double height = getHeight();
		double width = getWidth();
		for (int i = 0; i < workers.length; i++) {
			JuliaComputationWorker worker = new JuliaComputationWorker();
			worker.bitmap = bitmap;
			worker.threadCount = workers.length;
			worker.threadNum = i;
			worker.bottom = bottom;
			worker.left = left;
			worker.height = height;
			worker.width = width;
			worker.start();
			workers[i] = worker;
		}
		//wait until workers complete jobs
		for (Thread worker : workers) {
			try {
				worker.join();
			}catch (InterruptedException e) {
				//ignore
				Log.d(this.getClass().getSimpleName(), "Interrupted while waiting workers", e);
			}
		}
	}


	//worker thread -----------------------------------------------------------
	/**
	 * worker thread of Julia set computation
	 */
	private class JuliaComputationWorker extends Thread {
		int threadCount;
		int threadNum;
		int[] bitmap;
		double bottom;
		double left;
		double height;
		double width;

		@Override
		public void run() {
			//determine computation range
			int processLength = getScreenWidth() * getScreenHeight() / this.threadCount;
			int startIndex = processLength * this.threadNum;
			int endIndex = startIndex + processLength;
			//prepare to determine coordinates
			double heightGap = this.height / getScreenHeight();
			double widthGap = this.width / getScreenWidth();
			//debug
			Log.d("julia.thread"+this.threadNum, "processLength=" + processLength);
			Log.d("julia.thread"+this.threadNum, "startIndex=" + startIndex);
			Log.d("julia.thread"+this.threadNum, "endIndex="+endIndex);
			//create bitmap
			for (int i = startIndex; i < endIndex; i++) {
				//convert array index to coordinates
				int row = i / getScreenWidth();
				int col = i % getScreenWidth();
				double x = left + widthGap * col;
				double y = bottom + heightGap * (getScreenHeight() - row);
				//compute Julia set
				int depth;
				double cx = getCx();
				double cy = getCy();
				for (depth = 0; depth < getColorTableGenerator().getSize()-1; depth++) {
					double x2 = x * x;
					double y2 = y * y;
					if (x2 + y2 >= COORD_DISTANCE_MAX) {
						break;
					}
					double nx = x2 - y2 + cx;
					double ny = 2*x*y + cy;
					x = nx;
					y = ny;
				}
				bitmap[i] = getColorTableGenerator().getColorTable()[depth];
			}
		}		
	}
	
}
