package tmf3jr.android.images.julia;

import tmf3jr.android.images.BitmapGeneratorComputationMode;
import tmf3jr.android.images.BitmapGeneratorListener;
import tmf3jr.android.images.GalacticColorTableGenerator;
import tmf3jr.android.images.julia.rs.RenderScriptJuliaBitmapGenerator;
import tmf3jr.android.images.julia.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Julia fractal renderer surface thread.
 */
public class JuliaRendererThread extends Thread {
	/** bitmap generator instance */
	private JuliaBitmapGenerator generator;
	
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
		this.createGenerator(mode, listener);
	}

	/**
	 * Creates a new surface renderer thread.
	 * @param surfaceView
	 */
	public JuliaRendererThread(SurfaceView surfaceView) {
		this(surfaceView, BitmapGeneratorComputationMode.RENDER_SCRIPT, null);
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
				Log.d(this.getClass().getSimpleName(), "Drawing started");
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
				Log.d(this.getClass().getSimpleName(), "Drawing completed");
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
	private void createGenerator(BitmapGeneratorComputationMode mode, BitmapGeneratorListener listener) {
		switch (mode) {
		case RENDER_SCRIPT:
			this.generator = new RenderScriptJuliaBitmapGenerator(context);
			break;
		case CPU:
		default:
			int threadCount = MultiThreadedJuliaBitmapGenerator.DEFAULT_THREAD_COUNT;
			String prefKey_numOfCPU = context.getString(R.string.prefkey_numOfCPU);
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			if (prefKey_numOfCPU != null && sharedPref != null) {
				threadCount = Integer.parseInt(sharedPref.getString(prefKey_numOfCPU, Integer.toString(threadCount)));
			}
			this.generator = new MultiThreadedJuliaBitmapGenerator(threadCount);
		}
		this.generator.setListener(listener);
		
		
		
		String prefKeyColor = context.getString(R.string.prefkey_color);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		if (prefKeyColor != null && sharedPref != null) {
			String prefColorDefault = context.getString(R.string.pref_color_default);
			String prefColorGalactic = context.getString(R.string.pref_color_galactic);			
			String prefColorValue = sharedPref.getString(prefKeyColor, prefColorDefault);
			if (prefColorValue.equals(prefColorGalactic)) {
				int size = this.generator.getColorTableGenerator().getSize();
				this.generator.setColorTableGenerator(new GalacticColorTableGenerator(size));
			}
		}		
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

}
