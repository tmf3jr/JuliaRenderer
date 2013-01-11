package tmf3jr.android.images.julia;

import java.text.MessageFormat;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class JuliaRendererSurfaceFragment extends Fragment implements SurfaceScreenChangeListener {	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View content = inflater.inflate(R.layout.surfaceview_fragment, container);
		JuliaRendererSurfaceView surfaceView = (JuliaRendererSurfaceView) content.findViewById(R.id.surfaceview);
		surfaceView.setScreenListener(this);
		return content;
	}	
	
	/**
	 * Updates displaying coordinates
	 */
	public void updateCoods() {
		JuliaRendererSurfaceView surfaceView = (JuliaRendererSurfaceView) getView().findViewById(R.id.surfaceview);
		JuliaBitmapGenerator generator = surfaceView.getGenerator();
		if (generator != null) {
			MessageFormat formatter = new MessageFormat("({0, number, 0.0000}, {1, number, 0.0000})");
			//top left
			TextView topLeft = (TextView) getView().findViewById(R.id.textView_coordTopLeft);
			Object[] topLeftValue = {generator.getLeft(), generator.getBottom() + generator.getHeight()};
			topLeft.setText(formatter.format(topLeftValue));
			//bottom left
			TextView bottomLeft = (TextView) getView().findViewById(R.id.textView_coordBottomLeft);
			Object[] bottomLeftValue = {generator.getLeft(), generator.getBottom()};
			bottomLeft.setText(formatter.format(bottomLeftValue));
			//bottom right
			TextView bottomRight = (TextView) getView().findViewById(R.id.textView_coordBottomRight);
			Object[] bottomRightValue = {generator.getLeft() + generator.getWidth(), generator.getBottom()};
			bottomRight.setText(formatter.format(bottomRightValue));
		}
	}

	//SurfaceScreenChangeListener implementation ------------------------------
	public void onScreenChanged() {
		this.updateCoods();
	}
}
