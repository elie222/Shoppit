package il.ac.huji.shoppit;

import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * The activity asking the user to log in or sign up.
 */
public class LoginActivity extends ActionBarActivity {

	protected static final String TAG = "LOGIN_ACTIVITY";

	private Button logInButton;
	private Button facebookLogInButton;
	private Button signUpButton;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		logInButton = (Button) findViewById(R.id.logInBtn);
		logInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLogInButtonClicked();
			}
		});

		facebookLogInButton = (Button) findViewById(R.id.facebookLoginButton);
		facebookLogInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFacebookLogInButtonClicked();
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
					logInSignUpSuccess();
				} else {
					// Signup failed. Look at the ParseException to see what happened.
					Toast.makeText(getApplicationContext(), fixExceptionMessage(e), Toast.LENGTH_LONG).show();
				}

			}
		});

	}

	private void onFacebookLogInButtonClicked() {
		// TODO - add cancel button?
		LoginActivity.this.progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_relationships", "user_birthday", "user_location");

		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(TAG, "User signed up and logged in through Facebook!");
					logInSignUpSuccess();
				} else {
					Log.d(TAG, "User logged in through Facebook!");
					logInSignUpSuccess();
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
					logInSignUpSuccess();
				} else {
					// Sign up didn't succeed. Look at the ParseException to figure out what went wrong
					Toast.makeText(getApplicationContext(), fixExceptionMessage(e), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void logInSignUpSuccess() {
		setResult(RESULT_OK, getIntent());
		Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
		finish();
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

}
