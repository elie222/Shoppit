package il.ac.huji.shoppit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * @author Elie2
 * One of the two fragments used by NewShopActivity.
 * This fragment manages the data entry for a
 * new Shop object. It lets the user input a 
 * shop name and take a photo. If there is already a photo
 * associated with this shop, it will be displayed in the 
 * preview at the bottom, which is a standalone
 * ParseImageView.
 */
public class NewShopFragment extends Fragment {

	private ImageView shopPreview;
	private EditText nameEditText;
	private EditText descriptionEditText;
	private Spinner locationSpinner;
	private ImageButton photoButton;
	private Button cancelButton;
	private Button saveButton;

	private byte[] photoData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle SavedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_new_shop, parent, false);

		shopPreview = (ImageView) v.findViewById(R.id.shop_preview_image);
		nameEditText = (EditText) v.findViewById(R.id.shopNameEditText);
		descriptionEditText = (EditText) v.findViewById(R.id.descriptionEditText);
		locationSpinner = (Spinner) v.findViewById(R.id.locationSpinner);
		photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
		cancelButton = ((Button) v.findViewById(R.id.cancel_button));
		saveButton = ((Button) v.findViewById(R.id.save_button));

		// Until the user has taken a photo, hide the preview
		//		shopPreview.setVisibility(View.INVISIBLE);

		// set up location spinner - has only one option atm ("Current location").
		ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.location_options_array, android.R.layout.simple_spinner_item);
		locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		locationSpinner.setAdapter(locationAdapter);
		locationSpinner.setEnabled(false); 

		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(descriptionEditText.getWindowToken(), 0);
				startCamera();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO check data is valid

				final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Adding shop...", true);
				
				String name = nameEditText.getText().toString();
				String description = descriptionEditText.getText().toString();
				
				if (name.length() == 0) {
					Toast.makeText(getActivity(), "Please enter the shop's name",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (name.length() < 3) {
					Toast.makeText(getActivity(), "Shop name is too short",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (name.length() > 30) {
					Toast.makeText(getActivity(), "Shop name is too long",
							Toast.LENGTH_LONG).show();
					return;
				}
				
				// no need to check shop description. Anything goes really.

				saveButton.setEnabled(false);

				Shop shop = ((NewShopActivity) getActivity()).getCurrentShop();

				shop.setName(name);
				shop.setDescription(description);
				shop.setAuthor(ParseUser.getCurrentUser());

				ParseFile photoFile = new ParseFile("photo.jpg", photoData);
				shop.setPhotoFile(photoFile);

				ParseGeoPoint point = new ParseGeoPoint(
						((NewShopActivity)getActivity()).getLatitude(),
						((NewShopActivity)getActivity()).getLongitude());
				shop.setLocation(point);

				// everyone can read the shop, only the current user can edit it.
				ParseACL shopACL = new ParseACL(ParseUser.getCurrentUser());
				shopACL.setPublicReadAccess(true);
				shop.setACL(shopACL);

				// Save the shop and return
				shop.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						progressDialog.dismiss();
						if (e == null) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Successfully added shop",
									Toast.LENGTH_SHORT).show();
							getActivity().setResult(Activity.RESULT_OK);
							getActivity().finish();
						} else {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Error saving: " + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}

				});

			}
		});

		return v;
	}

	public void startCamera() {
		Fragment cameraFragment = new CameraFragment();
		FragmentTransaction transaction = getActivity().getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.fragmentContainer, cameraFragment);
		transaction.addToBackStack("NewShopFragment");
		transaction.commit();
	}

	@Override
	public void onResume() {
		super.onResume();

		photoData = ((NewShopActivity) getActivity()).getCurrentPhotoData();

		if (photoData != null) {
			Bitmap shopImageBitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
			shopPreview.setImageBitmap(shopImageBitmap);
			photoButton.setVisibility(View.INVISIBLE);
		}
	}

}