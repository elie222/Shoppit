package il.ac.huji.shoppit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;


public class SearchActivity extends ActionBarActivity {

	String[] categories = GeneralInfo.categories;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		//Create the category list

		final ListView listview = (ListView) findViewById(R.id.categList);

		listview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, categories));

		//Create on-click listeners

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				handleClick(position);
			}
		});

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_activity_actions, menu);
		return true;
	}


	/**
	 * Handle clicking a category.
	 * @param pos the position of the category in the array of categories.
	 */
	private void handleClick(int pos) {
		//TODO
	}


	//Clicking the action bar.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_userinfo:
			startActivity(new Intent(getBaseContext(), GeneralInfo.logged ? InfoActivity.class : LoginActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
