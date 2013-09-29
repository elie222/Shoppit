package il.ac.huji.shoppit;

import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.TextView;


/**
 * @author Elie2
 * This class handles displaying shops.
 */
public class ShopActivity extends Activity {

	private ShareActionProvider mShareActionProvider;

	private Shop mShop;

	private TextView nameTextView;
	private ParseImageView imageView;
	private TextView descriptionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);

		// add up caret to top let hand corner of screen
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mShop = GeneralInfo.shopHolder;

		nameTextView = (TextView) findViewById(R.id.nameTextView);
		imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);

		setupViewsWithShopData();

	}


	/**
	 * This function setups the views on the page with info from the Shop.
	 */
	protected void setupViewsWithShopData() {

		setTitle(mShop.getName());

		nameTextView.setText(mShop.getName());
		descriptionTextView.setText(mShop.getDescription());

		ParseFile photoFile = mShop.getPhotoFile();
		if (photoFile != null) {
			imageView.setParseFile(photoFile);
			imageView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		// show comments
		//reloadCommentsListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop, menu);
		MenuItem shop = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) shop.getActionProvider();

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "I really like this shop on Shoppit: "
				+ mShop.getName() + "!");
		sendIntent.setType("text/plain");

		mShareActionProvider.setShareIntent(sendIntent);

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
	    // TODO add show on map option
	    return super.onOptionsItemSelected(item);
	}

	
	public Shop getShop() {
		return mShop;
	}

}
