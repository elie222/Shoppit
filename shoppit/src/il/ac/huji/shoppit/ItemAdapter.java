package il.ac.huji.shoppit;

import java.util.Arrays;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseException;

public class ItemAdapter extends ParseQueryAdapter<Item> {

	public ItemAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Item>() {
			public ParseQuery<Item> create() {
				// Here we can configure a ParseQuery to display
				// only top-rated meals.
				ParseQuery query = new ParseQuery("Item");
				//                query.whereContainedIn("rating", Arrays.asList("5", "4"));
				//                query.orderByDescending("rating");
				return query;
			}
		});
	}

	@Override
	public View getItemView(Item item, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list, null);
		}

		super.getItemView(item, v, parent);

		ParseImageView itemImage = (ParseImageView) v.findViewById(R.id.icon);
		ParseFile photoFile = item.getParseFile("photo");
		if (photoFile != null) {
			itemImage.setParseFile(photoFile);
			itemImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		TextView titleTextView = (TextView) v.findViewById(R.id.text1);
		titleTextView.setText(item.getName());
		TextView ratingTextView = (TextView) v.findViewById(R.id.favorite_meal_rating);
		ratingTextView.setText(item.getRating());
		return v;
	}
}