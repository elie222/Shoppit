package il.ac.huji.shoppit;

import java.text.DecimalFormat;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

// NOT USING THIS CLASS ANYMORE. HAS BUGS. USING NewItemActivity (with CameraFragment and NewItemFragment instead)
public class AddItemActivity extends ActionBarActivity {

	// TODO remove subcategory
	Spinner categorySpinner, subCategorySpinner;
	EditText keywordsEditText;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_item);

		categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		subCategorySpinner = (Spinner) findViewById(R.id.subCategorySpinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item, GeneralInfo.categories);
		categorySpinner.setAdapter(adapter);
		categorySpinner.setSelection(GeneralInfo.categories.length-1);

		categorySpinner.setOnItemSelectedListener(new Categ1Selection());

		subCategorySpinner.setEnabled(false);

		((EditText)findViewById(R.id.nameEditText)).requestFocus();

		// I replaced this line
		//		((ImageView) findViewById(R.id.itemPic)).setImageBitmap(GeneralInfo.itemImage);
		Bitmap itemImageBitmap = BitmapFactory.decodeByteArray
				(GeneralInfo.itemImageData, 0, GeneralInfo.itemImageData.length);
		((ImageView) findViewById(R.id.itemPic)).setImageBitmap(itemImageBitmap);
		
		keywordsEditText = (EditText) findViewById(R.id.keywordsEditText);


		//Create on click listener for the done button
		((Button)findViewById(R.id.doneButton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString().trim().replaceAll("[ \t]+", " ");
				String price = ((EditText)findViewById(R.id.priceEditText)).getText().toString();
				
				// TODO - check keywords format is valid
				String keywordsString = keywordsEditText.getText().toString();
				String [] keywords = keywordsString.split("\\s+");

				//Perform sanity checks

				if (name.length() == 0) {
					Toast.makeText(getApplicationContext(), "Please fill the item name",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (name.length() < 3) {
					Toast.makeText(getApplicationContext(), "Item name is too short",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (name.length() > 30) {
					Toast.makeText(getApplicationContext(), "Item name is too long",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (price.length() == 0) {
					Toast.makeText(getApplicationContext(), "Please fill the item price",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (price.length() > 6) {
					Toast.makeText(getApplicationContext(), "Item price is too long",
							Toast.LENGTH_LONG).show();
					return;
				}

				//Check there are no more than two digits after the decimal point.
				int decimalPos = price.indexOf(".");
				if (decimalPos >= 0 && price.substring(decimalPos + 1).length() > 2) {
					Toast.makeText(getApplicationContext(), "Item price is invalid",
							Toast.LENGTH_LONG).show();
					return;
				}

				//Check if the price is a valid number
				float priceVal = 0;
				try {
					priceVal = Float.parseFloat(price);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Item price is invalid",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (priceVal == 0) {
					Toast.makeText(getApplicationContext(), "Item price cannot be zero",
							Toast.LENGTH_LONG).show();
					return;
				}

				//TODO why are name and price being saved in GeneralInfo?
				//Can't remember now, I commented this out
				//GeneralInfo.name = name;
				//GeneralInfo.price = new DecimalFormat("0.00").format(priceVal); //Give the price this format

				//Try to get the device location
				GeneralInfo.stopGettingLocation();
				if (GeneralInfo.location == null) {
					Toast.makeText(getApplicationContext(), "Cannot get device location",
							Toast.LENGTH_LONG).show();
					return;
				}

				//Data is OK, upload the item to parse.

				((Button) findViewById(R.id.doneButton)).setEnabled(false);

				Item newItem = new Item();
				newItem.setName(name);
				newItem.setPrice(Double.parseDouble(price));
				newItem.setCurrency("NIS");// TODO
				newItem.setAuthor(ParseUser.getCurrentUser());
				newItem.setKeywords(keywords);

				// location
				ParseGeoPoint point = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
						GeneralInfo.location.getLongitude());
				newItem.setLocation(point);

				// photo
				ParseFile photoFile = new ParseFile("photo.jpg", GeneralInfo.itemImageData);
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
							Toast.makeText(getApplicationContext(), "Item added successfully",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getApplicationContext(), "Error adding item " + e.getCode() + " " + e.getMessage(),
									Toast.LENGTH_LONG).show();
						}
					}

				});

			}
		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	@Override
	protected void onPause() {
		super.onPause();
		GeneralInfo.stopGettingLocation();
	}



	//Spinner selection listeners


	public class Categ1Selection implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

			//If the "(Other)" category was selected, don't select a sub category.
			boolean lastCategSelected = (pos == GeneralInfo.categories.length - 1);
			if (lastCategSelected) {
				subCategorySpinner.setAdapter(null);
			}
			else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_spinner_item, GeneralInfo.subCateg[pos]);
				subCategorySpinner.setAdapter(adapter);
				subCategorySpinner.setSelection(GeneralInfo.subCateg[pos].length - 1);
			}

			subCategorySpinner.setEnabled(!lastCategSelected);
		}

		public void onNothingSelected(AdapterView<?> parent) {}
	}
	



}
