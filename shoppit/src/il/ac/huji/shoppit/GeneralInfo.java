package il.ac.huji.shoppit;

import java.text.DecimalFormat;

import com.parse.ParseGeoPoint;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.view.View;
import android.widget.TextView;

/**
 * This class contains information that is global to the entire application.
 * This class also contains various static method to be used from different activities.
 */
public class GeneralInfo {

	//The info of the item being uploaded by the user.
	//It is saved here because various activities are used to get the location.
	//Storing it here makes it easy for them to call a function that uploads the item.
	static Bitmap itemImage = null;
	static byte[] itemImageData = null;
	static Location location = null;
	static Item itemHolder = null;
	static Shop shopHolder = null;


	/**
	 * Calculate the distance from the given ParseGeoPoint and set it in the given view.
	 * Make the view invisible if current location is unknown.
	 * The result is in km if the distance is above 1000m, otherwise in meters.
	 * @param pgp
	 * @param view
	 */
	static void displayDistance(ParseGeoPoint pgp, TextView view) {
		if (location == null) {
			view.setVisibility(View.GONE);
		} else {
			double distInKM = pgp.distanceInKilometersTo(
					new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
			DecimalFormat myFormat = new DecimalFormat("#.#");
			String roundedDistInKM = myFormat.format(distInKM);
			boolean isKM = distInKM >= 1;
			long distInMeters = Math.round(distInKM * 1000);

			view.setText("Distance: " +
					(isKM ? roundedDistInKM + " km" : distInMeters + " m"));
		}
	}


	//	static String currencyName = null,
	//			currencySymbol = null;


	//Location readers
	//	static LocationRetriever1 lr1; //This is always null. Not null only when connected to Google Play.
	static LocationRetriever2 lr2 = new LocationRetriever2(null);


	//	/**
	//	 * 
	//	 * @param context
	//	 * @param waitTime How much to wait before stopping to listen for updates, in milliseconds.
	//	 * Set this variable to 0 to stop only manually.
	//	 * @return
	//	 */
	//	static boolean startGettingLocation(Context context, long waitTime) {
	//
	//		GeneralInfo.location = null;
	//
	//		lr1 = LocationRetriever1.connect(context, waitTime);
	//		if (lr1 != null) {
	//			lr1.startGettingUpdates();
	//		}
	//
	//		if (lr1 == null) {
	//			if (!lr2.startGettingUpdates(context, waitTime)) {
	//				return false;
	//			}
	//		}
	//
	//		return true;
	//	}
	//
	//
	//	static void stopGettingLocation() {
	//
	//		try {
	//			lr1.stopGettingUpdates();
	//			lr1 = null;
	//		} catch (Exception e) {}
	//		lr2.stopGettingUpdates();
	//
	//		//Save the last location update.
	//		try {
	//			GeneralInfo.lr1.getLastLocation();
	//		} catch (Exception e) {
	//			GeneralInfo.lr2.getLastLocation();
	//		}
	//
	//	}


	// stuff like this should be in res/values/strings.xml

	//	static String[] categories = new String[] { "Clothes", "Cosmetics", "Electronics",
	//		"Food", "Furniture", "Jewelry", "(Other)"};
	//
	//	static String[][] subCateg = new String[][] {
	//
	//		//Clothes
	//		{"Hats", "Shirts", "Shoes", "Pants", "(Other)"},
	//
	//		//Cosmetics
	//		{"Eyes", "Face", "Lips", "Nails", "(Other)"},
	//
	//		//Electronics
	//		{"Appliances", "Computers", "DVDs", "Headphones", "Music Players", "Telephony", "TVs", "Watches", "(Other)"},
	//
	//		//Food
	//		{"Baked", "Beverages", "Dairy", "Liquor", "Meat", "Produce", "Snacks", "(Other)"},
	//
	//		//Furniture
	//		{"Beds", "Bookcases", "Chairs", "Closets", "Sofas", "Tables", "(Other)"},
	//
	//		//Jewelry
	//		{"Bracelets", "Earrings", "Necklaces", "Rings", "(Other)"},
	//
	//		//Other
	//		{""}
	//
	//	};



}
