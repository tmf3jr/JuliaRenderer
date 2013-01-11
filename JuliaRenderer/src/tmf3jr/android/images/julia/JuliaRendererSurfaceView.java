package tmf3jr.android.images.julia;

import tmf3jr.android.images.BitmapGeneratorComputationMode;
import tmf3jr.android.images.BitmapGeneratorListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ScaleGestureDetector.OnScaleGestureListener;


/**
 * 
 */
public class JuliaRendererSurfaceView extends SurfaceView
	implements SurfaceHolder.Callback, OnScaleGestureListener {
	/** surface renderer thread. NOTE: this thread may be null before surface is not created */
	private JuliaRendererThread rendererThread;
	/** bitmap generation event listener */
	private BitmapGeneratorListener listener;
	/** bitmap generation mode */
	private BitmapGeneratorComputationMode computationMode;
	/** zoom scale detector */
	private ScaleGestureDetector scaleDetector;
	/** screen change listenr */
	private SurfaceScreenChangeListener screenListener;
	
	//Julia fractal definition
	/** Cx of the coordinate */
	private double cx;
	/** Cy of the coordinate */
	private double cy;
	
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
		//UI event handlers
		this.scaleDetector = new ScaleGestureDetector(context, this);
	}
	
	//public methods ----------------------------------------------------------
	/**
	 * Draw surface.
	 * This method just notify rendering thread to draw.
	 */
	public void draw() {
		if (this.rendererThread != null) {
			synchronized(this.rendererThread) {
				this.getGenerator().setCx(this.cx);
				this.getGenerator().setCy(this.cy);
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
	protected JuliaBitmapGenerator getGenerator() {
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
	
	/**
	 * @return the screenListener
	 */
	public SurfaceScreenChangeListener getScreenListener() {
		return screenListener;
	}

	/**
	 * @param screenListener the screenListener to set
	 */
	public void setScreenListener(SurfaceScreenChangeListener screenListener) {
		this.screenListener = screenListener;
	}

	public double getCx() {
		return cx;
	}

	public void setCx(double cx) {
		this.cx = cx;
	}

	public double getCy() {
		return cy;
	}

	public void setCy(double cy) {
		this.cy = cy;
	}

	//event handlers ----------------------------------------------------------
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.scaleDetector.onTouchEvent(event);
	}
	
	//SurfaceHolder.Callback implementation -----------------------------------
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		//create rendering thread
		this.createSurfaceViewThread();
		//notify screen changed
		this.screenListener.onScreenChanged();
        //Notify to draw to rendering thread
        this.draw();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		//destroy rendering thread
		this.destroySurfaceViewThread();
	}
	
	//OnScaleGestureListener implementation -----------------------------------
	public boolean onScale(ScaleGestureDetector detector) {
		boolean result = false;
		JuliaBitmapGenerator generator = this.getGenerator();
		if (!this.isRendering()) {
			if (generator != null) {
				// calculate new width and height
				double newWidth = generator.getWidth()
						/ detector.getScaleFactor();
				double newHeight = generator.getHeight()
						/ detector.getScaleFactor();
				if (newWidth > JuliaBitmapGenerator.WIDTH_MAX) {
					newWidth = JuliaBitmapGenerator.WIDTH_MAX;
				}
				if (newHeight > JuliaBitmapGenerator.HEIGHT_MAX) {
					newHeight = JuliaBitmapGenerator.HEIGHT_MAX;
				}
				// calculate new bottom and left
				double newBottom = generator.getBottom()
						+ (generator.getWidth() - newWidth) / 2;
				double newLeft = generator.getLeft()
						+ (generator.getHeight() - newHeight) / 2;
				if (newBottom < JuliaBitmapGenerator.CONST_MIN) {
					newBottom = JuliaBitmapGenerator.CONST_MIN;
				} else if (newBottom > JuliaBitmapGenerator.CONST_MAX) {
					newBottom = JuliaBitmapGenerator.CONST_MAX;
				}
				if (newLeft < JuliaBitmapGenerator.CONST_MIN) {
					newLeft = JuliaBitmapGenerator.CONST_MIN;
				} else if (newBottom > JuliaBitmapGenerator.CONST_MAX) {
					newLeft = JuliaBitmapGenerator.CONST_MAX;
				}
				// update screen size and position
				generator.setWidth(newWidth);
				generator.setHeight(newHeight);
				generator.setBottom(newBottom);
				generator.setLeft(newLeft);
				// notify screen changed
				this.screenListener.onScreenChanged();
				// draw if compute on GPU
				if (this.computationMode == BitmapGeneratorComputationMode.RENDER_SCRIPT) {
					this.draw();
				}
			}
			result = true;
		}
		return result;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		boolean result = false;
		JuliaBitmapGenerator generator = this.getGenerator();
		if (generator != null) {
			result = true;
		}
		return result;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
		this.draw();
	}

}
