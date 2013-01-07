/*
 * Copyright (C) 2011-2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file is auto-generated. DO NOT MODIFY!
 * The source Renderscript file: C:\Tools\Android\workspace\JuliaRenderer\src\tmf3jr\images\julia\rs\julia.rs
 */
package tmf3jr.images.julia.rs;

import android.renderscript.*;
import android.content.res.Resources;

/**
 * @hide
 */
public class ScriptC_julia extends ScriptC {
    // Constructor
    public  ScriptC_julia(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        __U32 = Element.U32(rs);
    }

    private Element __U32;
    private final static int mExportVarIdx_script = 0;
    private Script mExportVar_script;
    public void set_script(Script v) {
        mExportVar_script = v;
        setVar(mExportVarIdx_script, v);
    }

    public Script get_script() {
        return mExportVar_script;
    }

    private final static int mExportVarIdx_param = 1;
    private ScriptField_JuliaComputationParam.Item mExportVar_param;
    public void set_param(ScriptField_JuliaComputationParam.Item v) {
        mExportVar_param = v;
        FieldPacker fp = new FieldPacker(24);
        fp.addF64(v.c.real);
        fp.addF64(v.c.imaginary);
        fp.addI32(v.depth);
        fp.skip(4);
        setVar(mExportVarIdx_param, fp);
    }

    public ScriptField_JuliaComputationParam.Item get_param() {
        return mExportVar_param;
    }

    private final static int mExportVarIdx_canvas = 2;
    private ScriptField_GeneratorCanvas.Item mExportVar_canvas;
    public void set_canvas(ScriptField_GeneratorCanvas.Item v) {
        mExportVar_canvas = v;
        FieldPacker fp = new FieldPacker(40);
        fp.addI32(v.screenWidth);
        fp.addI32(v.screenHeight);
        fp.addF64(v.bottom);
        fp.addF64(v.left);
        fp.addF64(v.width);
        fp.addF64(v.height);
        setVar(mExportVarIdx_canvas, fp);
    }

    public ScriptField_GeneratorCanvas.Item get_canvas() {
        return mExportVar_canvas;
    }

    private final static int mExportVarIdx_colorTable = 3;
    private Allocation mExportVar_colorTable;
    public void set_colorTable(Allocation v) {
        mExportVar_colorTable = v;
        setVar(mExportVarIdx_colorTable, v);
    }

    public Allocation get_colorTable() {
        return mExportVar_colorTable;
    }

    private final static int mExportForEachIdx_root = 0;
    public void forEach_root(Allocation aout) {
        // check aout
        if (!aout.getType().getElement().isCompatible(__U32)) {
            throw new RSRuntimeException("Type mismatch with U32!");
        }
        forEach(mExportForEachIdx_root, null, aout, null);
    }

}

