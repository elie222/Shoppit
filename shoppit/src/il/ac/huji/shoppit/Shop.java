package il.ac.huji.shoppit;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Shop
 */

// TODO - things to add?
// items query, other info like address, opening times, etc.

@ParseClassName("Shop")
public class Shop extends ParseObject {
	public Shop() {
		// A default constructor is required.
	}
	
	public String getName() {
		return getString("name");
	}

	public void setName(String name) {
		put("name", name);
	}
	
	public String getDescription() {
		return getString("description");
	}

	public void setDescription(String description) {
		put("description", description);
	}
	
	public ParseUser getAuthor() {
		return getParseUser("author");
	}

	public void setAuthor(ParseUser user) {
		put("author", user);
	}
	
	public ParseFile getPhotoFile() {
		return getParseFile("photo");
	}

	public void setPhotoFile(ParseFile file) {
		put("photo", file);
	}
	
	public ParseGeoPoint getLocation() {
		return getParseGeoPoint("location");
	}

	public void setLocation(ParseGeoPoint point) {
		put("location", point);
	}
	
	public ParseRelation<ParseUser> getLikesRelation() {
		return getRelation("likes");
	}
	
	// use Cloud function to like a Shop
	
}
