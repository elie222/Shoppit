package il.ac.huji.shoppit;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class CategoryFragment extends Fragment {

	public static final String ARG_CATEGORY_NUMBER = "category_number";
	public static final String MAIN_CATEGORY = "mainCategory";

	// FOR DEBUGGING
	protected static final String TAG = "CAT_FRAG";

	private ItemAdapter adapter;

	public CategoryFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_category, container, false);
		final int i = getArguments().getInt(ARG_CATEGORY_NUMBER);

		final String category = getResources().getStringArray(R.array.categories_array)[i];

		ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
			public ParseQuery<Item> create() {				

				ParseQuery<Item> query = new ParseQuery<Item>("Item");
				if (i != 0) {
					query.whereEqualTo(MAIN_CATEGORY, category);
				}

				if (GeneralInfo.location != null) {
					ParseGeoPoint userLocation = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
							GeneralInfo.location.getLongitude());
					query.whereNear("location", userLocation);
				}
				//query.setLimit(100); //need this?

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