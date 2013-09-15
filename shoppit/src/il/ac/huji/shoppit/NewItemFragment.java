package il.ac.huji.shoppit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
 * One of the two fragments used by NewItemActivity.
 * This fragment manages the data entry for a
 * new Item object. It lets the user input a 
 * item name and take a photo. If there is already a photo
 * associated with this item, it will be displayed in the 
 * preview at the bottom, which is a standalone
 * ParseImageView.
 */
public class NewItemFragment extends Fragment {

	//	private ImageButton photoButton;
	//	private Button saveButton;
	//	private Button cancelButton;

	private ImageView photoImageView;
	private EditText nameEditText;
	private EditText priceEditText;
	private Spinner currencySpinner;
	private Spinner categorySpinner;
	private EditText keywordsEditText;
	private Button doneButton;

	private byte[] photoData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_new_item, parent, false);

		photoImageView = ((ImageView) v.findViewById(R.id.photoImageView));
		nameEditText = ((EditText) v.findViewById(R.id.nameEditText));
		priceEditText = ((EditText) v.findViewById(R.id.priceEditText));
		currencySpinner = ((Spinner) v.findViewById(R.id.currencySpinner));
		categorySpinner = ((Spinner) v.findViewById(R.id.categorySpinner));
		keywordsEditText = ((EditText) v.findViewById(R.id.keywordsEditText));
		doneButton = ((Button) v.findViewById(R.id.doneButton));

		// set up currency spinner
		ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.currencies_array, android.R.layout.simple_spinner_item);
		currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencySpinner.setAdapter(currencyAdapter);

		// set up category spinner
		ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.categories_array, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);

		// set up done button
		doneButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addItem();
			}

		});




		//		photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
		//		photoButton.setOnClickListener(new View.OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				InputMethodManager imm = (InputMethodManager) getActivity()
		//						.getSystemService(Context.INPUT_METHOD_SERVICE);
		//				imm.hideSoftInputFromWindow(nameEditText.getWindowToken(), 0);
		//				startCamera();
		//			}
		//		});
		//
		//		saveButton = ((Button) v.findViewById(R.id.save_button));
		//		saveButton.setOnClickListener(new View.OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				// TODO check data is valid
		//
		//				Item item = ((NewItemActivity) getActivity()).getCurrentItem();
		//
		//				item.setName(nameEditText.getText().toString());
		//				item.setAuthor(ParseUser.getCurrentUser());
		//
		//				ParseFile photoFile = new ParseFile("photo.jpg", photoData);
		//				item.setPhotoFile(photoFile);
		//
		////				ParseGeoPoint point = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
		////						GeneralInfo.location.getLongitude());
		//				
		//				// TODO
		//				ParseGeoPoint point = new ParseGeoPoint();
		//				
		//				item.setLocation(point);
		//
		//				// everyone can read the item, only the current user can edit it.
		//				// TODO - write a cloud code function to enable other users to like the object.
		//				ParseACL itemACL = new ParseACL(ParseUser.getCurrentUser());
		//				itemACL.setPublicReadAccess(true);
		//				item.setACL(itemACL);
		//
		//				// Save the item and return
		//				item.saveInBackground(new SaveCallback() {
		//
		//					@Override
		//					public void done(ParseException e) {
		//						if (e == null) {
		//							Toast.makeText(
		//									getActivity().getApplicationContext(),
		//									"Successfully added item",
		//									Toast.LENGTH_SHORT).show();
		//							getActivity().setResult(Activity.RESULT_OK);
		//							getActivity().finish();
		//						} else {
		//							Toast.makeText(
		//									getActivity().getApplicationContext(),
		//									"Error saving: " + e.getMessage(),
		//									Toast.LENGTH_SHORT).show();
		//						}
		//					}
		//
		//				});
		//
		//			}
		//		});
		//
		//		cancelButton = ((Button) v.findViewById(R.id.cancel_button));
		//		cancelButton.setOnClickListener(new View.OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				getActivity().setResult(Activity.RESULT_CANCELED);
		//				getActivity().finish();
		//			}
		//		});
		//
		//		// Until the user has taken a photo, hide the preview
		//		itemPreview = (ImageView) v.findViewById(R.id.photoImageView);
		//		itemPreview.setVisibility(View.INVISIBLE);

		return v;
	}

	public void startCamera() {
		//		Fragment cameraFragment = new CameraFragment();
		//		FragmentTransaction transaction = getActivity().getFragmentManager()
		//				.beginTransaction();
		//		transaction.replace(R.id.fragmentContainer, cameraFragment);
		//		transaction.addToBackStack("NewItemFragment");
		//		transaction.commit();

		FragmentManager fm = getActivity().getFragmentManager();
		fm.popBackStack("NewCameraFragment",
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onResume() {
		super.onResume();

		photoData = ((NewItemActivity) getActivity()).getCurrentPhotoData();

		if (photoData != null) {
			Bitmap itemImageBitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
			photoImageView.setImageBitmap(itemImageBitmap);
		}
	}

	private void addItem() {

		String name = nameEditText.getText().toString().trim().replaceAll("[ \t]+", " ");
		String price = priceEditText.getText().toString();

		// TODO - check everything is okay with category, currency and keywords
		String currency = currencySpinner.getSelectedItem().toString();
		String category = categorySpinner.getSelectedItem().toString();

		String keywordsString = keywordsEditText.getText().toString();
		String [] keywords = keywordsString.split("\\s+");

		//Perform sanity checks

		if (name.length() == 0) {
			Toast.makeText(getActivity(), "Please fill the item name",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (name.length() < 3) {
			Toast.makeText(getActivity(), "Item name is too short",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (name.length() > 30) {
			Toast.makeText(getActivity(), "Item name is too long",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (price.length() == 0) {
			Toast.makeText(getActivity(), "Please fill the item price",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (price.length() > 6) {
			Toast.makeText(getActivity(), "Item price is too long",
					Toast.LENGTH_LONG).show();
			return;
		}

		//Check there are no more than two digits after the decimal point.
		int decimalPos = price.indexOf(".");
		if (decimalPos >= 0 && price.substring(decimalPos + 1).length() > 2) {
			Toast.makeText(getActivity(), "Item price is invalid",
					Toast.LENGTH_LONG).show();
			return;
		}

		//Check if the price is a valid number
		float priceVal = 0;
		try {
			priceVal = Float.parseFloat(price);
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Item price is invalid",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (priceVal == 0) {
			Toast.makeText(getActivity(), "Item price cannot be zero",
					Toast.LENGTH_LONG).show();
			return;
		}

		// Try to get the device location
		//		GeneralInfo.stopGettingLocation(); // TODO - this was crashing the app so I commented it out.
		if (GeneralInfo.location == null) {
			Toast.makeText(getActivity(), "Cannot get device location",
					Toast.LENGTH_LONG).show();
			return;
		}

		//Data is OK, upload the item to parse.

		doneButton.setEnabled(false);

		Item newItem = new Item();
		newItem.setName(name);
		newItem.setPrice(Double.parseDouble(price));
		newItem.setCurrency(currency);
		newItem.setAuthor(ParseUser.getCurrentUser());
		newItem.setMainCategory(category);
		newItem.setKeywords(keywords);

		// location
		ParseGeoPoint point = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
				GeneralInfo.location.getLongitude());
		newItem.setLocation(point);

		// photo
		ParseFile photoFile = new ParseFile("photo.jpg", photoData); // TODO items being uploaded rotated
		newItem.setPhotoFile(photoFile);

		// Permissions
		// everyone can read the item, only user that creates it can edit it.
		ParseACL itemACL = new ParseACL(ParseUser.getCurrentUser());
		itemACL.setPublicReadAccess(true);
		newItem.setACL(itemACL);

		newItem.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Toast.makeText(getActivity(), "Item added successfully",
							Toast.LENGTH_LONG).show();
					getActivity().setResult(Activity.RESULT_OK);
					getActivity().finish();
				} else {
					Toast.makeText(getActivity(), "Error adding item " + e.getCode() + " " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}

		});
	}

}