package il.ac.huji.shoppit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity
implements ConnectionCallbacks, OnConnectionFailedListener, 
LocationListener {

	private static final String TAG = "MAIN_ACT";

	private static final String GENERAL_SEPARATOR = "General";
	private static final String CATEGORY_SEPARATOR = "Categories";

	public static final String LATITUDE_EXTRA = "LATITUDE_EXTRA";
	public static final String LONGITUDE_EXTRA = "LONGITUDE_EXTRA";

	private static final int ADD_ITEM_REQUEST_CODE = 5000;
	private static final int ADD_SHOP_REQUEST_CODE = 5001;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mCategoryTitles;
	private String[] mGeneralNavBarTitles;

	private NavDrawerAdapter mNavDrawerAdapter;

	final Activity mainActivity = this;

	int selectedCategory = 1;

	// -------------
	// for getting location
	// -------------
	private LocationClient mLocationClient;

	// These settings are the same as the settings for the map. They will in fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(60* 60 * 1000)		// 60 minutes
			.setFastestInterval(60 * 1000)	// 60 seconds
			.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

	private boolean foundLocation = false;



	// You want to put this code in CategoryFragment.java
	//This class will get the device location to fill the list of nearby items.
	//	private LocationRetriever2 lr = new LocationRetriever2(new LocationRetriever2.TimerFunc() {
	//
	//		@Override
	//		void timerFunc() {
	//
	//			//This function will fill the list of nearby items
	//			//when the timer for getting the device position has elapsed
	//			//or as soon as the position is located.
	//
	//			runOnUiThread(new Runnable() {
	//				public void run() {
	//					//					Log.d(TAG, (GeneralInfo.location == null)+"");
	//					//					Log.d(TAG, (GeneralInfo.location)+"");
	//					if (GeneralInfo.location == null) { //In case of error
	//						Toast.makeText(mainActivity, "Error getting device location",
	//								Toast.LENGTH_LONG).show();
	//					}
	//
	//					selectItem(mNavDrawerAdapter.getPosition(CATEGORY_SEPARATOR)+1);
	//				}
	//			});
	//
	//		}
	//
	//	});

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Get the device location
		//		lr.startGettingUpdates(this, 10000);


		ParseObject.registerSubclass(Item.class);
		ParseObject.registerSubclass(Shop.class);
		ParseObject.registerSubclass(Comment.class);
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli");
		ParseFacebookUtils.initialize(getString(R.string.app_id));

		//Check if the user is logged in, connect to parse if so.
		//		checkIfLoggedIn();

		mTitle = mDrawerTitle = getTitle();
		mCategoryTitles = getResources().getStringArray(R.array.categories_array);
		mGeneralNavBarTitles = getResources().getStringArray(R.array.general_navbar_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		//		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mCategoryTitles));
		mNavDrawerAdapter = new NavDrawerAdapter(getBaseContext());
		mNavDrawerAdapter.addSeparatorItem(CATEGORY_SEPARATOR);
		mNavDrawerAdapter.addItems(mCategoryTitles);
		mNavDrawerAdapter.addSeparatorItem(GENERAL_SEPARATOR);
		mNavDrawerAdapter.addItems(mGeneralNavBarTitles);

		mDrawerList.setAdapter(mNavDrawerAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getActionBar().setHomeButtonEnabled(true);// REQUIRES API LEVEL 14.
		}

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// TODO moved to onResume
		//		if (savedInstanceState == null) {
		//			selectItem(mNavDrawerAdapter.getPosition(CATEGORY_SEPARATOR)+1);
		//		}
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

		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		menu.findItem(R.id.action_add).setVisible(!drawerOpen);
		menu.findItem(R.id.action_map).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons

		// DEMO CODE
		//		switch(item.getItemId()) {
		//		case R.id.action_websearch:
		//			// create intent to perform web search for this planet
		//			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		//			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
		//			// catch event that there's no activity to handle intent
		//			if (intent.resolveActivity(getPackageManager()) != null) {
		//				startActivity(intent);
		//			} else {
		//				Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
		//			}
		//			return true;
		//		default:
		//			return super.onOptionsItemSelected(item);
		//		}

		switch (item.getItemId()) {
		case R.id.action_map:
			Intent intent = new Intent(getBaseContext(), MapActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_add:
			if (ParseUser.getCurrentUser() != null) {
				if (foundLocation) {
					Location lastLocation = mLocationClient.getLastLocation();

					if (lastLocation == null ) {
						// this shouldn't ever happen really
						Toast.makeText(mainActivity, "Error getting device location",
								Toast.LENGTH_LONG).show();
						return true;
					}
					
					Intent newItemIntent = new Intent(getBaseContext(), NewItemActivity.class);
					newItemIntent.putExtra(LATITUDE_EXTRA, lastLocation.getLatitude());
					newItemIntent.putExtra(LONGITUDE_EXTRA, lastLocation.getLongitude());
					startActivity(newItemIntent);
				} else {
					Toast.makeText(mainActivity, "Error getting device location",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
				startActivityForResult(loginIntent, ADD_ITEM_REQUEST_CODE);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		if (mNavDrawerAdapter.isSeparator(position)) {
			return;
		}

		String sectionName = mNavDrawerAdapter.getSectionName(position);
		String selectedName = mNavDrawerAdapter.getItemName(position);
		//Log.i("MAIN ACT", selectedName);
		int positionInSection = mNavDrawerAdapter.getPositionInSection(position);

		if (sectionName == CATEGORY_SEPARATOR) {

			if (!foundLocation) {
				Toast.makeText(mainActivity, "Error getting device location",
						Toast.LENGTH_LONG).show();
				return;
			}
			
			Location lastLocation = mLocationClient.getLastLocation();

			if (lastLocation == null ) {
				// this shouldn't really happen
				Toast.makeText(mainActivity, "Error getting device location",
						Toast.LENGTH_LONG).show();
				return;
			}

			// update the main content by replacing fragments
			Fragment fragment = new CategoryFragment();
			Bundle args = new Bundle();

			args.putInt(CategoryFragment.ARG_CATEGORY_NUMBER, positionInSection);
			args.putDouble(CategoryFragment.ARG_LATITUDE, lastLocation.getLatitude());
			args.putDouble(CategoryFragment.ARG_LONGITUDE, lastLocation.getLongitude());
			fragment.setArguments(args);

			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			setTitle(mCategoryTitles[positionInSection]);
			mDrawerLayout.closeDrawer(mDrawerList);

			selectedCategory = position;
			return;

		} else if (selectedName.equals("Add Shop")) { // a bit ugly...
			// start new activity
			if (ParseUser.getCurrentUser() != null) {
				startActivity(new Intent(getBaseContext(), NewShopActivity.class));
			} else {
				Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
				startActivityForResult(loginIntent, ADD_SHOP_REQUEST_CODE);
			}

			mDrawerLayout.closeDrawer(mDrawerList);
			return;

		} else if (selectedName.equals("Log out")) { // ugly again...

			ParseUser.logOut();

			// TODO remove log out option from menu (replace with login option?)
			mDrawerLayout.closeDrawer(mDrawerList);
			return;

		} else if (selectedName.equals("Shops")) { // ugly again...
			// update the main content by replacing fragments
			Fragment fragment = new ShopListFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerLayout.closeDrawer(mDrawerList);

			selectedCategory = position;

			return;
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_ITEM_REQUEST_CODE && resultCode == RESULT_OK){
			startActivity(new Intent(getBaseContext(), NewItemActivity.class));
		} else if (requestCode == ADD_SHOP_REQUEST_CODE && resultCode == RESULT_OK){
			startActivity(new Intent(getBaseContext(), NewShopActivity.class));
		}
	}

	//	@Override
	//	protected void onPause() {
	//		super.onPause();
	//		lr.stopGettingUpdates();
	//	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(
					getApplicationContext(),
					this,  // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		// we only want to do this if no location has been found before. i.e. on app startup
		if (!foundLocation) {
			foundLocation = true;
			selectItem(mNavDrawerAdapter.getPosition(CATEGORY_SEPARATOR)+1);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle bundle) {
		mLocationClient.requestLocationUpdates(
				REQUEST,
				this);  // LocationListener
	}

	@Override
	public void onDisconnected() {
		// do nothing
	}

}