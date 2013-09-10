package il.ac.huji.shoppit;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ShopActivity extends Activity {

	public final static String EXTRA_SHOP_ID = "il.ac.huji.shoppit.SHOP_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);

		// The placeholder will be used before and during the fetch, to be replaced by the fetched image
		// data.
		//		ParseImageView imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		//		imageView.setPlaceholder(getResources().getDrawable(R.drawable.placeholder));


		// TODO - this is bad. We're downloading the item again here. See the note in ItemAdapter.java
		// for more info.

//		Intent intent = getIntent();
//		String shopId = intent.getStringExtra(EXTRA_SHOP_ID);
//
//		ParseQuery<Shop> query = ParseQuery.getQuery("SHOP");
//		query.getInBackground(shopId, new GetCallback<Shop>() {
//			public void done(Shop shop, ParseException e) {
//				if (e == null) {
//					setupViews(shop);
//				} else {
//					//					objectRetrievalFailed();
//					Log.e("ITEM_ACTIIVTY", "Failed to load object.");
//				}
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shop, menu);
		return true;
	}

	protected void setupViews(Shop shop) {

		TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
		nameTextView.setText(shop.getName());

		ParseImageView imageView = (ParseImageView) findViewById(R.id.photoParseImageView);

		ParseFile photoFile = shop.getPhotoFile();
		if (photoFile != null) {
			imageView.setParseFile(photoFile);
			imageView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}
	}

}
