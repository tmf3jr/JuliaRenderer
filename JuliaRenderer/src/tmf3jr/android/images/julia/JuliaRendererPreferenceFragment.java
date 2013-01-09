package tmf3jr.android.images.julia;

import tmf3jr.android.images.julia.R;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.text.InputType;
import android.widget.EditText;

public class JuliaRendererPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
		this.addPreferencesFromResource(R.xml.preference);
        //restrict EditText for number input
		String prefKeyNumOfCPU = this.getString(R.string.prefkey_numOfCPU);
		EditTextPreference prefNumOfCPU = (EditTextPreference) this.findPreference(prefKeyNumOfCPU);
		EditText editText_NumOfCPU = prefNumOfCPU.getEditText();
		editText_NumOfCPU.setInputType(InputType.TYPE_CLASS_NUMBER);
	}
	
}
