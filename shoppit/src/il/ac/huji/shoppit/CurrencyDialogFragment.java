package il.ac.huji.shoppit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CurrencyDialogFragment extends DialogFragment {

	private Spinner currencySelector;

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. */
	public interface CurrencyDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	CurrencyDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the CurrencyDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the CurrencyDialogListener so we can send events to the host
			mListener = (CurrencyDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement CurrencyDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_currency_dialog, null);

		// set up currency spinner
		currencySelector = (Spinner) view.findViewById(R.id.currency_selector);
		ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.currencies, android.R.layout.simple_spinner_item);
		currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currencySelector.setAdapter(currencyAdapter);
		
		SharedPreferences settings = getActivity().getSharedPreferences("Shoppit", 0);
		final SharedPreferences.Editor editor = settings.edit();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		.setTitle("Welcome to Shoppit!")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int id) {
				
				String selection = currencySelector.getSelectedItem().toString();
				
				//The currency text looks like this: "USD ($)
				
				String name = selection.substring(0,3),
						symbol = selection.substring(5,6);
				
				editor.putString("currencyName", name);
				editor.putString("currencySymbol", symbol);
				editor.commit();
				
				GeneralInfo.currencyName = name;
				GeneralInfo.currencySymbol = symbol;
				
			}
		});     

		return builder.create();

	}

}
