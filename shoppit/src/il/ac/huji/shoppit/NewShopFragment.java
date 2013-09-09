package il.ac.huji.shoppit;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/*
 * This fragment manages the data entry for a
 * new Shop object. It lets the user input a 
 * shop name, give it a rating, and take a 
 * photo. If there is already a photo associated
 * with this shop, it will be displayed in the 
 * preview at the bottom, which is a standalone
 * ParseImageView.
 */
public class NewShopFragment extends Fragment {

	private ImageButton photoButton;
	private Button saveButton;
	private Button cancelButton;
	private TextView nameTextView;
	private byte[] photoData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle SavedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_new_shop, parent, false);

		nameTextView = ((EditText) v.findViewById(R.id.shop_name));

		photoButton = ((ImageButton) v.findViewById(R.id.photo_button));
		photoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(nameTextView.getWindowToken(), 0);
				startCamera();
			}
		});

		saveButton = ((Button) v.findViewById(R.id.save_button));
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO check data is valid (copy code from AddShopActivity)
				
				Shop shop = ((NewShopActivity) getActivity()).getCurrentShop();

				shop.setName(nameTextView.getText().toString());
				shop.setAuthor(ParseUser.getCurrentUser());

				ParseFile photoFile = new ParseFile("photo.jpg", photoData);
				shop.setPhotoFile(photoFile);

				ParseGeoPoint point = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
						GeneralInfo.location.getLongitude());
				shop.setLocation(point);

				// everyone can read the shop, only the current user can edit it.
				// TODO - write a cloud code function to enable other users to like the object.
				ParseACL shopACL = new ParseACL(ParseUser.getCurrentUser());
				shopACL.setPublicReadAccess(true);
				shop.setACL(shopACL);

				// Save the shop and return
				shop.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
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

		cancelButton = ((Button) v.findViewById(R.id.cancel_button));
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
			}
		});

		photoData = ((NewShopActivity) getActivity()).getCurrentPhotoData();

		Bitmap shopImageBitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
		((ImageView) v.findViewById(R.id.shop_preview_image)).setImageBitmap(shopImageBitmap);

		return v;
	}

	// just go back to the previous camera screen
	// TODO - need to save the data entered by the user so far
	// can do this if we don't pop the back stack, but instead push another camera fragment onto
	// the back stack
	public void startCamera() {
		FragmentManager fm = getActivity().getFragmentManager();
		fm.popBackStack("CameraFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

}