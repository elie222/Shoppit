package il.ac.huji.shoppit;

import android.graphics.Bitmap;

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

		//Other
		{""}

	};




}
