package il.ac.huji.shoppit;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;


/**
 * @author Elie2
 * This activity is used to add a new shop. Makes use of two fragments:
 * NewShopFragment and CameraFragment. Pictures for shops are optional.
 */
public class NewShopActivity extends Activity {

	private Shop shop = null;
	private byte[] photoData = null;

	private double latitude = 0;
	private double longitude = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		shop = new Shop();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		latitude = intent.getDoubleExtra(MainActivity.LATITUDE_EXTRA, 0);
		longitude = intent.getDoubleExtra(MainActivity.LONGITUDE_EXTRA, 0);

		setContentView(R.layout.activity_new_shop);
		FragmentManager manager = getFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = new NewShopFragment();
			manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
		}
	}

	public Shop getCurrentShop() {
		return shop;
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

}