package tmf3jr.images.julia;

import tmf3jr.images.BitmapGeneratorComputationMode;
import tmf3jr.images.BitmapGeneratorListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * 
 */
public class JuliaRendererSurfaceView extends SurfaceView  implements SurfaceHolder.Callback {
	/** surface renderer thread */
	private JuliaRendererThread rendererThread;

	//constructors ------------------------------------------------------------	
	/**
	 * Creates a new surface view object.
	 * @param context
	 * @param attrs
	 */
	public JuliaRendererSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//initialize surface view
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);
		//initialize renderer thread
		this.rendererThread = new JuliaRendererThread(holder);
		this.rendererThread.setContext(this.getContext());
		this.rendererThread.start();
		//debug
		Log.d(this.getClass().getSimpleName(), "Created a new rederer thread");
	}
	
	//public methods ----------------------------------------------------------
	/**
	 * Draw surface
	 */
	public void draw() {
		synchronized(this.rendererThread) {
			this.rendererThread.notify();
		}
	}

	//access methods ----------------------------------------------------------
	public JuliaBitmapGenerator getGenerator() {
		return this.rendererThread.getGenerator();
	}
	
	public void setListener(BitmapGeneratorListener listener) {
		this.rendererThread.setListener(listener);
	}
	
	public void setComputationMode(BitmapGeneratorComputationMode computationMode) {
		this.rendererThread.setComputationMode(computationMode);
	}

	public BitmapGeneratorComputationMode getComputationMode() {
		return this.rendererThread.getComputationMode();
	}

	//SurfaceHolder.Callback implementation -----------------------------------
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (this.rendererThread != null) {
			this.rendererThread.setRunning(false);
			this.rendererThread.interrupt();
			this.rendererThread = null;
			//debug
			Log.d(this.getClass().getSimpleName(), "Destroyed a rederer thread");
		}
	}

}
