#if !defined(__JULIA_RSH__)
#define __JULIA_RSH__ 1
#include "rs_types.rsh"

#define COORD_DISTANCE_MAX 4.0

typedef struct ImaginaryPoint {
	double real;
	double imaginary;
} ImaginaryPoint_t;

typedef struct GeneratorCanvas {
	int screenWidth;
	int screenHeight;
	double bottom;
	double left;
	double width;
	double height;
} GeneratorCanvas_t;

typedef struct JuliaComputationParam {
	ImaginaryPoint_t c;
	int depth;
} JuliaComputationParam_t;

#endif
