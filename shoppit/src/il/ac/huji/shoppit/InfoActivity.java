package il.ac.huji.shoppit;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This activity displays the info of the user when logged in.
 */
public class InfoActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		((TextView) findViewById(R.id.textView1)).setText(GeneralInfo.username);

		//Logout listener.
		//Delete the file holding the login data.
		Button logout = (Button) findViewById(R.id.logout);
		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				File loginData = new File(getFilesDir() + "/data/files/", "shoppit.txt");
				loginData.delete();
				GeneralInfo.logged = false;
				finish();
			}
		});
	}


	//Clicking the action bar.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			addItemIfLoggedIn();
			return true;
		case R.id.action_search:
			startActivity(new Intent(getBaseContext(), SearchActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_activity_actions, menu);
		return true;
	}
	
	
	private void addItemIfLoggedIn() {

		//If user is logged in, continue to taking the item picture.
		if (GeneralInfo.logged) {
			startActivity(new Intent(getBaseContext(), TakePictureActivity.class));
			return;
		}
		
		//Else, ask the user to log in.
		Intent intent = new Intent(getBaseContext(), LoginActivity.class);
		startActivityForResult(intent, 5000);
	}

}
