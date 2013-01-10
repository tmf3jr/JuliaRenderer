package tmf3jr.android.images.julia;

import tmf3jr.android.images.BitmapGeneratorComputationMode;
import tmf3jr.android.images.BitmapGeneratorListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/**
 * 
 */
public class JuliaRendererSurfaceView extends SurfaceView  implements SurfaceHolder.Callback {
	/** surface renderer thread. NOTE: this thread may be null before surface is not created */
	private JuliaRendererThread rendererThread;
	/** bitmap generation event listener */
	private BitmapGeneratorListener listener;
	/** bitmap generation mode */
	private BitmapGeneratorComputationMode computationMode;
	
	//constructors ------------------------------------------------------------	
	/**
	 * Creates a new surface view object.
	 * @param context
	 * @param attrs
	 */
	public JuliaRendererSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//initialize surface holder callback
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);		
	}
	
	//public methods ----------------------------------------------------------
	/**
	 * Draw surface.
	 * This method just notify rendering thread to draw.
	 */
	public void draw() {
		if (this.rendererThread != null) {
			synchronized(this.rendererThread) {
				this.rendererThread.notify();
			}
		}
	}
	
	/**
	 * Returns true is rendering thread is working, otherwise false
	 * @return
	 */
	public boolean isRendering() {
		boolean rendering = false;
		if (this.rendererThread != null) {
			rendering = this.rendererThread.getGenerator().isGenerating();
		}
		return rendering;
	}

	//private methods ---------------------------------------------------------
	/**
	 * Create a new rendering thread
	 */
	private void createSurfaceViewThread() {
		this.rendererThread = new JuliaRendererThread(this, this.computationMode, this.listener);
		this.rendererThread.start();
		//debug
		Log.d(this.getClass().getSimpleName(), "Created a new rederer thread");
	}

	private void destroySurfaceViewThread() {
		if (this.rendererThread != null) {
			this.rendererThread.setRunning(false);
			this.rendererThread.interrupt();
			this.rendererThread = null;
			//debug
			Log.d(this.getClass().getSimpleName(), "Destroyed a rederer thread");
		}
	}
	
	//access methods ----------------------------------------------------------
	/**
	 * Returns bitmap generator.
	 * @return bitmap generator, or null if surface is not ready
	 */
	public JuliaBitmapGenerator getGenerator() {
		JuliaBitmapGenerator generator = null;
		if (this.rendererThread != null) {
			generator = this.rendererThread.getGenerator();
		}
		return generator;
	}
	
	public void setListener(BitmapGeneratorListener listener) {
		this.listener = listener;
		if (this.rendererThread != null) {
			this.destroySurfaceViewThread();
			this.createSurfaceViewThread();
		}
	}
	
	public BitmapGeneratorListener getListener() {
		return listener;
	}
	
	public void setComputationMode(BitmapGeneratorComputationMode computationMode) {
		this.computationMode = computationMode;
		if (this.rendererThread != null) {
			this.destroySurfaceViewThread();
			this.createSurfaceViewThread();
		}
	}

	public BitmapGeneratorComputationMode getComputationMode() {
		return this.computationMode;
	}
	
	
	//SurfaceHolder.Callback implementation -----------------------------------
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		//create rendering thread
		this.createSurfaceViewThread();
        //Notify to draw to rendering thread
        this.draw();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		//destroy rendering thread
		this.destroySurfaceViewThread();
	}

}
