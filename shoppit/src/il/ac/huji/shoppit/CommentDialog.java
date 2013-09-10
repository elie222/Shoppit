package il.ac.huji.shoppit;

import android.os.Bundle;
import android.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CommentDialog extends DialogFragment implements OnEditorActionListener {

	public interface CommentDialogListener {
		void onFinishEditDialog(String inputText);
	}

	private EditText commentEditText;
	private Button postCommentButton;

	public CommentDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_comment_dialog, container);

		commentEditText = (EditText) view.findViewById(R.id.commentEditText);
//		postCommentButton = (Button) view.findViewById(R.id.postCommentButton);

		getDialog().setTitle("Comment");

		// Show soft keyboard automatically
		commentEditText.requestFocus();
		commentEditText.setOnEditorActionListener(this);

		postCommentButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Return input text to activity
				CommentDialogListener activity = (CommentDialogListener) getActivity();
				activity.onFinishEditDialog(commentEditText.getText().toString());
				dismiss();
			}

		});

		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			// Return input text to activity
			CommentDialogListener activity = (CommentDialogListener) getActivity();
			activity.onFinishEditDialog(commentEditText.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}
}