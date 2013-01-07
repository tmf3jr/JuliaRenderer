package tmf3jr.images.julia;

import java.text.DecimalFormat;

import tmf3jr.images.BitmapGeneratorComputationMode;
import tmf3jr.images.BitmapGeneratorListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class JuliaRendererActivity extends Activity implements BitmapGeneratorListener,  OnSeekBarChangeListener {
	/** state bundle key definitions */
	protected static final String BUNDLEKEY_COMP_MODE_RS = "CompModeRS";
	/** Julia fractal surface view */
	private JuliaRendererSurfaceView surfaceView;
    
    //functionality implementation --------------------------------------------
    private void drawJulia() {
		JuliaBitmapGenerator generator = this.surfaceView.getGenerator();
		//return if previous generation is still running
		if (generator.isGenerating()) {
			return;
		}
		//generate and draw image
    	SeekBar seekBarReal = (SeekBar)this.findViewById(R.id.seekBar_realNumber);
    	SeekBar seekBarImaginary = (SeekBar)this.findViewById(R.id.seekBar_imaginaryNumber);
		double cx = generator.getLeft() + generator.getWidth() * seekBarReal.getProgress() / seekBarReal.getMax();
		double cy = generator.getBottom() + generator.getHeight() * seekBarImaginary.getProgress() / seekBarImaginary.getMax();
		generator.setCx(cx);
		generator.setCy(cy);
    	this.surfaceView.draw();
    }

    private void restoreComputationMode(Bundle savedInstanceState) {
        boolean compModeRS = false;
        if (savedInstanceState != null) {
        	compModeRS = savedInstanceState.getBoolean(BUNDLEKEY_COMP_MODE_RS);
        }
        if (compModeRS) {
        	this.surfaceView.setComputationMode(BitmapGeneratorComputationMode.RENDER_SCRIPT);
            RadioButton radioButtonCompRS = (RadioButton)this.findViewById(R.id.radioButton_compRS);
            radioButtonCompRS.setChecked(true);
        }else{
        	this.surfaceView.setComputationMode(BitmapGeneratorComputationMode.CPU);
            RadioButton radioButtonCompCPU = (RadioButton)this.findViewById(R.id.radioButton_compCPU);        	
            radioButtonCompCPU.setChecked(true);
        }
    }
 
    //Activity life cycle override --------------------------------------------
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//create view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //initialize SurfaceView
        this.surfaceView = (JuliaRendererSurfaceView)this.findViewById(R.id.surfaceview);
        this.surfaceView.setListener(this);
        //set event listeners
        ((SeekBar)this.findViewById(R.id.seekBar_realNumber)).setOnSeekBarChangeListener(this);
        ((SeekBar)this.findViewById(R.id.seekBar_imaginaryNumber)).setOnSeekBarChangeListener(this);
        //reflect current status
        this.restoreComputationMode(savedInstanceState);
    }
        
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	//save computation mode
    	boolean compModeRS = false;
        RadioButton radioButtonCompRS = (RadioButton)this.findViewById(R.id.radioButton_compRS);
        if (radioButtonCompRS.isChecked()) {
        	compModeRS = true;
        }
        outState.putBoolean(BUNDLEKEY_COMP_MODE_RS, compModeRS);
        //invoke super class
    	super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = false;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		result = true;
		return result;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = false;
		switch (item.getItemId()) {
		case R.id.menuitem_settings:
			Toast message = Toast.makeText(this, R.string.not_implemented, Toast.LENGTH_SHORT);
			message.show();
			result = true;
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}
		return result;
    }
    
    //View event handler ------------------------------------------------------
    public void onComputationModeSelected(View v) {
    	switch (v.getId()) {
    	case R.id.radioButton_compCPU:
    		this.surfaceView.setComputationMode(BitmapGeneratorComputationMode.CPU);
    		break;
    	case R.id.radioButton_compRS:
    		this.surfaceView.setComputationMode(BitmapGeneratorComputationMode.RENDER_SCRIPT);
    		break;
    	}
    }
    
	public void onClickDraw(View v) {
		try {
			//draw
			switch (v.getId()) {
			case R.id.button_draw:
				this.drawJulia();
				break;
			}
		}catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "Exception at UI event", e);
		}
	}
	
    //BitmapGeneratorListener implementation ----------------------------------
    public void generateStarted() {
    	final ProgressBar renderingProgress = (ProgressBar)this.findViewById(R.id.progressBar_rendering);
    	renderingProgress.post(new Runnable() {
			public void run() {
				renderingProgress.setVisibility(View.VISIBLE);
			}
		});
    }
	
	public void generateCompleted(long elapsedTime) {
		final ProgressBar renderingProgress = (ProgressBar)this.findViewById(R.id.progressBar_rendering);
    	renderingProgress.post(new Runnable() {
			public void run() {
				renderingProgress.setVisibility(View.INVISIBLE);
			}
		});

		final long displayTime = elapsedTime;
    	final TextView editTtextErapsedDtime = (TextView)this.findViewById(R.id.textView_elapsedTime);
    	editTtextErapsedDtime.post(new Runnable() {
			public void run() {
				editTtextErapsedDtime.setText(Long.toString(displayTime));
			}
		});
	}


	//OnSeekBarChangeListener implementation ----------------------------------
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		//get generator
		JuliaBitmapGenerator generator = this.surfaceView.getGenerator();
		//get current seek bar value
		DecimalFormat formatter = new DecimalFormat("0.0000");
		switch (seekBar.getId()) {
		case R.id.seekBar_realNumber:
			double cx = generator.getLeft() + generator.getWidth() * progress / seekBar.getMax();
			String valueCx = formatter.format(cx);
			((TextView)this.findViewById(R.id.textView_realValue)).setText(valueCx);
			break;
		case R.id.seekBar_imaginaryNumber:
			double cy = generator.getBottom() + generator.getHeight() * progress / seekBar.getMax();
			String valueCy = formatter.format(cy);
			((TextView)this.findViewById(R.id.textView_imaginaryValue)).setText(valueCy);
			break;
		}
		this.drawJulia();
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}