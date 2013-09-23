package il.ac.huji.shoppit;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Report")
public class Report extends ParseObject {
	public Report() {
		// A default constructor is required.
	}
	
	public ParseUser getAuthor() {
		return getParseUser("author");
	}

	public void setAuthor(ParseUser user) {
		put("author", user);
	}
	
	public String getReason() {
		return getString("reason");
	}
	
	public void setReason(String reason) {
		put("reason", reason);
	}
	
	public ParseObject getItem() {
		return getParseObject("item");
	}
	
	public void setItem(ParseObject item) {
		put("item", item);
	}
}
