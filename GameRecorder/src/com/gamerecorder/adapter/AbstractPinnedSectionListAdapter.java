package com.gamerecorder.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.gamerecorder.interfaces.Identity;
import com.gamerecorder.util.DateUtil;
import com.gamerecorder.widget.PinnedSectionListView;
import com.gamerecorder.widget.PinnedSectionListView.PinnedSectionListAdapter;

public abstract class AbstractPinnedSectionListAdapter extends ArrayAdapter<AbstractPinnedSectionListAdapter.Item> implements PinnedSectionListAdapter {
	
	protected LayoutInflater mInflater;
	protected int mResource ;

	public AbstractPinnedSectionListAdapter(Context context, int resource,List<Item> items) {
		super(context, resource, items);

		mResource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return getItem(position).getViewType();
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		return viewType == Item.SECTION;
	}
	
	
	public static class Item implements Identity{

		public static final int ITEM = 0;
		public static final int SECTION = 1;
		private int viewType,id;
		private String desc,statType;
		private Date start, end;
		
		public int getId() {
			return id;
		}
		
		public String getStatType() {
			return statType;
		}

		public void setStatType(String statType) {
			this.statType = statType;
		}
		
		public String getStartTime() {
			return DateUtil.formatDateTime(start).split(" ")[1];
		}

		public String getEndTime() {
			return DateUtil.formatDateTime(end).split(" ")[1];
		}

		public String getStartDate() {
			return DateUtil.formatDateTime(start).split(" ")[0];
		}

		public String getDesc() {
			return desc;
		}
		
		public int getViewType() {
			return viewType;
		}

		public Item(int type, Date start) {
			this.viewType = type;
			this.start = start;
		}
		
		public Item(int type, String statType,Date start) {
			this(type, start);
			this.statType = statType;
		}
		
		public Item(int type, int id,String desc, Date start, Date end) {
			this(type, start);
			this.id = id;
			this.desc = desc;
			this.end = end;
		}
		
		public Item(int type, int id,String desc, String statType,Date start) {
			this(type,id,desc,start,null);
			this.statType = statType;
		}

		@Override
		public String toString() {
			return desc + "(" + start + ")";
		}

	}



}
