package il.ac.huji.shoppit;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
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
	
	public final static String EXTRA_ITEM_ID = "il.ac.huji.shoppit.ITEM_ID";
	
	private Context _context;

	public ItemAdapter(Context context, ParseQueryAdapter.QueryFactory<Item> queryFactory) {
		super(context, queryFactory);
		
		_context = context;
	}

	@Override
	public View getItemView(final Item item, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list, null);
		}

		super.getItemView(item, v, parent);

		ParseImageView itemImage = (ParseImageView) v.findViewById(R.id.icon);
		ParseFile photoFile = item.getPhotoFile();
//		ParseFile photoFile = item.getParseFile("photo");
		if (photoFile != null) {
			itemImage.setParseFile(photoFile);
			itemImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		TextView nameTextView = (TextView) v.findViewById(R.id.text1);
		nameTextView.setText(item.getName());
		
		TextView priceTextView = (TextView) v.findViewById(R.id.priceTextView);
		priceTextView.setText(item.getPrice());
		
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_context, ItemActivity.class);
				intent.putExtra(EXTRA_ITEM_ID, item.getObjectId());
				_context.startActivity(intent);
			}
		});

		return v;
	}

}