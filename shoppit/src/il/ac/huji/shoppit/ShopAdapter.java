package il.ac.huji.shoppit;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter;
import com.parse.ParseException;

public class ShopAdapter extends ParseQueryAdapter<Shop> {
		
	private Context _context;

	public ShopAdapter(Context context, ParseQueryAdapter.QueryFactory<Shop> queryFactory) {
		super(context, queryFactory);
		
		_context = context;
	}

	@Override
	public View getItemView(final Shop shop, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.shop_list, null);
		}

		super.getItemView(shop, v, parent);

		ParseImageView shopImage = (ParseImageView) v.findViewById(R.id.icon);
		ParseFile photoFile = shop.getPhotoFile();

		if (photoFile != null) {
			shopImage.setParseFile(photoFile);
			shopImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		TextView nameTextView = (TextView) v.findViewById(R.id.text1);
		nameTextView.setText(shop.getName());
		
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GeneralInfo.shopHolder = shop;
				Intent intent = new Intent(_context, ShopActivity.class);
				_context.startActivity(intent);
			}
		});

		return v;
	}

}