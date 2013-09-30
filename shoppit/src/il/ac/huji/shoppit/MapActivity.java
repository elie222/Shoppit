package il.ac.huji.shoppit;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.ParseQueryAdapter;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

// TODO - add shops to map instead, or as well?

public class MapActivity extends Activity
implements ConnectionCallbacks, OnConnectionFailedListener, 
LocationListener, OnMyLocationButtonClickListener,
OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener {

	private final static String TAG = "MAP_ACTIVITY";

	public final static String SHOW_ITEM_EXTRA = "SHOW_ITEM_EXTRA";
	public final static String QUERY_EXTRA = "QUERY_EXTRA";

	private final static int ITEMS_TO_SHOW = 30;

	private GoogleMap mMap;
	private LocationClient mLocationClient;

	private Item mItem = null;
	private LatLng mItemLatLng = null;
	private Marker mItemMarker = null;

	private String mQueryString = null;

	private boolean movedCameraToInitialPosition = false;

	private HashMap<Marker, Item> mMarkerItemMap;

	// These settings are the same as the settings for the map. They will in fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000)         // 5 seconds
			.setFastestInterval(16)    // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// Show the Up button in the action bar.
		setupActionBar();

		if (getIntent().getBooleanExtra(SHOW_ITEM_EXTRA, false)) {
			mItem = GeneralInfo.itemHolder;
			mItemLatLng = new LatLng(mItem.getLocation().getLatitude(), mItem.getLocation().getLongitude());
		}

		mQueryString = getIntent().getStringExtra(QUERY_EXTRA);
	}

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

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		// Add lots of markers to the map.
		addMarkersToMap();

		// Set listeners for marker events.  See the bottom of this class for their behavior.
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnMarkerDragListener(this);

		// Pan to see all markers in view.
		// Cannot zoom to bounds until the map has a size.
		final View mapView = getFragmentManager().findFragmentById(R.id.map).getView();

		if (mapView.getViewTreeObserver().isAlive()) {
			mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@SuppressWarnings("deprecation") // We use the new method when supported
				@SuppressLint("NewApi") // We check which build version we are using.
				@Override
				public void onGlobalLayout() {

					LatLng currentLatLng = new LatLng(mLocationClient.getLastLocation().getLatitude(), 
							mLocationClient.getLastLocation().getLongitude());

					if (mItemLatLng != null) {
						// zoom can be between 2.0 and 21.0
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mItemLatLng, 15));

						mItemMarker.showInfoWindow();
					} else {
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
					}

				}
			});
		}
	}

	private void addMarkersToMap() {
		// TODO add photos to markers. see here: https://developers.google.com/maps/documentation/android/marker#info_windows

		if (mItem != null) {

			Log.d(TAG, "mItem not null: " + mItem.getName());

			mItemMarker = mMap.addMarker(new MarkerOptions()
			.position(mItemLatLng)
			.title(mItem.getName())
			.snippet(mItem.getCurrency() + mItem.getPrice())
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		} else if (mQueryString != null) {

			Log.d(TAG, "mQueryString not null: " + mQueryString);

			ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
			query.whereContains("searchString", mQueryString.toLowerCase());
			query.setLimit(ITEMS_TO_SHOW);

			query.findInBackground(new FindCallback<Item>() {

				@Override
				public void done(List<Item> items, ParseException e) {
					if (e == null) {
						Log.d(TAG, "Retrieved " + items.size() + " items");

						mMarkerItemMap = new HashMap<Marker, Item>();

						// add item markers to map
						for (int i = 0; i < items.size(); i++) {
							Item item = items.get(i);
							Marker marker = mMap.addMarker(new MarkerOptions()
							.position(new LatLng(item.getLocation().getLatitude(),
									item.getLocation().getLongitude()))
									.title(item.getName())
									.snippet(item.getPrice() + item.getCurrency())
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

							mMarkerItemMap.put(marker, item);
						}

					} else {
						Log.d(TAG, "Error: " + e.getMessage());
					}	
				}

			});
		} else {

			Log.d(TAG, "mItem and mQueryString are both null");

			ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
			ParseGeoPoint userLocation = (ParseGeoPoint) new ParseGeoPoint(mLocationClient.getLastLocation().getLatitude(),
					mLocationClient.getLastLocation().getLongitude());
			query.whereNear("location", userLocation);
			query.setLimit(ITEMS_TO_SHOW);

			query.findInBackground(new FindCallback<Item>() {

				@Override
				public void done(List<Item> items, ParseException e) {
					if (e == null) {
						Log.d(TAG, "Retrieved " + items.size() + " items");

						mMarkerItemMap = new HashMap<Marker, Item>();

						// add item markers to map
						for (int i = 0; i < items.size(); i++) {
							Item item = items.get(i);
							Marker marker = mMap.addMarker(new MarkerOptions()
							.position(new LatLng(item.getLocation().getLatitude(),
									item.getLocation().getLongitude()))
									.title(item.getName())
									.snippet(item.getPrice() + item.getCurrency())
									.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

							mMarkerItemMap.put(marker, item);
						}

					} else {
						Log.d(TAG, "Error: " + e.getMessage());
					}	
				}

			});
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


	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		
		if (!movedCameraToInitialPosition && location != null) {

			movedCameraToInitialPosition = true;

			setUpMapIfNeeded();
		}

	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	/**
	 * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(
				REQUEST,
				this);  // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
	 */
	@Override
	public void onDisconnected() {
		// Do nothing
	}


	//
	// Marker related listeners.
	//

	@Override
	public void onMarkerDrag(Marker marker) {
		// do nothing
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		// do nothing
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		// do nothing
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

		if (marker.equals(mItemMarker)) {
			Intent intent = new Intent(getBaseContext(), ItemActivity.class);
			startActivity(intent);
		} else {
			Item item = mMarkerItemMap.get(marker);
			GeneralInfo.itemHolder = item;
			Intent intent = new Intent(getBaseContext(), ItemActivity.class);
			startActivity(intent);
		}

	}

	/**
	 * This shows a little info box when a marker is clicked.
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		// We return false to indicate that we have not consumed the event and that we wish
		// for the default behavior to occur (which is for the camera to move such that the
		// marker is centered and for the marker's info window to open, if it has one).
		return false;
	}

}
