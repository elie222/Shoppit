package il.ac.huji.shoppit;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseException;
import com.parse.ParseQuery;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class ItemActivity extends Activity {

	public final static String EXTRA_ITEM_ID = "il.ac.huji.shoppit.ITEM_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
	
		// The placeholder will be used before and during the fetch, to be replaced by the fetched image
		// data.
		//		ParseImageView imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		//		imageView.setPlaceholder(getResources().getDrawable(R.drawable.placeholder));

		Intent intent = getIntent();
		String itemId = intent.getStringExtra(EXTRA_ITEM_ID);

		ParseQuery<Item> query = ParseQuery.getQuery("Item");
		query.getInBackground(itemId, new GetCallback<Item>() {
			public void done(Item item, ParseException e) {
				if (e == null) {
					setupViews(item);
				} else {
//					objectRetrievalFailed();
					Log.e("ITEM_ACTIIVTY", "Failed to load object.");
				}
			}
		});

	}

	protected void setupViews(Item item) {
		
		TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
		nameTextView.setText(item.getName());
		
		TextView priceTextView = (TextView) findViewById(R.id.priceTextView);
		priceTextView.setText(item.getPrice());
		
		TextView categoryTextView = (TextView) findViewById(R.id.categoryTextView);
		categoryTextView.setText(item.getMainCategory());
		
		ParseImageView imageView = (ParseImageView) findViewById(R.id.photoParseImageView);

		ParseFile photoFile = item.getParseFile("photo");
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item, menu);
		return true;
	}

}
