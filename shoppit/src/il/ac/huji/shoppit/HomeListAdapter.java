package il.ac.huji.shoppit;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

// TODO - in the middle of trying to get this working. We need to change how the items in the list view on the home screen
// are displayed.

public class HomeListAdapter extends ParseQueryAdapter<ParseObject> {

	public HomeListAdapter(Context context,
			com.parse.ParseQueryAdapter.QueryFactory<ParseObject> queryFactory) {
		super(context, queryFactory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getItemView(ParseObject object, View v, ViewGroup parent) {
	  if (v == null) {
	    v = View.inflate(getContext(), R.layout.adapter_item, null);
	  }
	 
	  // Take advantage of ParseQueryAdapter's getItemView logic for
	  // populating the main TextView/ImageView.
	  // The IDs in your custom layout must match what ParseQueryAdapter expects
	  // if it will be populating a TextView or ImageView for you.
	  super.getItemView(object, v, parent);
	 
	  return v;
	}
}
