package il.ac.huji.shoppit;

import android.graphics.Bitmap;

/**
 * Abbreviated information about an item, as shown in the list of nearby items.
 */
public class ItemThumb {
	
	public final String id;
	public final String name;
	public final Bitmap image;
	
	
	
	public ItemThumb(String id, String name, int price, Bitmap image) {
		this.id = id;
		this.name = name + " – $" + price;
		this.image = image;
	}

}
