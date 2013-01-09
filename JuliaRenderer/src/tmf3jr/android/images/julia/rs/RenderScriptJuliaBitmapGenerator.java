package tmf3jr.android.images.julia.rs;

import tmf3jr.android.images.julia.JuliaBitmapGenerator;
import tmf3jr.android.images.julia.R;
import tmf3jr.android.images.julia.rs.ScriptC_julia;
import tmf3jr.android.images.julia.rs.ScriptField_GeneratorCanvas;
import tmf3jr.android.images.julia.rs.ScriptField_JuliaComputationParam;
import android.content.Context;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;

public class RenderScriptJuliaBitmapGenerator extends JuliaBitmapGenerator {
	/** RenderScript */
	private RenderScript rs;
	private ScriptC_julia juliaScript;
	private ScriptField_JuliaComputationParam.Item param;
	private ScriptField_GeneratorCanvas.Item canvas;
	
	//constructors ------------------------------------------------------------
	public RenderScriptJuliaBitmapGenerator(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("Context must not be null");
		}
		//prepare renderscript
		this.rs = RenderScript.create(context);
		this.juliaScript = new ScriptC_julia(rs, context.getResources(), R.raw.julia);
		this.param = new ScriptField_JuliaComputationParam.Item();		
		this.canvas = new ScriptField_GeneratorCanvas.Item();
	}
	
	//BitmapGenerator implementation ------------------------------------------
	@Override
	protected void generateBitmap(int[] bitmap) {
		//allocate color table
		int[] colorTable = this.getColorTableGenerator().getColorTable();
		Allocation colorTableAllocation = Allocation.createSized(rs, Element.U32(rs), colorTable.length);
		colorTableAllocation.copyFrom(colorTable);
		//prepare output memory
		int allocationLength = this.getScreenWidth() * this.getScreenHeight();
		Allocation resultMem = Allocation.createSized(rs, Element.U32(rs), allocationLength);
		//prepare parameters
		param.c.real = this.getCx();
		param.c.imaginary = this.getCy();
		param.depth = this.getColorTableGenerator().getSize();
		canvas.bottom = this.getBottom();
		canvas.height = this.getHeight();
		canvas.left = this.getLeft();
		canvas.screenHeight = this.getScreenHeight();
		canvas.screenWidth = this.getScreenWidth();
		canvas.width = this.getWidth();
		juliaScript.set_script(juliaScript);
		juliaScript.set_param(param);
		juliaScript.set_canvas(canvas);
		juliaScript.set_colorTable(colorTableAllocation);
		//invoke render script
		juliaScript.forEach_root(resultMem);
		//get result of render script
		resultMem.copyTo(bitmap);		
	}
	
}
