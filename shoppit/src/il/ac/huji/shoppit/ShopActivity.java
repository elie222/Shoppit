package il.ac.huji.shoppit;

import java.util.HashMap;

import com.parse.CountCallback;
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
 * This class handles displaying shops.
 */
public class ShopActivity extends Activity implements CommentDialogFragment.CommentDialogListener {

	private ShareActionProvider mShareActionProvider;

	private Shop mShop;

	private TextView nameTextView;
	private CheckBox likeCheckBox;
	private TextView likesCountTextView;
	private Integer likesCount;
	private ParseImageView imageView;
	private ListView commentsListView;
	private Button addCommentButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);

		// add up caret to top let hand corner of screen
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// The placeholder will be used before and during the fetch, to be replaced by the fetched image
		// data.
		//		ParseImageView imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		//		imageView.setPlaceholder(getResources().getDrawable(R.drawable.placeholder));

		mShop = GeneralInfo.shopHolder;

		nameTextView = (TextView) findViewById(R.id.nameTextView);
		likeCheckBox = (CheckBox) findViewById(R.id.likeCheckBox);
		likesCountTextView = (TextView) findViewById(R.id.likesCountTextView);
		imageView = (ParseImageView) findViewById(R.id.photoParseImageView);
		commentsListView = (ListView) findViewById(R.id.commentsListView);
		addCommentButton = (Button) findViewById(R.id.addCommentButton);

		likeCheckBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO - disabled liking for now. think i need to change the CloudCode to work with shops too.
//				if (likeCheckBox.isChecked()) {
//					likesCount++;
//				} else {
//					likesCount--;
//				}
//				likesCountTextView.setText(likesCount.toString());
//
//				HashMap<String, Object> params = new HashMap<String, Object>();
//				params.put("shopId", mShop.getObjectId());
//				params.put("like", likeCheckBox.isChecked());
//
//				ParseCloud.callFunctionInBackground("likeShop", params, new FunctionCallback<String>() {
//					public void done(String message, ParseException e) {
//						if (e == null) {
//							Log.i("SHOP_ACTIVITY", message);
//						} else {
//							Log.e("SHOP_ACTIVITY", "ERROR MESSAGE: " + e.getMessage());
//						}
//					}
//				});
			}
		});

		addCommentButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO - commented out for now so as not to get Shop and Item comments mixed up.
				//				FragmentManager fm = getFragmentManager();
				//				CommentDialogFragment commentDialog = new CommentDialogFragment();
				//				commentDialog.setRetainInstance(true);
				//				commentDialog.show(fm, "comment_dialog_fragment");
			}

		});

		setupViewsWithShopData();

	}


	/**
	 * This function setups the views on the page with info from the Shop.
	 */
	protected void setupViewsWithShopData() {

		setTitle(mShop.getName());

		nameTextView.setText(mShop.getName());

		// get the number of users that like the shop
		ParseQuery<ParseUser> queryLikesCount = mShop.getLikesRelation().getQuery();
		queryLikesCount.countInBackground(new CountCallback() {
			public void done(int count, ParseException e) {
				if (e == null) {
					likesCount = count;
					likesCountTextView.setText(likesCount.toString());
				} else {
					Log.e("LIKE_CHECK_BOX",  "error2 with the likesCount query: " + e.getMessage());
				}
			}
		});

		// check if current user has liked the object
		ParseUser user = ParseUser.getCurrentUser();

		if (user!= null) {
			ParseQuery<ParseUser> queryUserLikes = mShop.getLikesRelation().getQuery();
			queryUserLikes.whereEqualTo("objectId", user.get("objectId"));

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
		}

		ParseFile photoFile = mShop.getPhotoFile();
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
		getMenuInflater().inflate(R.menu.shop, menu);
		MenuItem shop = menu.findItem(R.id.menu_item_share);
		mShareActionProvider = (ShareActionProvider) shop.getActionProvider();

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "I really like this shop on Shoppit: "
				+ mShop.getName() + "!");
		sendIntent.setType("text/plain");

		mShareActionProvider.setShareIntent(sendIntent);

		return true;
	}

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
				query.whereEqualTo("shop", mShop);
				query.orderByAscending("createdAt");

				return query;
			}
		};

		ParseQueryAdapter<Comment> adapter = new ParseQueryAdapter<Comment>(this, queryFactory);
		adapter.setTextKey("comment"); // TODO - show comment author too

		commentsListView.setAdapter(adapter);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	public Shop getShop() {
		return mShop;
	}

}
