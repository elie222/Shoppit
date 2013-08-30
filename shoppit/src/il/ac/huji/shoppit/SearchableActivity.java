package il.ac.huji.shoppit;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

public class SearchableActivity extends ListActivity {

	private ItemAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.e("SEARCHABLE_ACT", "HELLLOO!");
		
		ParseObject.registerSubclass(Item.class);
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli");

		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			final String queryString = intent.getStringExtra(SearchManager.QUERY);
			
			ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
				public ParseQuery<Item> create() {
					ParseQuery query = new ParseQuery("Item");
					query.whereContains("name", queryString);
					
					return query;
				}
			};
			
			adapter = new ItemAdapter(this, queryFactory);
			
			setListAdapter(adapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

}
