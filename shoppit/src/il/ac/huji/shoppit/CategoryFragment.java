package il.ac.huji.shoppit;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class CategoryFragment extends Fragment
//implements RadioGroup.OnCheckedChangeListener 
{

	//	public static final String ARG_CATEGORY_NUMBER = "category_number";
	public static final String ARG_CATEGORY_NAME = "category_name";

	//	public static final String ARG_LATITUDE = "latitude";
	//	public static final String ARG_LONGITUDE = "longitude";
	public static final String MAIN_CATEGORY = "mainCategory";
	//	public static final String ALL = "All";

	// FOR DEBUGGING
	protected static final String TAG = "CAT_FRAG";

	private Spinner sortBySpinner;
	//private RadioGroup radioGroupSortBy;
	private ListView listView;

	private ItemAdapter adapter;

	//	private double latitude;
	//	private double longitude;
	private ParseGeoPoint currentLocation;

	private String category;

	//private int checkedRadioButtonId;
	private int selectedSortByPosition;

	public CategoryFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_category, container, false);
		//		final int i = getArguments().getInt(ARG_CATEGORY_NUMBER);
		final String categoryName = getArguments().getString(ARG_CATEGORY_NAME);

		sortBySpinner = (Spinner) rootView.findViewById(R.id.sortBySpinner);
		//radioGroupSortBy = (RadioGroup) rootView.findViewById(R.id.radioGroupSortBy);
		listView = (ListView) rootView.findViewById(R.id.homeListView);

		// get location from MainActivity
		double latitude = getArguments().getDouble(MainActivity.LATITUDE_EXTRA);
		double longitude = getArguments().getDouble(MainActivity.LONGITUDE_EXTRA);
		currentLocation = new ParseGeoPoint(latitude, longitude);

		category = categoryName;

		// set up sort by spinner
		ArrayAdapter<CharSequence> sortbyAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.sort_by_array, android.R.layout.simple_spinner_item);
		sortbyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortBySpinner.setAdapter(sortbyAdapter);
		
		sortBySpinner.setSelection(((MainActivity) getActivity()).getSortBy());
		
		sortBySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				((MainActivity) getActivity()).setSortBy(position);
				selectedSortByPosition = position;
				loadItems();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				
			}
		});
		
		//radioGroupSortBy.setOnCheckedChangeListener(this);

		//checkedRadioButtonId = radioGroupSortBy.getCheckedRadioButtonId();

		loadItems();

		return rootView;
	}

	private void loadItems() {

		ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
			public ParseQuery<Item> create() {				

				ParseQuery<Item> query = new ParseQuery<Item>("Item");

				//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				//query.whereEqualTo("currency", sharedPrefs.getString("currency_key", null));
				
				if (!category.equals(getResources().getString(R.string.all))) {
					query.whereEqualTo(MAIN_CATEGORY, category);
				}

				switch (selectedSortByPosition) {
				case 0:
					query.whereNear("location", currentLocation);
					query.whereWithinMiles("location", currentLocation, 100000);
					break;
				case 1:
					// TODO make sure the item is within a certain distance too, or not?
					// query.whereWithinMiles("location", currentLocation, 10);
					query.orderByAscending("price");
					break;
				case 2:
					query.orderByDescending("likesCount");
					break;
				default:
					break;
				}

				return query;
			}
		};

		adapter = new ItemAdapter(getActivity(), queryFactory);
		listView.setAdapter(adapter);
		getActivity().setTitle(category);
	}

//	@Override
//	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		checkedRadioButtonId = checkedId;
//		loadItems();
//	}
}