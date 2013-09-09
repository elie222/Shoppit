package il.ac.huji.shoppit;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

public class NewShopActivity extends Activity {
	 
    private Shop shop = null;
    private byte[] photoData = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	shop = new Shop();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
 
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
 
}