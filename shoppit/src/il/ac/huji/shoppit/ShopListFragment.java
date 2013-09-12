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

public class ShopListFragment extends Fragment {

	// FOR DEBUGGING
	protected static final String TAG = "SHOP_LIST_FRAG";

	private ShopAdapter adapter;

	public ShopListFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_shop_list, container, false);

		ParseQueryAdapter.QueryFactory<Shop> queryFactory = new ParseQueryAdapter.QueryFactory<Shop>() {
			public ParseQuery<Shop> create() {				

				ParseQuery<Shop> query = new ParseQuery<Shop>("Shop");

				if (GeneralInfo.location != null) {
					ParseGeoPoint userLocation = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
							GeneralInfo.location.getLongitude());
					query.whereNear("location", userLocation);
				}

				return query;
			}
		};

		adapter = new ShopAdapter(getActivity(), queryFactory);
		ListView listView = (ListView) rootView.findViewById(R.id.shopsListView);
		listView.setAdapter(adapter);
		getActivity().setTitle("Shops");// TODO - put in res/values/strings.xml

		return rootView;
	}
}