package il.ac.huji.shoppit;

import android.os.Bundle;
import android.app.Activity;
import android.content.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity {

	ItemList nearbyItems;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Create the spinner that chooses how to sort nearby items.
		Spinner spinner = (Spinner) findViewById(R.id.sorter);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.sort_options, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new Sorter());

		//Create the nearby item list.
		nearbyItems = new ItemList(this);
		ListView list = (ListView)findViewById(R.id.itemList);
		list.setAdapter(nearbyItems.listAdapter);

		//Create on click listener for the Search button
		((Button)findViewById(R.id.search)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
				startActivity(intent);
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






	//This class implements sorting nearby items.
	private class Sorter extends Activity implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, 
				int pos, long id) {
			// pos: 0 = cheapest, 1 = closest, 2 = newest.
		}

		public void onNothingSelected(AdapterView<?> parent) {}
	}

}
