package il.ac.huji.shoppit;

//random comment

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import android.os.Bundle;
//import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
import android.widget.ListView;
//import android.widget.Spinner;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//		ParseObject.registerSubclass(Item.class);
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli");

		//Check if the user is logged in, connect to parse if so.
		checkIfLoggedIn();

		//creating some objects to test with and saving them to the server.
		//		for (int i=0; i<20; i++) {
		//			ParseObject testObject = new ParseObject("TestObject");
		//			testObject.put("name", "bar " + i);
		//			testObject.saveInBackground();
		//		}	

		//Fill the list of nearby items.
		//TODO - not working yet
		//HomeListAdapter adapter = new HomeListAdapter(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
		ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
			public ParseQuery<ParseObject> create() {
				// Here we can configure a ParseQuery to our heart's desire.
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TestObject");
				// you can do a more advanced query as follows:
				// can also set an order for the items.
				//				      query.whereContainedIn("genre", Arrays.asList({ "Punk", "Metal" }));
				//				      query.whereGreaterThanOrEqualTo("memberCount", 4);
				//				      query.orderByDescending("albumsSoldCount");
				return query;
			}
		});

		//		ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, "TestObject");
		adapter.setTextKey("name");
		adapter.setImageKey("photo");
		ListView listView = (ListView) findViewById(R.id.homeListView);
		listView.setAdapter(adapter);

		//Create the sorter.
//		Spinner spinner = (Spinner) findViewById(R.id.sorter);
//		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
//				this, R.array.sort_options, android.R.layout.simple_spinner_item);
//		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner.setAdapter(spinnerAdapter);
//		spinner.setOnItemSelectedListener(new Sorter());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parse_table, menu);
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}


	private void checkIfLoggedIn() {

		File loginData = new File(this.getFilesDir() + "/data/files/", "shoppit.txt");

		if (loginData.exists()) { //User is logged in.
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(loginData));
				final String username = reader.readLine(),
						password = reader.readLine();

				ParseUser.logInInBackground(username, password, new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						
						if (e == null && user != null) {
//							loginSuccessful();
							GeneralInfo.logged = true;
							GeneralInfo.username = username;
						} else if (user == null) {
//							usernameOrPasswordIsInvalid();
						} else {
//							somethingWentWrong();
						}
					}
				});

			} catch (Exception e) {}
			try {
				reader.close();
			} catch (Exception e) {}

		}
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
		case R.id.action_userinfo:
			startActivity(new Intent(getBaseContext(), GeneralInfo.logged ? InfoActivity.class : LoginActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	//This class implements sorting nearby items.
//	private class Sorter extends Activity implements OnItemSelectedListener {
//
//		public void onItemSelected(AdapterView<?> parent, View view, 
//				int pos, long id) {
//			// pos: 0 = cheapest, 1 = closest, 2 = newest.
//		}
//
//		public void onNothingSelected(AdapterView<?> parent) {}
//	}


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


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//If user logged in successfully, continue to taking the item picture.
		if (requestCode == 5000 && resultCode == RESULT_OK){
			startActivity(new Intent(getBaseContext(), TakePictureActivity.class));
		}
	}

}
