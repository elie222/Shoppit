package il.ac.huji.shoppit;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class ParseTableActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parse_table);
		
//		ParseObject.registerSubclass(Item.class);
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli");
		
		//creating some objects to test with and saving them to the server.
//		for (int i=0; i<20; i++) {
//			ParseObject testObject = new ParseObject("TestObject");
//			testObject.put("name", "bar " + i);
//			testObject.saveInBackground();
//		}
		
		ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, "TestObject");
		adapter.setTextKey("name");
//		adapter.setImageKey("image");
		 
		ListView listView = (ListView) findViewById(R.id.homeListView);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.parse_table, menu);
		return true;
	}

}
