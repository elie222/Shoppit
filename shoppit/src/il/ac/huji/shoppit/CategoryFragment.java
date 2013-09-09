package il.ac.huji.shoppit;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CategoryFragment extends Fragment {
	public static final String ARG_CATEGORY_NUMBER = "category_number";
	public static final String MAIN_CATEGORY = "mainCategory";

	private ItemAdapter adapter;

	public CategoryFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_category, container, false);
		final int i = getArguments().getInt(ARG_CATEGORY_NUMBER);
		//		Log.i("CAT_FRAG", "cat no: " + i);

		final String category = getResources().getStringArray(R.array.categories_array)[i];

		ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
			public ParseQuery<Item> create() {
				ParseQuery<Item> query = new ParseQuery<Item>("Item");
				if (i != 0) {
					query.whereEqualTo(MAIN_CATEGORY, category);	
				}
				
				ParseGeoPoint userLocation = new ParseGeoPoint(37.708813, -122.526398); // TODO
//				ParseGeoPoint userLocation = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
//						GeneralInfo.location.getLongitude());
//
				query.whereNear("location", userLocation);
				// query.setLimit(100); //need this? no. default is 100

				return query;
			}
		};

		adapter = new ItemAdapter(getActivity(), queryFactory);

		ListView listView = (ListView) rootView.findViewById(R.id.homeListView);
		listView.setAdapter(adapter);

		getActivity().setTitle(category);

		return rootView;
	}
}