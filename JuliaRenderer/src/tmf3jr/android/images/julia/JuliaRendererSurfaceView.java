package tmf3jr.android.images.julia;

import tmf3jr.android.images.BitmapGeneratorComputationMode;
import tmf3jr.android.images.BitmapGeneratorListener;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ScaleGestureDetector.OnScaleGestureListener;


/**
 * 
 */
public class JuliaRendererSurfaceView extends SurfaceView
	implements SurfaceHolder.Callback, OnScaleGestureListener, OnGestureListener {
	/** surface renderer thread. NOTE: this thread may be null before surface is not created */
	private JuliaRendererThread rendererThread;
	/** bitmap generation event listener */
	private BitmapGeneratorListener listener;
	/** bitmap generation mode */
	private BitmapGeneratorComputationMode computationMode;
	/** scroll detector */
	private GestureDetector gestureDetector;
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
		this.gestureDetector = new GestureDetector(context, this);
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
		return this.gestureDetector.onTouchEvent(event) ||
		       this.scaleDetector.onTouchEvent(event);
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
		if (generator != null) {
			Log.d(this.getClass().getSimpleName(), "Scaling from: (" + generator.getLeft() + "," + generator.getBottom() +")");
			Log.d(this.getClass().getSimpleName(), "Scaling size: (" + generator.getWidth() + "," + generator.getHeight() +")");
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
			Log.d(this.getClass().getSimpleName(), "Scaled size: (" + newWidth + "," + newHeight +")");
			// calculate new bottom and left
			double newLeft = generator.getLeft() + (generator.getWidth() - newWidth) / 2;
			double newBottom = generator.getBottom() + (generator.getHeight() - newHeight) / 2;
			if (newLeft < JuliaBitmapGenerator.CONST_MIN) {
				newLeft = JuliaBitmapGenerator.CONST_MIN;
			} else if (newLeft + newWidth > JuliaBitmapGenerator.CONST_MAX) {
				newLeft = JuliaBitmapGenerator.CONST_MAX - newWidth;
			}
			if (newBottom < JuliaBitmapGenerator.CONST_MIN) {
				newBottom = JuliaBitmapGenerator.CONST_MIN;
			} else if (newBottom + newHeight > JuliaBitmapGenerator.CONST_MAX) {
				newBottom = JuliaBitmapGenerator.CONST_MAX - newHeight;
			}
			Log.d(this.getClass().getSimpleName(), "Scaled to: (" + newLeft + "," + newBottom +")");
			// update screen size and position
			generator.setWidth(newWidth);
			generator.setHeight(newHeight);
			generator.setBottom(newBottom);
			generator.setLeft(newLeft);
			// notify screen changed
			this.screenListener.onScreenChanged();
			// draw
			this.draw();
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

	//OnGestureListener implementation ----------------------------------------
	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		boolean result = false;
		JuliaBitmapGenerator generator = this.getGenerator();
		if (generator != null) {
			double distanceRatioX = distanceX / getWidth();
			double distanceRatioY = distanceY / getHeight();
			// calculate new bottom and left
			double newLeft = generator.getLeft() + generator.getWidth() * distanceRatioX;
			double newBottom = generator.getBottom() - generator.getHeight() * distanceRatioY;
			if (newLeft < JuliaBitmapGenerator.CONST_MIN) {
				newLeft = JuliaBitmapGenerator.CONST_MIN;
			} else if (newLeft + generator.getWidth() > JuliaBitmapGenerator.CONST_MAX) {
				newLeft = JuliaBitmapGenerator.CONST_MAX - generator.getWidth();
			}
			if (newBottom < JuliaBitmapGenerator.CONST_MIN) {
				newBottom = JuliaBitmapGenerator.CONST_MIN;
			} else if (newBottom + generator.getHeight() > JuliaBitmapGenerator.CONST_MAX) {
				newBottom = JuliaBitmapGenerator.CONST_MAX - generator.getHeight();
			}
			// update screen size and position
			generator.setBottom(newBottom);
			generator.setLeft(newLeft);
			// notify screen changed
			this.screenListener.onScreenChanged();
			// draw
			this.draw();				
			result = true;
		}
		return result;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
