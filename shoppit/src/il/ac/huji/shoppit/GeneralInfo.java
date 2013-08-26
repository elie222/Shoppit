package il.ac.huji.shoppit;

import android.content.Context;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;

/**
 * This class contains information that is global to the entire application.
 */
public class GeneralInfo {

	//The picture of an item that was taken by the user.
	static Bitmap itemImage = null;
	
	//Whether the user is logged in or not.
	//This variable is set when starting the app or logging in manually.
	static boolean logged = false;
	static String username = null;

	//The unique device ID (used to identify the user when uploading items).
	//It is set when starting the application.
	//If it remains null, then there was an error getting the device ID.
	//Cannot upload nor rate items when this value is null.
	static String deviceID = null;

	static String[] categories = new String[] { "Clothes", "Cosmetics", "Electronics",
		"Food", "Furniture", "Jewelry", "(Other)"};

	static String[][] subCateg = new String[][] {

		//Clothes
		{"Hats", "Shirts", "Shoes", "Pants", "(Other)"},

		//Cosmetics
		{"Eyes", "Face", "Lips", "Nails", "(Other)"},

		//Electronics
		{"Appliances", "Computers", "DVDs", "Headphones", "Music Players", "Telephony", "TVs", "Watches", "(Other)"},

		//Food
		{"Baked", "Beverages", "Dairy", "Liquor", "Meat", "Produce", "Snacks", "(Other)"},

		//Furniture
		{"Beds", "Bookcases", "Chairs", "Closets", "Sofas", "Tables", "(Other)"},

		//Jewelry
		{"Bracelets", "Earrings", "Necklaces", "Rings", "(Other)"},

	};



	/**
	 * This function is called when starting the application.
	 * Sets the device ID, returns true if getting device information was available.
	 * If there was an error getting the device ID, deviceID remains null.
	 * @param context
	 */
	static void setID(Context context) {

		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		//final String tmDevice, tmSerial, androidId;
		//tmDevice = tm.getDeviceId();
		//tmSerial = tm.getSimSerialNumber();
		//androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		String tmDevice = tm.getDeviceId();

		if (tmDevice == null) // || tmSerial == null || androidId == null)
			return;
		
		//Device ID should not be all zeros, because it should be unique.
		//A device ID of all zeros means there was a problem getting the true device identifier.
		if (tmDevice.matches("(0)*"))
			return;

		//UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		//deviceID = deviceUuid.toString();
		deviceID = tmDevice;

	}


}
