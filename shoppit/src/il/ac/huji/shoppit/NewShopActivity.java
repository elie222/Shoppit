package il.ac.huji.shoppit;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
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
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	shop = new Shop();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
 
        setContentView(R.layout.activity_new_shop);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
 
        if (fragment == null) {
            fragment = new NewShopFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_shop, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    
	    return super.onOptionsItemSelected(item);
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