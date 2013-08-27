package il.ac.huji.shoppit;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * The activity asking the user to log in or sign up.
 */
public class LoginActivity extends ActionBarActivity {

	private Button logInButton;
	private Button signUpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		//Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli"); 
		//ParseAnalytics.trackAppOpened(getIntent());

		//		Testing to make sure Parse is working.
		//		ParseObject testObject = new ParseObject("TestObject");
		//		testObject.put("foo", "bar");
		//		testObject.saveInBackground();

		logInButton = (Button) findViewById(R.id.logInBtn);
		logInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogInButtonClicked();
			}
		});

		signUpButton = (Button) findViewById(R.id.signUpBtn);
		signUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSignUpButtonClicked();
			}
		});

		((EditText)findViewById(R.id.logInUsernameEdt)).requestFocus();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_activity_actions, menu);
		return true;
	}

	private void onLogInButtonClicked() {

		EditText usernameEdt = (EditText) findViewById(R.id.logInUsernameEdt);
		EditText passwordEdt = (EditText) findViewById(R.id.logInPasswordEdt);

		final String username = usernameEdt.getText().toString();
		final String password = passwordEdt.getText().toString();

		if (username.equals("") || password.equals("")) {
			Toast.makeText(getApplicationContext(), "Please fill username and password", Toast.LENGTH_LONG).show();
			return;
		}

		ParseUser.logInInBackground(username, password, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
				if (user != null) {
					// Hooray! The user is logged in.
					//Save the login data to the device.
					writeLoginData(username, password);
					setResult(RESULT_OK, getIntent());
					finish();
					Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
				} else {
					// Signup failed. Look at the ParseException to see what happened.
					Toast.makeText(getApplicationContext(), fixExceptionMessage(e), Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	private void onSignUpButtonClicked() {

		EditText usernameEdt = (EditText) findViewById(R.id.signUpUsernameEdt);
		EditText passwordEdt = (EditText) findViewById(R.id.signUpPasswordEdt);

		final String username = usernameEdt.getText().toString();
		final String password = passwordEdt.getText().toString();

		if (username.equals("") || password.equals("")) {
			Toast.makeText(getApplicationContext(), "Please fill username and password", Toast.LENGTH_LONG).show();
			return;
		}

		ParseUser user = new ParseUser();
		user.setUsername(username);
		user.setPassword(password);
		//		user.setEmail("email@example.com");

		// other fields can be set just like with ParseObject
		//		user.put("phone", "650-253-0000");

		user.signUpInBackground(new SignUpCallback() {
			public void done(ParseException e) {
				if (e == null) {
					// Hooray! Let them use the app now.
					writeLoginData(username, password);
					setResult(RESULT_OK, getIntent());
					finish();
					Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
				} else {
					// Sign up didn't succeed. Look at the ParseException to figure out what went wrong
					Toast.makeText(getApplicationContext(), fixExceptionMessage(e), Toast.LENGTH_LONG).show();
				}
			}
		});
	}


	/**
	 * Write the login data to the device.
	 * @param username
	 * @param password
	 */
	private void writeLoginData(String username, String password) {
		File loginData = new File(getFilesDir() + "/data/files/", "shoppit.txt");
		PrintWriter writer = null;
		try {
			loginData.getParentFile().mkdirs();
			loginData.createNewFile();
			writer = new PrintWriter(new FileWriter(loginData));
			writer.println(username);
			writer.println(password);
		} catch (Exception e) {
			try {
				writer.close();
			} catch (Exception e1) {}
			loginData.delete();
			return;
		}
		GeneralInfo.logged = true;
		GeneralInfo.username = username;
		writer.close();
	}


	/**
	 * Make an exception message more readable.
	 * Remove the "com.parse.parseException: " prefix and make it start with upper case.
	 * @param e
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private String fixExceptionMessage(ParseException e) {
		String message = e.toString();
		message = message.substring("com.parse.parseException: ".length());
		message = message.substring(0,1).toUpperCase() + message.substring(1);
		return message;
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
