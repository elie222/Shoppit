package il.ac.huji.shoppit;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import android.os.Bundle;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class SearchableActivity extends ListActivity {

	private ItemAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	
		// Get the intent, verify the action and get the query
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			final String queryString = intent.getStringExtra(SearchManager.QUERY);
			
			setTitle("Search results for: \"" + queryString + "\"");
			
			ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
				public ParseQuery<Item> create() {
					ParseQuery<Item> query = new ParseQuery<Item>("Item");
					query.whereContains("searchString", queryString.toLowerCase());
					
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
//		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}


}
