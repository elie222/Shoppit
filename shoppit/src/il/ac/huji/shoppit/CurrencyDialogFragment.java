package il.ac.huji.shoppit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;


public class CurrencyDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Welcome to Shoppit!\nPlease choose a currency")
		.setItems(R.array.pref_currency_entries, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// The 'which' argument contains the index position
				// of the selected item
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = sharedPrefs.edit();
				
				editor.putString("currency_key", getResources().getStringArray(R.array.pref_currency_values)[which]);
				editor.apply();
			}
		});
		return builder.create();
	}
}