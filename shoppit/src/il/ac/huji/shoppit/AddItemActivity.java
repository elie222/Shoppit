package il.ac.huji.shoppit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddItemActivity extends ActionBarActivity {

	Spinner categ1, categ2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_item);

		categ1 = (Spinner) findViewById(R.id.categ1);
		categ2 = (Spinner) findViewById(R.id.categ2);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
				android.R.layout.simple_spinner_item, GeneralInfo.categories);
		categ1.setAdapter(adapter);
		categ1.setSelection(GeneralInfo.categories.length-1);

		categ1.setOnItemSelectedListener(new Categ1Selection());

		categ2.setEnabled(false);

		((EditText)findViewById(R.id.editText1)).requestFocus();

		((ImageView) findViewById(R.id.itemPic)).setImageBitmap(GeneralInfo.itemImage);


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

				((Button) findViewById(R.id.done)).setEnabled(false);
				
				//TODO data is okay, get GPS position and upload to parse.

			}
		});

	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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