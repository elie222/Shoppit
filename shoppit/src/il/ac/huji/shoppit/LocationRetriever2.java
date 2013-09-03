package il.ac.huji.shoppit;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * This class uses the old Location Manager API to get the device location.
 * It is used if the new Location Manager API fails.
 * 
 * This was taken off an example from Stack Overflow. It uses the GPS and the network signals to
 * evaluate the device position. If no information was gained within 20 seconds it returns failure.
 */
public class LocationRetriever2 {

	Timer timer;
	TimerFunc timerFunc;
	LocationManager lm = null;
	boolean gps_enabled = false;
	boolean network_enabled = false;
	
	
	/**
	 * 
	 * @param timerFunc This function will run when a location has been found or timer has elapsed.
	 * It can be used to fill the nearby item list.
	 */
	public LocationRetriever2(TimerFunc timerFunc) {
		this.timerFunc = timerFunc;
	}


	/**
	 * Start listening on location updates.
	 * @param context
	 * @param waitTime how much to wait before stopping to listen for updates, in milliseconds.
	 * set the variable to 0 to stop listening to updates only manually.
	 * @return
	 */
	public boolean startGettingUpdates(Context context, long waitTime) {
		
		timer = new Timer();

		if (lm == null)
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch (Exception e) {}

		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch (Exception e) {}

		//don't start listeners if no provider is enabled
		if (!gps_enabled && !network_enabled)
			return false;

		if (gps_enabled) {
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		}

		if (network_enabled) {
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		}

		
		if (waitTime > 0) {
			
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					getLastLocation();
				}
			};
			timer.schedule(task, waitTime);
		}

		return true;
	}


	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			GeneralInfo.location = location;
			if (timerFunc != null)
				timerFunc.timerFunc();
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};


	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
			GeneralInfo.location = location;
			if (timerFunc != null)
				timerFunc.timerFunc();
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};


	void getLastLocation() {

		timer.cancel();
		stopGettingUpdates();

		Location net_loc = null, gps_loc = null;

		if (gps_enabled)
			gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (network_enabled)
			net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		//if there are both values use the latest one
		if (gps_loc != null && net_loc != null){
			if (gps_loc.getTime() > net_loc.getTime())
				GeneralInfo.location = gps_loc;
			else
				GeneralInfo.location = net_loc;
		}

		else if (gps_loc != null){
			GeneralInfo.location = gps_loc;
		}
		else if (net_loc != null){
			GeneralInfo.location = net_loc;
		}
		
		if (timerFunc != null)
			timerFunc.timerFunc();

	}


	void stopGettingUpdates() {
		lm.removeUpdates(locationListenerGps);
		lm.removeUpdates(locationListenerNetwork);
	}
	
	
	
	static abstract class TimerFunc {
		abstract void timerFunc();
	}




}