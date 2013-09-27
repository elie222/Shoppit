package il.ac.huji.shoppit;

import java.util.List;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;

/**
 * @author Elie2
 * This activity is used to add a new item. Makes use of two fragments:
 * NewItemFragment and CameraFragment. Pictures for items are optional.
 */
public class NewItemActivity extends Activity {

	private Item item = null;
	private byte[] photoData = null;
	private double latitude = 0;
	private double longitude = 0;
	private String barcode = null;
	private List<Item> itemList = null;
	
	//This item info is saved in case the user comes to the new item fragment after scanning
	//a barcode and wishes to go back to the picture taking fragment to take own picture.
	boolean savedData = false;
	String name;
	String price;
	int categorySelection;
	String keywords;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		item = new Item();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_item);

		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(MainActivity.LATITUDE_EXTRA, 0);
		longitude = intent.getDoubleExtra(MainActivity.LONGITUDE_EXTRA, 0);

		FragmentManager manager = getFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new CameraFragment();
			manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
	}

	public Item getCurrentItem() {
		return item;
	}

	public byte[] getCurrentPhotoData() {
		return photoData;
	}

	public void setCurrentPhotoData(byte[] data) {
		photoData = data;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> list) {
		itemList = list;
	}

}