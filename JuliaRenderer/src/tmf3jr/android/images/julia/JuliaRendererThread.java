package tmf3jr.android.images.julia;

import tmf3jr.android.images.BitmapGeneratorComputationMode;
import tmf3jr.android.images.BitmapGeneratorListener;
import tmf3jr.android.images.julia.rs.RenderScriptJuliaBitmapGenerator;
import tmf3jr.android.images.julia.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Julia fractal renderer surface thread.
 */
public class JuliaRendererThread extends Thread {
	/** bitmap generator's computation mode */
	private BitmapGeneratorComputationMode computationMode;
	/** bitmap generator instance */
	private JuliaBitmapGenerator generator;
	/** bitmap generator listener */
	private BitmapGeneratorListener listener;
	
	/** Android context */
	private Context context;
	/** UI surface holder */
	private SurfaceHolder holder;
	/** worker thread state */
	private boolean running;

	//constructors ------------------------------------------------------------
	/**
	 * Creates a new surface renderer thread.
	 * @param surfaceView
	 * @param mode
	 * @param listener
	 */
	public JuliaRendererThread(SurfaceView surfaceView, BitmapGeneratorComputationMode mode,
			BitmapGeneratorListener listener) {
		this.context = surfaceView.getContext();
		this.holder = surfaceView.getHolder();
		this.running = true;
		this.computationMode = mode;
		this.listener = listener;
		this.createGenerator();
	}

	/**
	 * Creates a new surface renderer thread.
	 * @param surfaceView
	 */
	public JuliaRendererThread(SurfaceView surfaceView) {
		this(surfaceView, BitmapGeneratorComputationMode.CPU, null);
	}
	
	//Thread implementation ---------------------------------------------------
	@Override
	public void run() {
		while (this.running) {
			try {
				//wait until notified
				synchronized (this) {
					this.wait();
				}
				//sanity check
				if (this.generator == null) {
					throw new IllegalStateException("BitmapGenerator must not be null");
				}
				//draw canvas
				Canvas canvas = null;
				try {
					canvas = this.holder.lockCanvas();
					if (canvas != null) {
						generator.setScreenWidth(canvas.getWidth());
						generator.setScreenHeight(canvas.getHeight());
						Bitmap bitmap = generator.generate();
						Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
						canvas.drawBitmap(bitmap, rect, rect, null);
					}
				}finally{
					if (canvas != null) {
						this.holder.unlockCanvasAndPost(canvas);
					}
				}
			} catch (InterruptedException e) {
				this.running = false;
			}
		}
	}
	
	//private methods ---------------------------------------------------------
	/**
	 * Create a new bitmap generator.
	 * Type of generator is determined by computation mode.
	 */
	private void createGenerator() {
		switch (this.computationMode) {
		case RENDER_SCRIPT:
			this.generator = new RenderScriptJuliaBitmapGenerator(context);
			break;
		case CPU:
		default:
			MultiThreadedJuliaBitmapGenerator cpuGenerator = new MultiThreadedJuliaBitmapGenerator();
			int threadCount = MultiThreadedJuliaBitmapGenerator.DEFAULT_THREAD_COUNT;
			String prefKey_numOfCPU = context.getString(R.string.prefkey_numOfCPU);
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			if (prefKey_numOfCPU != null && sharedPref != null) {
				threadCount = Integer.parseInt(sharedPref.getString(prefKey_numOfCPU, Integer.toString(threadCount)));
			}
			cpuGenerator.setThreadCount(threadCount);
			this.generator = cpuGenerator;
		}
		this.generator.setListener(this.listener);
	}

	
	//access methods ----------------------------------------------------------
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return running;
	}

	public JuliaBitmapGenerator getGenerator() {
		return generator;
	}
		
	public BitmapGeneratorComputationMode getComputationMode() {
		return computationMode;
	}

	/**
	 * Set computation mode.
	 * Note that, this method recreates BitmapGenerator instance.
	 * @param computationMode
	 */
	public void setComputationMode(BitmapGeneratorComputationMode computationMode) {
		this.computationMode = computationMode;
		this.createGenerator();
	}

	public BitmapGeneratorListener getListener() {
		return listener;
	}

	public void setListener(BitmapGeneratorListener listener) {
		this.listener = listener;
		this.generator.setListener(listener);
	}


}
