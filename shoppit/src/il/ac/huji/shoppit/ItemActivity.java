package il.ac.huji.shoppit;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import android.location.Location;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;


/**
 * @author Elie2
 * This class handles displaying items.
 */
public class ItemActivity extends Activity implements CommentDialogFragment.CommentDialogListener {

	//	public final static String EXTRA_ITEM_ID = "il.ac.huji.shoppit.ITEM_ID";
	private final static int ADD_COMMENT_REQUEST_CODE = 4000;
	private final static int LIKE_ITEM_REQUEST_CODE = 4001;
	private final static int REPORT_ITEM_REQUEST_CODE = 4002;

	private ShareActionProvider mShareActionProvider;

	private Item mItem;

	private TextView uploader;
	private TextView nameTextView;
	private TextView priceTextView;
	private TextView currencyTextView;
	private TextView categoryTextView;
	//private ImageView likeButton;
	private TextView likesCountTextView;
	private Integer likesCount;
	private ParseImageView imageView;
	private ListView commentsListView;
	private Button addCommentButton;
	private Boolean liked = null; //null means that this info has not yet been received.
	private TextView distance;


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

		uploader = (TextView) findViewById(R.id.uploader);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		priceTextView = (TextView) findViewById(R.id.priceTextView);
		currencyTextView = (TextView) findViewById(R.id.currencyTextView);
		categoryTextView = (TextView) findViewById(R.id.categoryTextView);
		//likeButton = (ImageView) findViewById(R.id.like_button);
		likesCountTextView = (TextView) findViewById(R.id.likesCountTextView);
		imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		commentsListView = (ListView) findViewById(R.id.commentsListView);
		addCommentButton = (Button) findViewById(R.id.addCommentButton);
		distance = (TextView) findViewById(R.id.dist);

//		likeButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (ParseUser.getCurrentUser() != null) {
//					likeItem();
//				} else {
//					Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
//					startActivityForResult(loginIntent, LIKE_ITEM_REQUEST_CODE);
//				}
//			}
//		});

		addCommentButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ParseUser.getCurrentUser() != null) {
					addComment();
				} else {
					Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
					startActivityForResult(loginIntent, ADD_COMMENT_REQUEST_CODE);
				}
			}

		});

		setupViewsWithItemData();

	}


	/**
	 * This function setups the views on the page with info from the Item.
	 */
	protected void setupViewsWithItemData() {

		setTitle(mItem.getName());

		//uploader.setText("Uploaded by: " + mItem.getAuthor().getUsername()); TODO
		nameTextView.setText(mItem.getName());
		priceTextView.setText(new DecimalFormat("0.00").format(mItem.getPrice()));
		currencyTextView.setText(String.valueOf(mItem.getCurrency()));
		categoryTextView.setText(String.valueOf(mItem.getMainCategory()));
		GeneralInfo.displayDistance(mItem.getLocation(), distance);

		likesCount = mItem.getLikesCount();
		likesCountTextView.setText("Likes: " + mItem.getLikesCount());
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
		checkIfUserLiked(false);

		//If user is not logged in, show the thumbs up icon.
		if (ParseUser.getCurrentUser() == null)
			//likeButton.setImageResource(R.drawable.rating_good);

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



	/**
	 * Check if this user has already liked this item and set the "liked" variable accordingly.
	 * @param likeWhenDone whether should simulate a click on the like button when done checking.
	 */
	private void checkIfUserLiked(final boolean likeWhenDone) {

		ParseUser user = ParseUser.getCurrentUser();
		if (user == null)
			return;

		ParseQuery<ParseUser> queryUserLikes = mItem.getLikesRelation().getQuery();
		queryUserLikes.whereEqualTo("objectId", user.getObjectId());

		queryUserLikes.countInBackground(new CountCallback() {
			public void done(int count, ParseException e) {
				if (e == null) {
					if (count == 1) {
						liked = true;
					} else if (count == 0) {
						liked = false;
					} else {
						Log.e("LIKE_CHECK_BOX", "error1 with the likes query");
					}
				} else {
					Log.e("LIKE_CHECK_BOX",  "error2 with the likes query: " + e.getMessage());
				}
				//likeButton.setImageResource(liked ? R.drawable.rating_bad : R.drawable.rating_good);

				if (likeWhenDone && !liked) {
					likeItem();
				}
			}
		});

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
			if (ParseUser.getCurrentUser() != null) {
				reportItem();
			} else {
				Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
				startActivityForResult(loginIntent, REPORT_ITEM_REQUEST_CODE);
			}

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void addComment() {
		FragmentManager fm = getFragmentManager();
		CommentDialogFragment commentDialog = new CommentDialogFragment();
		commentDialog.setRetainInstance(true);
		commentDialog.show(fm, "comment_dialog_fragment");
	}

	private void likeItem() {

		//Not yet known whether the user liked this item already or not.
		if (liked == null)
			return;

		liked = !liked;
		likesCount += (liked ? 1 : -1);
		likesCountTextView.setText("Likes: " + likesCount);
		//likeButton.setImageResource(liked ? R.drawable.rating_bad : R.drawable.rating_good);

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("itemId", mItem.getObjectId());
		params.put("like", liked);

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

	private void reportItem() {
		FragmentManager fm = getFragmentManager();
		ReportDialogFragment reportDialog = new ReportDialogFragment();
		reportDialog.setRetainInstance(true);
		reportDialog.show(fm, "report_dialog_fragment");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ADD_COMMENT_REQUEST_CODE:
				addComment();
				break;
			case LIKE_ITEM_REQUEST_CODE:
				checkIfUserLiked(true);
				break;
			case REPORT_ITEM_REQUEST_CODE:
				reportItem();
				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public Item getItem() {
		return mItem;
	}


}
