package il.ac.huji.shoppit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivityNew extends ActionBarActivity {
	
	private Button logInButton;
	private Button signUpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_new);
		
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli"); 
		
		ParseAnalytics.trackAppOpened(getIntent());
		
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}
	
	private void onLogInButtonClicked() {
		
		EditText usernameEdt = (EditText) findViewById(R.id.logInUsernameEdt);
		EditText passwordEdt = (EditText) findViewById(R.id.logInPasswordEdt);

		String username = usernameEdt.getText().toString();
		String password = passwordEdt.getText().toString();
		
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			  public void done(ParseUser user, ParseException e) {
			    if (user != null) {
			      // Hooray! The user is logged in.
			    } else {
			      // Signup failed. Look at the ParseException to see what happened.
			    }
			  }
			});
	}
	
	private void onSignUpButtonClicked() {
		
		EditText usernameEdt = (EditText) findViewById(R.id.signUpUsernameEdt);
		EditText passwordEdt = (EditText) findViewById(R.id.signUpPasswordEdt);

		String username = usernameEdt.getText().toString();
		String password = passwordEdt.getText().toString();
		
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
		    } else {
		      // Sign up didn't succeed. Look at the ParseException
		      // to figure out what went wrong
		    }
		  }
		});
	}
	
}
