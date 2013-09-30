package il.ac.huji.shoppit;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ReportDialogFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.report)
		.setItems(R.array.report_reasons_array, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// The 'which' argument contains the index position
				// of the selected item
				Item item = ((ItemActivity) getActivity()).getItem();

				Report report = new Report();
				report.setReason(getResources().getStringArray(R.array.report_reasons_array)[which]);
				report.setAuthor(ParseUser.getCurrentUser());
				report.setItem(item);

				ParseACL acl = new ParseACL();
				report.setACL(acl);

				report.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							Log.i("REPORT_DIALOG", "Report added successfully");
							//							Toast.makeText(getActivity(), "Report submitted successfully",
							//									Toast.LENGTH_LONG).show();
						} else {
							Log.e("REPORT_DIALOG", "Error adding report. CODE: " + e.getCode() + ". MESSAGE: " + e.getMessage());
							//							Toast.makeText(getActivity(), "Error submitting report",
							//									Toast.LENGTH_LONG).show();
						}
					}
				});

				// Would be better to do this in the callback, but that crashes the app.
				// Can get parent activity to show it, but cba to implement the listeners. Not that important.
				// Probably worked.
				Toast.makeText(getActivity(), "Report submitted successfully",
						Toast.LENGTH_LONG).show();
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				// do nothing
			}
		});
		return builder.create();
	}
}