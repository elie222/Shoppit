package il.ac.huji.shoppit;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQueryAdapter;

public class CommentAdapter extends ParseQueryAdapter<Comment> {
	
	private final static String TAG = "CommentAdapter";
	
	private Context mContext;

	private TextView commentTextView;
	private TextView authorTextView;

	public CommentAdapter(Context context, ParseQueryAdapter.QueryFactory<Comment> queryFactory) {
		super(context, queryFactory);
		
		mContext = context;
	}

	@Override
	public View getItemView(final Comment comment, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.comment_list, null);
		}

		super.getItemView(comment, v, parent);
	
		commentTextView = (TextView) v.findViewById(R.id.text1);
		authorTextView = (TextView) v.findViewById(R.id.authorTextView);
		
		commentTextView.setText(comment.getComment());	
		
		try {
			authorTextView.setText(comment.getAuthor().fetchIfNeeded().getUsername());
		} catch (ParseException e) {
			e.printStackTrace();
		}		

		return v;
	}

}