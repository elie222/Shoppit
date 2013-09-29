package il.ac.huji.shoppit;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

class NavDrawerAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private ArrayList<String> mData = new ArrayList<String>();
	private LayoutInflater mInflater;

	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

	public NavDrawerAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(final String item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void addItems(final String[] items) {
		for (int i=0; i<items.length; i++) {
			mData.add(items[i]);
		}
		notifyDataSetChanged();
	}

	public void addSeparatorItem(final String item) {
		mData.add(item);
		// save separator position
		mSeparatorsSet.add(mData.size() - 1);
		notifyDataSetChanged();
	}

	public int getSectionNumber(int position) {
		return mSeparatorsSet.headSet(position).size();
	}

	public String getSectionName(int position) {
		int index = mSeparatorsSet.floor(position);
		return mData.get(index);
	}
	
	public String getItemName(int position) {
		return mData.get(position);
	}
	
	public int getPositionInSection(int position) {
		int separatorIndex = mSeparatorsSet.floor(position);
		return position - separatorIndex - 1;
	}
	
	public int getPosition(String item) {
		return mData.indexOf(item);
	}

	public boolean isSeparator(int position) {
		if (mSeparatorsSet.contains(position)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public String getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);

		//		Log.i("navdrawer", "getView " + position + " " + convertView + " type = " + type);

		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case TYPE_ITEM:
				convertView = mInflater.inflate(R.layout.drawer_list_item, null);
				holder.textView = (TextView)convertView.findViewById(R.id.drawerListItem);
				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.drawer_list_section_header, null);
				holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		holder.textView.setText(mData.get(position));
		return convertView;
	}


	public class ViewHolder {
		public TextView textView;
	}

}

