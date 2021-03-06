package il.ac.huji.shoppit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private static final int PROFILE_REQUEST_CODE = 5002;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mCategoryTitles;

	private NavDrawerAdapter mNavDrawerAdapter;

	final Activity mainActivity = this;

	int selectedCategory = 1;

	private int sortBySelected = 0;

	// -------------
	// for getting location
	// -------------
	private LocationClient mLocationClient;

	// used when Google Play services is unavailable.
	private LocationRetriever2 mFallbackLocationRetriever;

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)		// 5 seconds
			.setFastestInterval(1000)	// 1 second
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	// if mLocationClient has found a location.
	private boolean foundLocationLC = false;

	// if mFallbackLocationRetriever has found a location.
	private boolean foundLocationFLR = false;

	/*
	 * Define a request code to send to Google Play services
	 * This code is returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Item.fillMap();

		ParseObject.registerSubclass(Item.class);
		ParseObject.registerSubclass(Shop.class);
		ParseObject.registerSubclass(Comment.class);
		ParseObject.registerSubclass(Report.class);
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli");
		ParseFacebookUtils.initialize(getString(R.string.app_id));

		mTitle = mDrawerTitle = getTitle();
		mCategoryTitles = getResources().getStringArray(R.array.categories_array);
		//		mGeneralNavBarTitles = getResources().getStringArray(R.array.general_navbar_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mNavDrawerAdapter = new NavDrawerAdapter(getBaseContext());
		mNavDrawerAdapter.addSeparatorItem(CATEGORY_SEPARATOR);
		mNavDrawerAdapter.addItem(getResources().getString(R.string.all));
		mNavDrawerAdapter.addItems(mCategoryTitles);
		mNavDrawerAdapter.addSeparatorItem(GENERAL_SEPARATOR);
		mNavDrawerAdapter.addItem(getResources().getString(R.string.shops));
		mNavDrawerAdapter.addItem(getResources().getString(R.string.add_shop));
		mNavDrawerAdapter.addItem(getResources().getString(R.string.settings));
		mNavDrawerAdapter.addItem(getResources().getString(R.string.profile));

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

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		if (sharedPrefs.getString("currency_key", null) == null) {
			FragmentManager fm = getFragmentManager();
			CurrencyDialogFragment currencyDialog = new CurrencyDialogFragment();
			currencyDialog.setRetainInstance(true);
			currencyDialog.show(fm, "currency_dialog_fragment");
		}


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

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
		switch (item.getItemId()) {
		case R.id.action_map:
			Intent intent = new Intent(getBaseContext(), MapActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_add:
			if (ParseUser.getCurrentUser() != null) {
				startNewItemIntent();
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

	void selectItem(int position) {
		if (mNavDrawerAdapter.isSeparator(position)) {
			return;
		}

		String sectionName = mNavDrawerAdapter.getSectionName(position);
		String selectedName = mNavDrawerAdapter.getItemName(position);

		if (sectionName == CATEGORY_SEPARATOR) {

			Location lastLocation = getLastLocation();

			if (lastLocation != null) {
				// update the main content by replacing fragments
				Fragment fragment = new CategoryFragment();
				Bundle args = new Bundle();

				args.putString(CategoryFragment.ARG_CATEGORY_NAME, selectedName);
				args.putDouble(MainActivity.LATITUDE_EXTRA, lastLocation.getLatitude());
				args.putDouble(MainActivity.LONGITUDE_EXTRA, lastLocation.getLongitude());
				fragment.setArguments(args);

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

				// update selected item and title, then close the drawer
				mDrawerList.setItemChecked(position, true);
				setTitle(selectedName);
				mDrawerLayout.closeDrawer(mDrawerList);

				selectedCategory = position;

			} else {
				Toast.makeText(mainActivity, "Error getting device location",
						Toast.LENGTH_LONG).show();
			}

			return;

		} else if (selectedName.equals( getResources().getString(R.string.add_shop) )) {
			// start new activity
			if (ParseUser.getCurrentUser() != null) {
				startNewShopIntent();
			} else {
				Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
				startActivityForResult(loginIntent, ADD_SHOP_REQUEST_CODE);
			}

			mDrawerLayout.closeDrawer(mDrawerList);
			return;

		} else if (selectedName.equals( getResources().getString(R.string.shops) )) {
			Location lastLocation = getLastLocation();

			if (lastLocation != null) {
				// update the main content by replacing fragments

				Fragment fragment = new ShopListFragment();
				Bundle args = new Bundle();

				args.putDouble(MainActivity.LATITUDE_EXTRA, lastLocation.getLatitude());
				args.putDouble(MainActivity.LONGITUDE_EXTRA, lastLocation.getLongitude());
				fragment.setArguments(args);

				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

				// update selected item and title, then close the drawer
				mDrawerList.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(mDrawerList);

				selectedCategory = position;

				return;
			} else {
				Toast.makeText(mainActivity, "Error getting device location",
						Toast.LENGTH_LONG).show();
				return;
			}
		} else if (selectedName.equals( getResources().getString(R.string.settings) )) {
			// Display the fragment as the main content.
			Fragment fragment = new SettingsFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			setTitle(selectedName);
			mDrawerLayout.closeDrawer(mDrawerList);

			selectedCategory = position;

			return;
		} else if (selectedName.equals( getResources().getString(R.string.profile) )) {

			if (ParseUser.getCurrentUser() == null) {
				Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
				startActivityForResult(loginIntent, PROFILE_REQUEST_CODE);
			} else {
				// Display the fragment as the main content.
				Fragment fragment = new ProfileFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			}

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			setTitle(selectedName);
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
			startNewItemIntent();
		} else if (requestCode == ADD_SHOP_REQUEST_CODE && resultCode == RESULT_OK){
			startNewShopIntent();
		} else if (requestCode == PROFILE_REQUEST_CODE && resultCode == RESULT_OK){
			// Display the fragment as the main content.
			Fragment fragment = new ProfileFragment();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		} else if (requestCode == CONNECTION_FAILURE_RESOLUTION_REQUEST) {
			// this isn't doing anything ATM
			/*
			 * If the result code is Activity.RESULT_OK, try
			 * to connect again
			 */
			switch (resultCode) {
			// If Google Play services resolved the problem
			case Activity.RESULT_OK:
				break;
				// If any other result was returned by Google Play services
			default:
				break;
			}

		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {

			//			Toast.makeText(mainActivity, "Google Play services is available.",
			//					Toast.LENGTH_LONG).show();

			Log.d("Location Updates", "Google Play services is available.");
			setUpLocationClientIfNeeded();
			mLocationClient.connect();

		} else {
			//			Toast.makeText(mainActivity, "Google Play services is unavailable. Using LR2.",
			//					Toast.LENGTH_LONG).show();

			startUpFallbackLocationRetriever();

		}
	}

	private void startUpFallbackLocationRetriever() {

		mFallbackLocationRetriever = new LocationRetriever2(new LocationRetriever2.TimerFunc() {

			@Override
			void timerFunc() {

				//This function will fill the list of nearby items
				//when the timer for getting the device position has elapsed
				//or as soon as the position is located.

				runOnUiThread(new Runnable() {
					public void run() {
						Log.i(TAG, "LR2 - runOnUiThread");
						//					Log.d(TAG, (GeneralInfo.location == null)+"");
						//					Log.d(TAG, (GeneralInfo.location)+"");
						if (GeneralInfo.location == null) { //In case of error
							Toast.makeText(mainActivity, "Error getting device location.",
									Toast.LENGTH_LONG).show();
						} else {
							foundLocationFLR = true;
							selectItem(mNavDrawerAdapter.getPosition(CATEGORY_SEPARATOR)+1);
						}
					}
				});

			}

		});

		// Get the device location
		mFallbackLocationRetriever.startGettingUpdates(this, 10000);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
		if (mFallbackLocationRetriever != null) {
			mFallbackLocationRetriever.stopGettingUpdates();
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
		if (!foundLocationLC && location != null) {
			foundLocationLC = true;
			selectItem(mNavDrawerAdapter.getPosition(CATEGORY_SEPARATOR)+1);
		}
		GeneralInfo.location = location;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects.
		 * If the error has a resolution, try sending an Intent to
		 * start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			startUpFallbackLocationRetriever();
		}

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

	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;
		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}
		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}
		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}


	/**
	 * This will return the last location using either the GooglePlayServives location
	 * finder or the fallback location retriever (LocationRetriever2).
	 * @return the last location or null.
	 */
	private Location getLastLocation() {
		if (foundLocationLC) {
			return mLocationClient.getLastLocation();
		} else if (foundLocationFLR) {
			return GeneralInfo.location;
		} else {
			return null;
		}
	}

	@Override
	public boolean onSearchRequested() {
		Bundle appData = new Bundle();
		appData.putDouble(LATITUDE_EXTRA, getLastLocation().getLatitude());
		appData.putDouble(LONGITUDE_EXTRA, getLastLocation().getLongitude());
		startSearch(null, false, appData, false);
		return true;
	}

	public int getSortBy() {
		return sortBySelected;
	}

	public void setSortBy(int position) {
		sortBySelected = position;
	}

	private void startNewItemIntent() {
		try {
			Location lastLocation = getLastLocation();
			if (lastLocation != null) {
				Intent intent = new Intent(getBaseContext(), NewItemActivity.class);
				intent.putExtra(LATITUDE_EXTRA, lastLocation.getLatitude());
				intent.putExtra(LONGITUDE_EXTRA, lastLocation.getLongitude());
				startActivity(intent);
			} else {
				Toast.makeText(mainActivity, "Error getting device location",
						Toast.LENGTH_LONG).show();
				return;
			}
		} catch (Exception e) {

		}

	}

	private void startNewShopIntent() {
		try {
			Location lastLocation = getLastLocation();
			if (lastLocation != null) {
				Intent intent = new Intent(getBaseContext(), NewShopActivity.class);
				intent.putExtra(LATITUDE_EXTRA, lastLocation.getLatitude());
				intent.putExtra(LONGITUDE_EXTRA, lastLocation.getLongitude());
				startActivity(intent);
			} else {
				Toast.makeText(mainActivity, "Error getting device location",
						Toast.LENGTH_LONG).show();
				return;
			}
		} catch (Exception e) {
			return;
		}


	}


}