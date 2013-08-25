package il.ac.huji.shoppit;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import android.graphics.Bitmap;

@ParseClassName("Item")
public class Item extends ParseObject {
	public String id;
	public String name;
	public Bitmap image;
}
