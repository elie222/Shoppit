package il.ac.huji.shoppit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LoginDialogFragment extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_login_dialog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_dialog, menu);
		return true;
	}

}
