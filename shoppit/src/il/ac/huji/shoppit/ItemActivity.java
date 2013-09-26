package il.ac.huji.shoppit;

import java.util.HashMap;
import java.util.List;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;


/**
 * @author Elie2
 * This class handles displaying items.
 */
public class ItemActivity extends Activity implements CommentDialogFragment.CommentDialogListener {

	//	public final static String EXTRA_ITEM_ID = "il.ac.huji.shoppit.ITEM_ID";
	private ShareActionProvider mShareActionProvider;

	private Item mItem;

	private TextView nameTextView;
	private TextView priceTextView;
	private TextView currencyTextView;
	private TextView categoryTextView;
	private CheckBox likeCheckBox;
	private TextView likesCountTextView;
	private Integer likesCount;
	private ParseImageView imageView;
	private ListView commentsListView;
	private Button addCommentButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);

		// enable up button (the caret in the top-left hand corner of the screen that allows going up an activity.
		// Has different functionality to the back button.)
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// The placeholder will be used before and during the fetch, to be replaced by the fetched image
		// data.
		//		ParseImageView imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		//		imageView.setPlaceholder(getResources().getDrawable(R.drawable.placeholder));

		mItem = GeneralInfo.itemHolder;

		nameTextView = (TextView) findViewById(R.id.nameTextView);
		priceTextView = (TextView) findViewById(R.id.priceTextView);
		currencyTextView = (TextView) findViewById(R.id.currencyTextView);
		categoryTextView = (TextView) findViewById(R.id.categoryTextView);
		likeCheckBox = (CheckBox) findViewById(R.id.likeCheckBox);
		likesCountTextView = (TextView) findViewById(R.id.likesCountTextView);
		imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		commentsListView = (ListView) findViewById(R.id.commentsListView);
		addCommentButton = (Button) findViewById(R.id.addCommentButton);

		likeCheckBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (likeCheckBox.isChecked()) {
					likesCount++;
				} else {
					likesCount--;
				}
				likesCountTextView.setText(likesCount.toString());

				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("itemId", mItem.getObjectId());
				params.put("like", likeCheckBox.isChecked());

				ParseCloud.callFunctionInBackground("likeItem", params, new FunctionCallback<String>() {
					public void done(String message, ParseException e) {
						if (e == null) {
							Log.i("ITEM_ACTIVITY", message);
						} else {
							Log.e("ITEM_ACTIVITY", "ERROR MESSAGE: " + e.getMessage());
						}
					}
				});
			}
		});

		addCommentButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				CommentDialogFragment commentDialog = new CommentDialogFragment();
				commentDialog.setRetainInstance(true);
				commentDialog.show(fm, "comment_dialog_fragment");
			}

		});

		setupViewsWithItemData();

	}


	/**
	 * This function setups the views on the page with info from the Item.
	 */
	protected void setupViewsWithItemData() {

		setTitle(mItem.getName());

		nameTextView.setText(mItem.getName());
		priceTextView.setText(String.valueOf(mItem.getPrice()));
		currencyTextView.setText(String.valueOf(mItem.getCurrency()));
		categoryTextView.setText(String.valueOf(mItem.getMainCategory()));

		likesCount = mItem.getLikesCount();
		likesCountTextView.setText(String.valueOf(mItem.getLikesCount()));
		// get the number of users that like the item
		//		ParseQuery<ParseUser> queryLikesCount = mItem.getLikesRelation().getQuery();
		//		queryLikesCount.countInBackground(new CountCallback() {
		//			public void done(int count, ParseException e) {
		//				if (e == null) {
		//					likesCount = count;
		//					likesCountTextView.setText(likesCount.toString());
		//				} else {
		//					Log.e("LIKE_CHECK_BOX",  "error2 with the likesCount query: " + e.getMessage());
		//				}
		//			}
		//		});

		// check if current user has liked the object
		ParseUser user = ParseUser.getCurrentUser();
				
		if (user!= null) {

			ParseQuery<ParseUser> queryUserLikes = mItem.getLikesRelation().getQuery();
			queryUserLikes.whereEqualTo("objectId", user.getObjectId());

			queryUserLikes.countInBackground(new CountCallback() {
				public void done(int count, ParseException e) {
					if (e == null) {
						if (count == 1) {
							likeCheckBox.setChecked(true);
						} else if (count == 0) {
							likeCheckBox.setChecked(false);
						} else {
							Log.e("LIKE_CHECK_BOX", "error1 with the likes query");
						}
					} else {
						Log.e("LIKE_CHECK_BOX",  "error2 with the likes query: " + e.getMessage());
					}
				}
			});

			// for debugging. remove afterwards
//			ParseQuery<ParseUser> debugQueryUserLikes = mItem.getLikesRelation().getQuery();
//
//			debugQueryUserLikes.findInBackground(new FindCallback<ParseUser>() {
//
//				@Override
//				public void done(List<ParseUser> users, ParseException e) {
//					for (int i=0; i<users.size(); i++) {
//						Log.d("ITEM_ACTIVITY", "Current user's id: " + ParseUser.getCurrentUser().getObjectId() +
//								". Users that like this object: " + users.get(i).getObjectId());
//
//						if (ParseUser.getCurrentUser().getObjectId().equals(users.get(i).getObjectId())) {
//							Log.d("ITEM_ACTIVITY", "User likes this item.");
//						} else {
//							Log.d("ITEM_ACTIVITY", "XXX");
//						}
//					}
//				}
//			});
			
		}

		imageView.setPlaceholder(getResources().getDrawable(R.drawable.placeholder));
		ParseFile photoFile = mItem.getPhotoFile();
		if (photoFile != null) {
			imageView.setParseFile(photoFile);
			imageView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		}

		// show comments
		reloadCommentsListView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.item, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "I really like this item on Shoppit: "
				+ mItem.getName() + ". And it only costs " + mItem.getPrice() + mItem.getCurrency() + "!");
		sendIntent.setType("text/plain");

		mShareActionProvider.setShareIntent(sendIntent);

		//		startActivity(sendIntent);

		return true;
	}

	// Call to update the share intent
	//	private void setShareIntent(Intent shareIntent) {
	//	    if (mShareActionProvider != null) {
	//	        mShareActionProvider.setShareIntent(shareIntent);
	//	    }
	//	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		reloadCommentsListView();
	}


	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
	}

	// TODO - try avoiding reloading all comments every time... ask question on Parse Forums
	// TODO - scroll down to bottom of comments list automatically
	private void reloadCommentsListView() {
		ParseQueryAdapter.QueryFactory<Comment> queryFactory = new ParseQueryAdapter.QueryFactory<Comment>() {
			public ParseQuery<Comment> create() {
				ParseQuery<Comment> query = new ParseQuery<Comment>("Comment");
				query.whereEqualTo("item", mItem);
				query.orderByAscending("createdAt");

				return query;
			}
		};

		CommentAdapter adapter = new CommentAdapter(this, queryFactory);

		commentsListView.setAdapter(adapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.menu_item_map:
			Intent intent = new Intent(getBaseContext(), MapActivity.class);

			if (mItem.getLocation() != null) {
				GeneralInfo.itemHolder = mItem;
				intent.putExtra(MapActivity.SHOW_ITEM_EXTRA, true);
				//				intent.putExtra(MapActivity.LAT_EXTRA, mItem.getLocation().getLatitude());
				//				intent.putExtra(MapActivity.LON_EXTRA, mItem.getLocation().getLongitude());
			}

			startActivity(intent);
			return true;
		case R.id.menu_item_report:
			FragmentManager fm = getFragmentManager();
			ReportDialogFragment reportDialog = new ReportDialogFragment();
			reportDialog.setRetainInstance(true);
			reportDialog.show(fm, "report_dialog_fragment");

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public Item getItem() {
		return mItem;
	}


}
