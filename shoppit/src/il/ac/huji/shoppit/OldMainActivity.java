package il.ac.huji.shoppit;

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
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;;

public class OldMainActivity extends ActionBarActivity {

	private ItemAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_old_main);

		ParseObject.registerSubclass(Item.class);
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli");

		//Check if the user is logged in, connect to parse if so.
		checkIfLoggedIn();
		
		ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
			public ParseQuery<Item> create() {
				// Here we can configure a ParseQuery to display
				// only top-rated meals.
				ParseQuery query = new ParseQuery("Item");
				//                query.whereContainedIn("rating", Arrays.asList("5", "4"));
				//                query.orderByDescending("rating");
				return query;
			}
		};

		adapter = new ItemAdapter(this, queryFactory);

		ListView listView = (ListView) findViewById(R.id.homeListView);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);

		// Get the SearchView and set the searchable configuration
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchableActivity.class)));
//		searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

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
			//		case R.id.action_search:
			//			startActivity(new Intent(getBaseContext(), SearchActivity.class));
			//			return true;
			//		case R.id.action_userinfo:
			//			startActivity(new Intent(getBaseContext(), GeneralInfo.logged ? InfoActivity.class : LoginActivity.class));
			//			return true;
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


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		//If user logged in successfully, continue to taking the item picture.
		if (requestCode == 5000 && resultCode == RESULT_OK){
			startActivity(new Intent(getBaseContext(), TakePictureActivity.class));
		}
	}

}
