package com.wscompany.summary;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class IconfiedTextListAdapter extends BaseAdapter {

	private Context mContext;
	
	private List<IconfiedText> mItems = new ArrayList<IconfiedText>();
	
	public IconfiedTextListAdapter(Context context) {
		mContext = context;
	}
	
	public void addItem(IconfiedText it)
	{
		mItems.add(it);
	}
	
	public void setListItems(List<IconfiedText> lit)
	{
		mItems = lit;
	}
	
	
	public int getCount() {
		// TODO Auto-generated method stub
		return mItems.size();
	}

	
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mItems.get(position);
	}
	
	public boolean areAllLitemsSelectable()
	{
		return false;
	}
	
	public boolean isSelectable(int position) {
//		try {
			return mItems.get(position).isSelectable();
//		}
//		catch(IndexOutOfBoundsException aioobe) {
//			return super.isSelectable(position);
//		}
	}


	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		IconfiedTextView btv;
		if(convertView == null) {
			btv = new IconfiedTextView(mContext, mItems.get(position));
		}
		else {
			btv = (IconfiedTextView) convertView;
			btv.setText(mItems.get(position).getText());
			btv.setIcon(mItems.get(position).getIcon());
		}
		
		return btv;
	}

}
