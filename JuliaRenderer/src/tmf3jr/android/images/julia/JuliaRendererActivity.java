package tmf3jr.android.images.julia;

import java.text.DecimalFormat;

import tmf3jr.android.images.BitmapGeneratorComputationMode;
import tmf3jr.android.images.BitmapGeneratorListener;
import tmf3jr.android.images.julia.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class JuliaRendererActivity extends Activity implements BitmapGeneratorListener, OnSeekBarChangeListener, OnSharedPreferenceChangeListener {
	/** state bundle key definitions */
	protected static final String BUNDLEKEY_COMP_MODE_RS = "CompModeRS";
	/** Julia fractal surface view */
	private JuliaRendererSurfaceView surfaceView;
    
    //functionality implementation --------------------------------------------
    private void drawJulia() {
    	JuliaBitmapGenerator generator = this.surfaceView.getGenerator();
		//generator must be prepared, and must not be busy
		if (generator != null && !this.surfaceView.isRendering()) {
			//generate and draw image
	    	SeekBar seekBarReal = (SeekBar)this.findViewById(R.id.seekBar_realNumber);
	    	SeekBar seekBarImaginary = (SeekBar)this.findViewById(R.id.seekBar_imaginaryNumber);
	    	double cx = JuliaBitmapGenerator.CONST_MIN + JuliaBitmapGenerator.CONST_MAX * seekBarReal.getProgress() / seekBarReal.getMax();
			double cy = JuliaBitmapGenerator.CONST_MIN + JuliaBitmapGenerator.CONST_MAX * seekBarImaginary.getProgress() / seekBarImaginary.getMax();
			generator.setCx(cx);
			generator.setCy(cy);
	    	this.surfaceView.draw();
		}
    }

    /**
     * Restore computation mode by saved state
     * @param savedInstanceState
     */
    private void restoreComputationMode(Bundle savedInstanceState) {
        boolean compModeRS = false;
        if (savedInstanceState != null) {
        	compModeRS = savedInstanceState.getBoolean(BUNDLEKEY_COMP_MODE_RS);
        }
        if (compModeRS) {
            RadioButton radioButtonCompRS = (RadioButton)this.findViewById(R.id.radioButton_compRS);
            radioButtonCompRS.setChecked(true);
        }else{
            RadioButton radioButtonCompCPU = (RadioButton)this.findViewById(R.id.radioButton_compCPU);        	
            radioButtonCompCPU.setChecked(true);
        }
    }
    
    /**
     * Synchronize computation mode from UI to surface view
     */
    private void syncComputationMode() {
        RadioButton radioButtonCompRS = (RadioButton)this.findViewById(R.id.radioButton_compRS);
        if (radioButtonCompRS.isChecked()) {
        	this.surfaceView.setComputationMode(BitmapGeneratorComputationMode.RENDER_SCRIPT);
        }else{
        	this.surfaceView.setComputationMode(BitmapGeneratorComputationMode.CPU);
        }
    }
 
    //Activity life cycle override --------------------------------------------
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set default preference values
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
    	//create view
        setContentView(R.layout.main);
        //set event listeners
        ((SeekBar)this.findViewById(R.id.seekBar_realNumber)).setOnSeekBarChangeListener(this);
        ((SeekBar)this.findViewById(R.id.seekBar_imaginaryNumber)).setOnSeekBarChangeListener(this);
        //apply current status
        this.restoreComputationMode(savedInstanceState);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	//prepare for rendering
        this.surfaceView = (JuliaRendererSurfaceView)this.findViewById(R.id.surfaceview);
        //set generation mode
        this.syncComputationMode();
        //set bitmap generation listener
        this.surfaceView.setListener(this);
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
			Intent intent = new Intent();
            intent.setClass(this, JuliaRendererPreferenceActivity.class);
            this.startActivity(intent);			
			result = true;
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}
		return result;
    }
    
    
    
    //View event handler ------------------------------------------------------
    public void onComputationModeSelected(View v) {
    	this.syncComputationMode();
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
    public void onGenerationStarted() {
    	//displays progress
    	final ProgressBar renderingProgress = (ProgressBar)this.findViewById(R.id.progressBar_rendering);
    	renderingProgress.post(new Runnable() {
			public void run() {
				renderingProgress.setVisibility(View.VISIBLE);
			}
		});
    }
	
	public void onGenerationCompleted(long elapsedTime) {
		//hide progress
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
		//update text by current seek bar value
		DecimalFormat formatter = new DecimalFormat("0.0000");
		switch (seekBar.getId()) {
		case R.id.seekBar_realNumber:
	    	double cx = JuliaBitmapGenerator.CONST_MIN + JuliaBitmapGenerator.CONST_MAX * seekBar.getProgress() / seekBar.getMax();
			String valueCx = formatter.format(cx);
			((TextView)this.findViewById(R.id.textView_realValue)).setText(valueCx);
			break;
		case R.id.seekBar_imaginaryNumber:
			double cy = JuliaBitmapGenerator.CONST_MIN + JuliaBitmapGenerator.CONST_MAX * seekBar.getProgress() / seekBar.getMax();
			String valueCy = formatter.format(cy);
			((TextView)this.findViewById(R.id.textView_imaginaryValue)).setText(valueCy);
			break;
		}
		//draw image
		this.drawJulia();
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		//do nothing
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		//do nothing
	}

	//OnSharedPreferenceChangeListener implementation -------------------------
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		//update computation mode
		BitmapGeneratorComputationMode mode = this.surfaceView.getComputationMode();
		this.surfaceView.setComputationMode(mode);
	}

}