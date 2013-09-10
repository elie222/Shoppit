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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CommentDialogFragment extends DialogFragment {

	private EditText commentEditText;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_comment_dialog, null);
		
		commentEditText = (EditText) view.findViewById(R.id.commentEditText);

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
		.setTitle(R.string.comment)
		.setPositiveButton(R.string.post, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				// post the comment
				String comment = commentEditText.getText().toString();
				
				Log.i("COMMENT", comment);
				
				Comment newComment = new Comment();
				newComment.setComment(comment);
				newComment.setAuthor(ParseUser.getCurrentUser());
				newComment.setItem(((ItemActivity) getActivity()).getItem());
				
				ParseACL acl = new ParseACL();
				acl.setPublicReadAccess(true);
				newComment.setACL(acl);
				
				newComment.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							Log.i("COMMENT_DIALOG", "Comment added successfully");
						} else {
							Log.e("COMMENT_DIALOG", "Error adding comment. CODE: " + e.getCode() + ". MESSAGE: " + e.getMessage());
						}
					}
				});
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				CommentDialogFragment.this.getDialog().cancel();
			}
		});      
		
		return builder.create();

	}

}
