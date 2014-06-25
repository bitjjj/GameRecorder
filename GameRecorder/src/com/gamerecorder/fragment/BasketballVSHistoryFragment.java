package com.gamerecorder.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gamerecorder.activity.R;
import com.gamerecorder.activity.VSHistoryDetailsActivity;
import com.gamerecorder.events.TeamVSHistoryChangeEvent;
import com.gamerecorder.interfaces.Identity;
import com.gamerecorder.interfaces.ListViewDelSelectedItemCallback;
import com.gamerecorder.model.Game;
import com.gamerecorder.util.Constants;
import com.gamerecorder.util.FileUtil;
import com.gamerecorder.widget.ListViewActionMode;
import com.gamerecorder.widget.PinnedSectionListView;
import com.gamerecorder.widget.PinnedSectionListView.PinnedSectionListAdapter;

import de.greenrobot.event.EventBus;

public class BasketballVSHistoryFragment extends Fragment implements ListViewDelSelectedItemCallback {

	private final static String TAG = "BasketballVSHistoryFragment";
	private PinnedSectionListView listView;
	private SectionListSimpleAdapter adapter;
	private List<Item> items;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
			Locale.getDefault());
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		EventBus.getDefault().register(this);  
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_basketball_vs_history,
				container, false);
		
		listView = (PinnedSectionListView) v
				.findViewById(R.id.team_vs_history_list);
		items = new ArrayList<BasketballVSHistoryFragment.Item>();
		adapter = new SectionListSimpleAdapter(getActivity(), items);
		
		listView.setAdapter(adapter);
		listView.setEmptyView(v.findViewById(R.id.team_vs_history_list_empty));
		
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,long id) {
				
				if(view instanceof LinearLayout && ((LinearLayout)view).getTag() != null){
					Log.d(TAG, ((LinearLayout)view).getTag().toString());
					
					Intent intent = new Intent();
				    intent.putExtra(Constants.GAME_ID, ((LinearLayout)view).getTag().toString());
				    intent.setClass(getActivity(), VSHistoryDetailsActivity.class);
				    startActivity(intent);
				}
				
			}
		});
		listView.setMultiChoiceModeListener(new ListViewActionMode<Item>(getActivity(), listView, adapter,this));

		return v;
	}

	public void onEvent(TeamVSHistoryChangeEvent event) {
		new LoadHistoryFileAsyncTask(getActivity(),event.getTeams()).execute();
	}

	private class SectionListSimpleAdapter extends ArrayAdapter<Item> implements
			PinnedSectionListAdapter {

		protected LayoutInflater mInflater;
		private static final int mResource = R.layout.team_vs_history_list_item;

		public SectionListSimpleAdapter(Context context, List<Item> items) {
			super(context, mResource, items);

			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout layout = null,dateLayout,dataLayout;
			
			Item item = getItem(position);
			if (convertView == null) {
				layout = (LinearLayout) mInflater.inflate(mResource, null);
			}
			else{
				layout = (LinearLayout)convertView;
			}
			
			dateLayout = (LinearLayout)layout.findViewById(R.id.date_label_container);
			dataLayout = (LinearLayout)layout.findViewById(R.id.data_label_container);
			
			if(item.getType() == Item.SECTION){
				dateLayout.setVisibility(View.VISIBLE);
				dataLayout.setVisibility(View.GONE);
				
				((TextView)dateLayout.findViewById(R.id.start_date)).setText(item.getStartDate());
			}
			else{
				dateLayout.setVisibility(View.GONE);
				dataLayout.setVisibility(View.VISIBLE);
				
				layout.setTag(item.getId());
				
				((TextView)dataLayout.findViewById(R.id.start_time)).setText(item.getStartTime());
				((TextView)dataLayout.findViewById(R.id.end_time)).setText(item.getEndTime());
				((TextView)dataLayout.findViewById(R.id.data)).setText(item.getDesc());
			}
			
			
			return layout;

		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).getType();
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}

	}

	private class Item implements Identity{

		public static final int ITEM = 0;
		public static final int SECTION = 1;

		private int type,id;

		private String desc;
		
		private Date start, end;

		public int getId() {
			return id;
		}
		
		public String getStartTime() {
			return sdf.format(start).split(" ")[1];
		}

		public String getEndTime() {
			return sdf.format(end).split(" ")[1];
		}

		public String getStartDate() {
			return sdf.format(start).split(" ")[0];
		}

		public String getDesc() {
			return desc;
		}
		
		public int getType() {
			return type;
		}

		public Item(int type, Date start) {
			this.type = type;
			this.start = start;
		}
		
		public Item(int type, int id,String desc, Date start, Date end) {
			this.type = type;
			this.id = id;
			this.desc = desc;
			this.start = start;
			this.end = end;
		}

		@Override
		public String toString() {
			return desc + "(" + start + ")";
		}

	}
	
	private class LoadHistoryFileAsyncTask extends AsyncTask<Void, Void, List<Object>>{

		private Context ctx = null;
		private ProgressDialog proDialog;
		private String [] teams;
		
		private LoadHistoryFileAsyncTask(Context ctx,String[] teams){
			this.ctx = ctx;
			this.teams = teams;
		}
		
		@Override
		protected void onPreExecute() {
			proDialog = ProgressDialog.show(this.ctx, 
					this.ctx.getResources().getString(R.string.vs_history_loading_title), this.ctx.getResources().getString(R.string.vs_history_loading_message));
		}

		
		@Override
		protected List<Object> doInBackground(Void... params) {
			return FileUtil.getGameHistoryFile(this.ctx);
		}
		
		@Override
		protected void onPostExecute(List<Object> history) {
			
			Set<String> startDateSet = new HashSet<String>();

			Iterator<Object> iter = history.iterator();
			while(iter.hasNext()){
				Game gm = (Game)iter.next();
				if(!gm.isGamingTeams(this.teams)){
					iter.remove();
				}
			}
			
			Collections.sort(history, new Comparator<Object>() {

				@Override
				public int compare(Object item1, Object item2) {
					Game gm1 = (Game)item1;
					Game gm2 = (Game)item2;
					
					return gm1.getStartDate().compareTo(gm2.getStartDate());
				}
			});
			                      
			items.clear();
			for(Object record:history){
				Game gm = (Game)record;
				Item item;
				String startDate = sdf.format(gm.getStartDate()).split(" ")[0];
				
				if(!startDateSet.contains(startDate)){
					item = new Item(Item.SECTION,gm.getStartDate());
					items.add(item);

					startDateSet.add(startDate);
				}
				
				item = new Item(Item.ITEM,99999999, gm.getHistoryRecordDesc(), gm.getStartDate(), gm.getEndDate());
				items.add(item);

			}
			
			//remove the same team record
			if(new HashSet<String>(Arrays.asList(teams)).size() == 1)items.clear();
			
			adapter.notifyDataSetChanged();
			
			proDialog.dismiss();
		}
		
	}

	@Override
	public void deleteSelectedItems(List<Identity> selectedIndexes) {
		
	}
	
	@Override  
    public void onStop() { 
        super.onStop();  
        EventBus.getDefault().unregister(this);  
    }  
}
