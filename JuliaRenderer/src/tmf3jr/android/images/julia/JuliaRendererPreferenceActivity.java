package tmf3jr.android.images.julia;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class JuliaRendererPreferenceActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set PreferenceFragment
		JuliaRendererPreferenceFragment prefFragment = new JuliaRendererPreferenceFragment();
		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		transaction.replace(android.R.id.content, prefFragment);
		// Commit the transaction
		transaction.commit();

	}
}
