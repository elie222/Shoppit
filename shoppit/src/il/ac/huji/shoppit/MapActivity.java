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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.support.v4.app.NavUtils;

// TODO - add shops to map instead, or as well?

public class MapActivity extends Activity
implements ConnectionCallbacks, OnConnectionFailedListener, 
LocationListener, OnMyLocationButtonClickListener,
OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener {

	private final static String TAG = "MAP_ACTIVITY";

	private GoogleMap mMap;
	private LocationClient mLocationClient;

	private static final LatLng SOMEWHERE_IN_JLEM = new LatLng(31.7644, 35.2116);

	private Marker mCurrentLocationMarker;
//	private Marker mSomewhereInJlem;
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
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
					LatLngBounds bounds = new LatLngBounds.Builder()
					.include(SOMEWHERE_IN_JLEM)
					.build();
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					} else {
						mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
				}
			});
		}
	}

	private void addMarkersToMap() {
		// TODO add photos to markers. see here: https://developers.google.com/maps/documentation/android/marker#info_windows

		// Uses a colored icon.
//		mSomewhereInJlem = mMap.addMarker(new MarkerOptions()
//		.position(SOMEWHERE_IN_JLEM)
//		.title("Jerusalem")
//		.snippet("Population: 804,400")
//		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
		ParseGeoPoint userLocation = (ParseGeoPoint) new ParseGeoPoint(SOMEWHERE_IN_JLEM.latitude, SOMEWHERE_IN_JLEM.longitude);
		query.whereNear("location", userLocation);
		query.setLimit(30);

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
								.snippet(item.getPrice() + " " + item.getCurrency())
								// this gives each marker a different colour
								.icon(BitmapDescriptorFactory.defaultMarker(i * 360 / items.size())));

						mMarkerItemMap.put(marker, item);
					}

				} else {
					Log.d(TAG, "Error: " + e.getMessage());
				}	
			}

		});
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
//		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// do nothing

//		String msg = "Location = " + location;
//		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

		//		if (mLocationClient != null && mLocationClient.isConnected()) {
		//            String msg = "Location = " + mLocationClient.getLastLocation();
		//            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		//            
		//    		LatLng currentLatLng = new LatLng(mLocationClient.getLastLocation().getLatitude(), 
		//    				mLocationClient.getLastLocation().getLongitude());
		//			
		//    		mSomewhereInJlem = mMap.addMarker(new MarkerOptions()
		//    		.position(currentLatLng)
		//    		.title("Current location")
		//    		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		//        }

		if (mCurrentLocationMarker != null) {
			mCurrentLocationMarker.remove();
		}

		LatLng currentLatLng = new LatLng(location.getLatitude(), 
				location.getLongitude());

		mCurrentLocationMarker = mMap.addMarker(new MarkerOptions()
		.position(currentLatLng)
		.title("Current location")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

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
		Item item = mMarkerItemMap.get(marker);

		if (item == null) {
			return;
		}

		GeneralInfo.itemHolder = item;
		Intent intent = new Intent(getBaseContext(), ItemActivity.class);
		startActivity(intent);
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
