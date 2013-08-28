package il.ac.huji.shoppit;

import java.util.Arrays;

import android.content.Context;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class ItemAdapter extends ParseQueryAdapter<Item> {
    public ItemAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Item>() {
            public ParseQuery<Item> create() {
                // Here we can configure a ParseQuery to display
                // only top-rated meals.
                ParseQuery query = new ParseQuery("Item");
//                query.whereContainedIn("rating", Arrays.asList("5", "4"));
//                query.orderByDescending("rating");
                return query;
            }
        });
    }
}