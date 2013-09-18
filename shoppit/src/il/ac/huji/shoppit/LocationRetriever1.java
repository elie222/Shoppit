//package il.ac.huji.shoppit;
//
//import android.content.Context;
//import android.location.Location;
//import android.os.Bundle;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.LocationClient;
//
///**
// * This class uses new Location Manager API to get the device location.
// * It is the preferred location retrieving method and LocationRetriever2 is used only
// * if this one fails.
// */
//public class LocationRetriever1 implements
//GooglePlayServicesClient.ConnectionCallbacks,
//GooglePlayServicesClient.OnConnectionFailedListener {
//
//	private Context context;
//	private LocationClient mLocationClient;
//	private Long waitTime;
//
//
//	/**
//	 * 
//	 * @param context
//	 * @param waitTime how much to wait before stopping to listen for updates.
//	 * Set this variable to null to stop only manually.
//	 */
//	private LocationRetriever1(Context context, Long waitTime) {
//		this.context = context;
//		this.waitTime = waitTime;
//	}
//
//
//	/**
//	 * Try to connect to Google Play Services.
//	 * @param context
//	 * @param waitTime how much to wait before stopping to listen for updates.
//	 * Set this variable to 0 to stop only manually.
//	 * @return an instance of this class on success, null on failure.
//	 */
//	static LocationRetriever1 connect(Context context, Long waitTime) {
//		int resultCode = GooglePlayServicesUtil.
//				isGooglePlayServicesAvailable(context);
//
//		if (ConnectionResult.SUCCESS == resultCode) {
//			return new LocationRetriever1(context, waitTime);
//		}
//
//		return null;
//	}
//
//
//	/*
//	 * Called by Location Services if the attempt to
//	 * Location Services fails.
//	 */
//	@Override
//	public void onConnectionFailed(ConnectionResult arg0) {
//		GeneralInfo.lr1 = null;
//		GeneralInfo.lr2.startGettingUpdates(context, waitTime);
//	}
//
//
//	/*
//	 * Called by Location Services when the request to connect the
//	 * client finishes successfully. At this point, you can
//	 * request the current location or start periodic updates
//	 */
//	@Override
//	public void onConnected(Bundle arg0) {}
//
//
//	/*
//	 * Called by Location Services if the connection to the
//	 * location client drops because of an error.
//	 */
//	@Override
//	public void onDisconnected() {
//		GeneralInfo.lr1 = null;
//		GeneralInfo.lr2.startGettingUpdates(context, waitTime);
//	}
//
//
//
//	void startGettingUpdates() {
//
//		mLocationClient = new LocationClient(context, this, this);
//		mLocationClient.connect();
//
//	}
//
//
//	void stopGettingUpdates() {
//		mLocationClient.disconnect();
//	}
//
//
//	Location getLastLocation() {
//		try {
//			GeneralInfo.location = mLocationClient.getLastLocation();
//			return mLocationClient.getLastLocation();
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//
//
//}