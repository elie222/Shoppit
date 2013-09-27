package il.ac.huji.shoppit;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

	protected static final String TAG = "PROFILE_FRAG";

	private TextView usernameTextView;
	private Spinner spinner;
	private ListView listView;
	private ItemAdapter adapter;

	private int spinnerPosition;

	public ProfileFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

		usernameTextView = (TextView) rootView.findViewById(R.id.usernameTextView);
		spinner = (Spinner) rootView.findViewById(R.id.spinner);
		listView = (ListView) rootView.findViewById(R.id.listView);
		
		usernameTextView.setText(ParseUser.getCurrentUser().getUsername());

		// set up spinner
		ArrayAdapter<CharSequence> sortbyAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.profile_options_array, android.R.layout.simple_spinner_item);
		sortbyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(sortbyAdapter);

		spinner.setSelection(((MainActivity) getActivity()).getSortBy());

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				((MainActivity) getActivity()).setSortBy(position);
				spinnerPosition = position;
				loadItems();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}
		});

		loadItems();

		return rootView;
	}

	private void loadItems() {

		ParseQueryAdapter.QueryFactory<Item> queryFactory = new ParseQueryAdapter.QueryFactory<Item>() {
			public ParseQuery<Item> create() {				

				ParseQuery<Item> query = new ParseQuery<Item>("Item");
				
				query.orderByDescending("createdAt");

				switch (spinnerPosition) {
				case 0:
					query.whereEqualTo("author", ParseUser.getCurrentUser());
					break;
				case 1:
					query.whereEqualTo("likes", ParseUser.getCurrentUser());
					break;
				case 2:
					// i'll probs just remove this option... slightly difficuly query to do
					// TODO query - where item has a comment from this author
					query.whereEqualTo("author", ParseUser.getCurrentUser()); // this isn't the right query
					break;
				default:
					break;
				}

				return query;
			}
		};

		adapter = new ItemAdapter(getActivity(), queryFactory);
		listView.setAdapter(adapter);
		getActivity().setTitle("Profile");
	}

}