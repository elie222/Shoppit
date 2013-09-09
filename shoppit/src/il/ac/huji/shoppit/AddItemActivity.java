package il.ac.huji.shoppit;

import java.text.DecimalFormat;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddItemActivity extends ActionBarActivity {

	Spinner categ1, categ2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_item);

		ParseObject.registerSubclass(Item.class);
		Parse.initialize(this, "jAcoqyTFZ83HhbvfAaGQUe9hcu8lf0IOhyyYVKj5", "6gYN5nmVPMPpwyL0qNLOJbqShosYV0JR7Owp2Oli");

		categ1 = (Spinner) findViewById(R.id.categ1);
		categ2 = (Spinner) findViewById(R.id.categ2);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item, GeneralInfo.categories);
		categ1.setAdapter(adapter);
		categ1.setSelection(GeneralInfo.categories.length-1);

		categ1.setOnItemSelectedListener(new Categ1Selection());

		categ2.setEnabled(false);

		((EditText)findViewById(R.id.editText1)).requestFocus();

		// I replaced this line
//		((ImageView) findViewById(R.id.itemPic)).setImageBitmap(GeneralInfo.itemImage);
		Bitmap itemImageBitmap = BitmapFactory.decodeByteArray(GeneralInfo.itemImageData, 0, GeneralInfo.itemImageData.length);;
		((ImageView) findViewById(R.id.itemPic)).setImageBitmap(itemImageBitmap);


		//Create on click listener for the done button
		((Button)findViewById(R.id.done)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String name = ((EditText)findViewById(R.id.editText1)).getText().toString().trim().replaceAll("[ \t]+", " ");
				String price = ((EditText)findViewById(R.id.editText2)).getText().toString();

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
				
				
				GeneralInfo.name = name;
				GeneralInfo.price = new DecimalFormat("0.00").format(priceVal); //Give the price this format
				

				//Try to get the device location
				GeneralInfo.stopGettingLocation();
				if (GeneralInfo.location == null) {
					Toast.makeText(getApplicationContext(), "Cannot get device location",
							Toast.LENGTH_LONG).show();
					return;
				}

				
				//Data is OK, upload the item to parse.

				((Button) findViewById(R.id.done)).setEnabled(false);
				
				//Split the keywords
				
				
				Item newItem = new Item();
				newItem.setName(name);
				newItem.setPrice(Double.parseDouble(price));
				newItem.setCurrency("NIS");// TODO
				newItem.setAuthor(ParseUser.getCurrentUser());
				
				ParseGeoPoint point = new ParseGeoPoint(GeneralInfo.location.getLatitude(),
						GeneralInfo.location.getLongitude());
				newItem.setLocation(point);
				
				ParseFile photoFile = new ParseFile("photo.jpg", GeneralInfo.itemImageData);
				newItem.setPhotoFile(photoFile);
				
				// everyone can read the item, only the current user can edit it.
				// will write a cloud code function to enable other users to like the object.
				ParseACL itemACL = new ParseACL(ParseUser.getCurrentUser());
				itemACL.setPublicReadAccess(true);
				newItem.setACL(itemACL);
				newItem.saveInBackground();

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
				categ2.setAdapter(null);
			}
			else {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_spinner_item, GeneralInfo.subCateg[pos]);
				categ2.setAdapter(adapter);
				categ2.setSelection(GeneralInfo.subCateg[pos].length - 1);
			}

			categ2.setEnabled(!lastCategSelected);
		}

		public void onNothingSelected(AdapterView<?> parent) {}
	}
	


}
