package il.ac.huji.shoppit;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity {

	String[] categories = new String[] { "Children", "Clothes", "Cosmetics", "Electronics", "Food",
			"Home", "Jewelry", "Office", "Pets", "Travel", "Vehicle", "Mics."};


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		//Create the category list (there are two lists)

		final ListView listview1 = (ListView) findViewById(R.id.list1),
				listview2 = (ListView) findViewById(R.id.list2);

		final ArrayList<String> list1 = new ArrayList<String>(),
				list2 = new ArrayList<String>();
		for (int i = 0; i < categories.length; ++i) {
			(i < categories.length/2 ? list1 : list2).add(categories[i]);
		}

		listview1.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list1));
		listview2.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list2));

		//Create on-click listeners

		listview1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				handleClick(position);
			}
		});

		listview2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				handleClick( categories.length/2 + position );
			}
		});

		//Create on click listener for the Add button
		((Button)findViewById(R.id.add)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), TakePictureActivity.class);
				startActivity(intent);
			}
		});

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	/**
	 * Handle clicking a category.
	 * @param pos the position of the category in the array of categories.
	 */
	private void handleClick(int pos) {
		//TODO
	}

}
