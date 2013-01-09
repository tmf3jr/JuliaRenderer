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
	private BitmapGeneratorComputationMode compurationMode;
	
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

	//private methods ---------------------------------------------------------
	/**
	 * Create a new rendering thread
	 */
	private void createSurfaceViewThread() {
		this.rendererThread = new JuliaRendererThread(this, this.compurationMode, this.listener);
		this.rendererThread.start();
		//debug
		Log.d(this.getClass().getSimpleName(), "Created a new rederer thread");
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
			this.rendererThread.setListener(listener);
		}
	}
	
	public void setComputationMode(BitmapGeneratorComputationMode computationMode) {
		this.compurationMode = computationMode;
		if (this.rendererThread != null) {
			this.rendererThread.setComputationMode(computationMode);
		}
	}

	public BitmapGeneratorComputationMode getComputationMode() {
		return this.compurationMode;
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
		if (this.rendererThread != null) {
			this.rendererThread.setRunning(false);
			this.rendererThread.interrupt();
			this.rendererThread = null;
			//debug
			Log.d(this.getClass().getSimpleName(), "Destroyed a rederer thread");
		}
	}

}
