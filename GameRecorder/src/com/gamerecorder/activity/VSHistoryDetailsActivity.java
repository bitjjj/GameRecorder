package com.gamerecorder.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.gamerecorder.model.Game;
import com.gamerecorder.task.LoadVSHistoryDataAsyncTask;
import com.gamerecorder.task.LoadVSHistoryDataAsyncTask.LoadVSHistoryDataCallback;
import com.gamerecorder.util.Constants;

public class VSHistoryDetailsActivity extends LeftSwipeBaseActivity implements LoadVSHistoryDataCallback{

	private List<Map<String, Object>> vsDetailsList;
	private SwipeListView vsDetailsSwipeListView;
	private SimpleAdapter vsDetailsListAdapter;
	private String gameId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_vs_history_details);
		
		getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS, R.layout.loading);  
	    setProgressBarIndeterminateVisibility(true);  
		
		setActionBar();
		
		vsDetailsList = new ArrayList<Map<String, Object>>();
		vsDetailsListAdapter = new SimpleAdapter(this, vsDetailsList,
				R.layout.team_members_list_item, new String[] { "text",
						"remove" }, new int[] { R.id.text, R.id.remove });
		vsDetailsListAdapter.setViewBinder(vsDetailsListAdapterBinder);
		
		vsDetailsSwipeListView = (SwipeListView) findViewById(R.id.vs_history_detail_list);
		vsDetailsSwipeListView.setAdapter(vsDetailsListAdapter);
		vsDetailsSwipeListView.setEmptyView(findViewById(R.id.vs_history_detail_list_empty));
		
		gameId = getIntent().getStringExtra(Constants.GAME_ID);
		
		new LoadVSHistoryDataAsyncTask(this,this).execute();
	}
	
	private void setActionBar() {
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.vs_history_details_label);

		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
	}
	
	private ViewBinder vsDetailsListAdapterBinder = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, final Object data,
				String textRepresentation) {
			switch (view.getId()) {
			case R.id.remove:
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Map<String, Object> removeItem = null;
						for (Map<String, Object> map : vsDetailsList) {
							if (map.get("remove").equals(data)) {
								removeItem = map;
								break;
							}
						}
						vsDetailsList.remove(removeItem);
						vsDetailsListAdapter.notifyDataSetChanged();
						vsDetailsSwipeListView.closeOpenedItems();

						//teamData.get(vsDetailsListAdapter.getItem(teamListPosition))
						//		.remove(removeItem.get("text"));
					}
				});
				break;
			case R.id.text:
				((TextView) view).setText((String) data);
				break;
			}
			return true;
		}
	};
	
	@Override
	public void success(Object result) {
		List<Object> history = (List<Object>)result;
		
		for(Object record:history){
			Game gm = (Game)record;
			
			if(gm.getId().equals(gameId)){
				
				Map<String, Object> map = new HashMap<String, Object>();
				//map.put("text", item);
				map.put("remove", Math.random());
				vsDetailsList.add(map);
			
			
				vsDetailsListAdapter.notifyDataSetChanged();
			}
		}
	}

}
