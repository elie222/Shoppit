package il.ac.huji.shoppit;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.Menu;

/**
 * @author Elie2
 * This activity is used to add a new item. Makes use of two fragments:
 * NewItemFragment and CameraFragment. Pictures for items are optional.
 */
public class NewItemActivity extends Activity {
	 
    private Item item = null;
    private byte[] photoData = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	item = new Item();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
 
        setContentView(R.layout.activity_new_item);
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
 
}