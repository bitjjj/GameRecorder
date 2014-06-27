package com.gamerecorder.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.gamerecorder.adapter.AbstractPinnedSectionListAdapter;
import com.gamerecorder.adapter.AbstractPinnedSectionListAdapter.Item;
import com.gamerecorder.db.dao.GameResultDao;
import com.gamerecorder.db.dao.GameResultStatisticDao;
import com.gamerecorder.db.model.GameResult;
import com.gamerecorder.db.model.GameResultStatistic;
import com.gamerecorder.interfaces.Identity;
import com.gamerecorder.interfaces.ListViewDelSelectedItemCallback;
import com.gamerecorder.util.Constants;
import com.gamerecorder.util.DateUtil;
import com.gamerecorder.widget.ListViewActionMode;
import com.gamerecorder.widget.PinnedSectionListView;

public class VSHistoryDetailsActivity extends LeftSwipeBaseActivity implements ListViewDelSelectedItemCallback{

	
	@InjectView( R.id.vs_history_detail_list_left) PinnedSectionListView listViewLeft;
	@InjectView( R.id.vs_history_detail_list_right) PinnedSectionListView listViewRight;
	@InjectView(R.id.vs_history_detail_result) TextView resultTextView;
	@InjectView(R.id.vs_history_detail_date) TextView dateTextView;
	
	private int gameResultId;
	private GameResultDao resultDao;
	private GameResultStatisticDao resultStatDao;
	private SectionListSimpleAdapter adapterLeft,adapterRight;
	private List<Item> itemsLeft,itemsRight;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_vs_history_details);
		ButterKnife.inject(this);

		setActionBar();
		
		itemsLeft = new ArrayList<AbstractPinnedSectionListAdapter.Item>();
		adapterLeft = new SectionListSimpleAdapter(this, R.layout.team_vs_history_detail_list_item, itemsLeft);
		listViewLeft.setAdapter(adapterLeft);
		listViewLeft.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listViewLeft.setMultiChoiceModeListener(new ListViewActionMode<Item>(this, listViewLeft, adapterLeft,this));
		
		itemsRight = new ArrayList<AbstractPinnedSectionListAdapter.Item>();
		adapterRight = new SectionListSimpleAdapter(this, R.layout.team_vs_history_detail_list_item, itemsRight);
		listViewRight.setAdapter(adapterRight);
		listViewRight.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listViewRight.setMultiChoiceModeListener(new ListViewActionMode<Item>(this, listViewRight, adapterRight,this));
		
		gameResultId = getIntent().getIntExtra(Constants.GAME_RESULT_ID,-1);
		resultDao = new GameResultDao(this);
		resultStatDao = new GameResultStatisticDao(this);
		
		new LoadGameResultStatsAsyncTask(this).execute(gameResultId);
	}
	
	private void setActionBar() {
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.vs_history_details_label);

		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
	}
	
	private class LoadGameResultStatsAsyncTask extends AsyncTask<Integer, Void, LoadGameResultStatsAsyncTask.ResultObject>{

		private Context ctx = null;
		
		
		private LoadGameResultStatsAsyncTask(Context ctx){
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS, R.layout.loading);  
		    setProgressBarIndeterminateVisibility(true);  
		}

		
		@Override
		protected ResultObject doInBackground(Integer... params) {
			GameResult result = resultDao.queryById(params[0]);
			GameResultStatistic[] stats = result.getDetails().toArray(new GameResultStatistic[0]);
			Map<String, List<GameResultStatistic>> statTypeMap,statTypeLeftMap,statTypeRightMap;
			statTypeLeftMap = new HashMap<String, List<GameResultStatistic>>();
			statTypeRightMap = new HashMap<String, List<GameResultStatistic>>();
			
			for(GameResultStatistic stat : stats){
				if(stat.getResult().getTeamLeft().getId() == stat.getTeammember().getTeam().getId()){
					statTypeMap = statTypeLeftMap;
				}
				else{
					statTypeMap = statTypeRightMap;
				}
				
				if(!statTypeMap.containsKey(stat.getStatisticType())){
					statTypeMap.put(stat.getStatisticType(), new ArrayList<GameResultStatistic>());
				}
				
				statTypeMap.get(stat.getStatisticType()).add(stat);
			}
			
			Map[] results = {statTypeLeftMap,statTypeRightMap};
			for(int i = 0; i < results.length; i++){
				statTypeMap = (Map<String, List<GameResultStatistic>>)results[i];
				for(Map.Entry<String, List<GameResultStatistic>> entry : statTypeMap.entrySet()){
					Collections.sort(entry.getValue(), new Comparator<GameResultStatistic>() {
						@Override
						public int compare(GameResultStatistic stat1,GameResultStatistic stat2) {
							return stat1.getTime().compareTo(stat2.getTime());
						}
					});
				}
			}
			
			ResultObject resultObject = new ResultObject();
			resultObject.result = result;
			resultObject.statTypeLeftMap = statTypeLeftMap;
			resultObject.statTypeRightMap = statTypeRightMap;

			return resultObject;
		}
		
		@Override
		protected void onPostExecute(ResultObject resultObject) {
			dateTextView.setText(DateUtil.formatDate(resultObject.result.getStartDate()));
			resultTextView.setText(resultObject.result.getHistoryRecordDesc());
			
			addStats(itemsLeft,resultObject.statTypeLeftMap,adapterLeft);
			addStats(itemsRight,resultObject.statTypeRightMap,adapterRight);
			setProgressBarIndeterminateVisibility(false);
		}

		private void addStats(List<Item> items,Map<String, List<GameResultStatistic>> statTypeMap,SectionListSimpleAdapter adapter) {
			items.clear();
			
			for(Map.Entry<String, List<GameResultStatistic>> entry : statTypeMap.entrySet()){
				//entry
				items.add(new Item(Item.SECTION,entry.getKey(),entry.getValue().get(0).getTime()));
				for(GameResultStatistic stat : entry.getValue()){
					
					String desc;
					int score = stat.getScore();
					if(score == Constants.NOT_PTS){
						desc = stat.getStatisticType();
					}
					else{
						desc = String.valueOf(score) + ctx.getResources().getString(R.string.score_label);
					}
					
					String text = String.format(ctx.getResources().getString(R.string.stats_desc_detail),stat.getTeammemeberName(),desc);
					
					items.add(new Item(Item.ITEM, stat.getId(),text, stat.getStatisticType(),stat.getTime()));
				}
			}
			
			adapter.notifyDataSetChanged();
		}
		
		private class ResultObject{
			GameResult result;
			Map<String, List<GameResultStatistic>> statTypeLeftMap,statTypeRightMap;
		}
		
	}
	
	private class SectionListSimpleAdapter extends AbstractPinnedSectionListAdapter{

		public SectionListSimpleAdapter(Context context, int resource,List<Item> items){
			super(context, resource, items);
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
			
			if(item.getViewType() == Item.SECTION){
				dateLayout.setVisibility(View.VISIBLE);
				dataLayout.setVisibility(View.GONE);
				
				((TextView)dateLayout.findViewById(R.id.stat_type)).setText(item.getStatType());
			}
			else{
				dateLayout.setVisibility(View.GONE);
				dataLayout.setVisibility(View.VISIBLE);
				
				layout.setTag(item.getId());
				
				((TextView)dataLayout.findViewById(R.id.start_time)).setText(item.getStartTime());
				((TextView)dataLayout.findViewById(R.id.data)).setText(item.getDesc());
			}
			
			
			return layout;
		}
		
	}

	@Override
	public void deleteSelectedItems(List<Identity> selectedItems) {
		resultStatDao.delById(selectedItems);
	}

}
