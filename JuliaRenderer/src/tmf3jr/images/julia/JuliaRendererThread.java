package tmf3jr.images.julia;

import tmf3jr.images.BitmapGeneratorComputationMode;
import tmf3jr.images.BitmapGeneratorListener;
import tmf3jr.images.julia.rs.RenderScriptJuliaBitmapGenerator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;

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
	 * @param holder
	 * @param generator
	 */
	public JuliaRendererThread(SurfaceHolder holder) {
		this.holder = holder;
		this.running = true;
		this.setComputationMode(BitmapGeneratorComputationMode.CPU);
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
						this.generator.setScreenWidth(canvas.getWidth());
						this.generator.setScreenHeight(canvas.getHeight());
						Bitmap bitmap = this.generator.generate();
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
	
	public void setGenerator(JuliaBitmapGenerator generator) {
		this.generator = generator;
	}
	
	public BitmapGeneratorComputationMode getComputationMode() {
		return computationMode;
	}

	public void setComputationMode(BitmapGeneratorComputationMode computationMode) {
		JuliaBitmapGenerator generator;
		switch (computationMode) {
		case RENDER_SCRIPT:
			generator = new RenderScriptJuliaBitmapGenerator(this.context);
			break;
		case CPU:
		default:
			generator = new MultiThreadedJuliaBitmapGenerator();
		}
		this.computationMode = computationMode;
		this.generator = generator;
		this.generator.setListener(this.listener);
	}


	public BitmapGeneratorListener getListener() {
		return listener;
	}

	public void setListener(BitmapGeneratorListener listener) {
		this.listener = listener;
		if (this.generator != null) {
			this.generator.setListener(listener);
		}
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public SurfaceHolder getHolder() {
		return holder;
	}

	public void setHolder(SurfaceHolder holder) {
		this.holder = holder;
	}

}
