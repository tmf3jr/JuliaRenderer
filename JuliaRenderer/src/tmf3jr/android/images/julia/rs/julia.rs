#pragma rs java_package_name(tmf3jr.android.images.julia.rs)
#pragma version(1)

#include "rs_core.rsh"
#include "rs_debug.rsh"
#include "julia.rsh"

//script memory 
rs_script script;
JuliaComputationParam_t param;
GeneratorCanvas_t canvas;
rs_allocation colorTable;

/**
 * Initialization
 */
void init() {
	rsDebug("Initializing render script", rsUptimeMillis());
}

/**
 * Compute a point of Julia fractal set.
 * This function can be called simultaneously.
 */
void root(uint32_t* v_out, uint32_t count) {
	//determine position of this pixel
	int row = count / canvas.screenWidth;
	int col = count % canvas.screenHeight;
	//map each pixel and coordinate
	double widthGap = canvas.width / canvas.screenWidth;
	double heightGap = canvas.height / canvas.screenHeight;
	double x = canvas.left + widthGap * col;
	double y = canvas.bottom + heightGap * row;
	//compute Julia fractal
	int depth;
	for (depth = 0; depth < param.depth-1; depth++) {
		double x2 = x * x;
		double y2 = y * y;
		if (x2 + y2 >= COORD_DISTANCE_MAX) {
			break;
		}
		double nx = x2 - y2 + param.c.real;
		double ny = 2*x*y + param.c.imaginary;
		x = nx;
		y = ny;
	}
	//map computation result with color table
	*v_out = *(uint32_t*)rsGetElementAt(colorTable, depth);
}
