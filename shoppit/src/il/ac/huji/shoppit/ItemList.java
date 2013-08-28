package il.ac.huji.shoppit;

import java.util.ArrayList;

import android.content.Context;
import android.view.*;
import android.widget.*;

/**
 * The list of nearby items.
 */
public class ItemList {


	ArrayList<ItemThumb> items = new ArrayList<ItemThumb>();
	SpecialAdapter listAdapter;
	
	
	public ItemList(Context context) {
		listAdapter = new SpecialAdapter(context, android.R.layout.simple_list_item_1, items);
	}
	
	
	public void sortByPrice() {
		//TODO
	}
	
	
	public void sortByDist() {
		//TODO
	}
	
	
	public void sortByDate() {
		//TODO
	}
	
	
	
	
	
	

	public class SpecialAdapter extends ArrayAdapter<ItemThumb> {

		public SpecialAdapter(Context context, int resource, ArrayList<ItemThumb> objects) {
			super(context, resource, objects);
		}

		public View getView(int pos, View convertView, ViewGroup parent) {

			ItemThumb item = getItem(pos);
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View view = inflater.inflate(R.layout.item_thumb, null);

			TextView name = (TextView)view.findViewById(R.id.name);
			name.setText(item.name);

			ImageView image = (ImageView)view.findViewById(R.id.image);
			image.setImageBitmap(item.image);

			return view;
		}

	}

}
