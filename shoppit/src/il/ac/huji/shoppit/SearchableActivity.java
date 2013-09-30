package il.ac.huji.shoppit;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchableActivity extends ListActivity {

	private ItemAdapter adapter;

	private String mQueryString;
	private ParseGeoPoint mCurrentLocation;

	private int sortBy = R.id.action_sortby_distance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
		if (appData != null) {
			mCurrentLocation = new ParseGeoPoint(appData.getDouble(MainActivity.LATITUDE_EXTRA),
					appData.getDouble(MainActivity.LONGITUDE_EXTRA));
		}

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mQueryString = intent.getStringExtra(SearchManager.QUERY);

			setTitle("Search results for: \"" + mQueryString + "\"");

			loadItems();

			Toast.makeText(getApplicationContext(), "Loading results sorted by distance", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_map:
			Intent intent = new Intent(getBaseContext(), MapActivity.class);
			intent.putExtra(MapActivity.QUERY_EXTRA, mQueryString);
			startActivity(intent);
			return true;
		case R.id.action_sortby_distance:
			sortBy = R.id.action_sortby_distance;
			loadItems();
			return true;
		case R.id.action_sortby_price:
			sortBy = R.id.action_sortby_price;
			loadItems();
			return true;
		case R.id.action_sortby_likes:
			sortBy = R.id.action_sortby_likes;
			loadItems();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadItems() {

		ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
			public ParseQuery<Item> create() {				

				ParseQuery<Item> query = new ParseQuery<Item>("Item");
				query.whereContains("searchString", mQueryString.toLowerCase());

				switch (sortBy) {
				case R.id.action_sortby_distance:
					query.whereNear("location", mCurrentLocation);
					query.whereWithinMiles("location", mCurrentLocation, 100000);
					Toast.makeText(getApplicationContext(), "Loading results sorted by distance", Toast.LENGTH_SHORT).show();
					break;
				case R.id.action_sortby_price:
					// TODO make sure the item is within a certain distance too, or not?
					// query.whereWithinMiles("location", currentLocation, 10);
					query.orderByAscending("price");
					Toast.makeText(getApplicationContext(), "Loading results sorted by price", Toast.LENGTH_SHORT).show();
					break;
				case R.id.action_sortby_likes:
					query.orderByDescending("likesCount");
					Toast.makeText(getApplicationContext(), "Loading results sorted by number of likes", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}

				return query;
			}
		};

		adapter = new ItemAdapter(this, queryFactory);
		setListAdapter(adapter);
	}

}
