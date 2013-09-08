package il.ac.huji.shoppit;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class NewItemActivity extends Activity {
	 
    private Item item;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	item = new Item();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
 
        // Begin with main data entry view,
        // NewMealFragment
        setContentView(R.layout.activity_new_item);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
 
        if (fragment == null) {
            fragment = new NewItemFragment();
            manager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }
 
    public Item getCurrentItem() {
        return item;
    }
 
}