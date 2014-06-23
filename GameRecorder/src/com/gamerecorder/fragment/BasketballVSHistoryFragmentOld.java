package com.gamerecorder.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.gamerecorder.activity.R;
import com.gamerecorder.interfaces.onTeamVSHistoryLoaded;
import com.gamerecorder.model.Game;
import com.gamerecorder.util.FileUtil;

public class BasketballVSHistoryFragmentOld extends Fragment implements
		onTeamVSHistoryLoaded {

	private final static String TAG = "BasketballVSHistoryFragment";

	private List<Map<String, Object>> teamHistoryList;
	private SwipeListView teamHistoryrSwipeListView;
	private SimpleAdapter teamHistoryListAdapter;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
	
	public static BasketballVSHistoryFragmentOld newInstance(int position) {
		BasketballVSHistoryFragmentOld f = new BasketballVSHistoryFragmentOld();
		Bundle b = new Bundle();
		// b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_basketball_vs_history_old,
				container, false);

		teamHistoryList = new ArrayList<Map<String, Object>>();

		teamHistoryListAdapter = new SimpleAdapter(getActivity(),teamHistoryList,R.layout.team_vs_history_list_item_old, 
				new String[] { "text","remove","start_date","start_time","end_time" }, new int[] { R.id.text, R.id.remove,R.id.start_date,R.id.start_time,R.id.end_time });
		teamHistoryListAdapter.setViewBinder(teamHistoryListAdapterBinder);

		teamHistoryrSwipeListView = (SwipeListView) v.findViewById(R.id.team_vs_history_list);
		teamHistoryrSwipeListView.setAdapter(teamHistoryListAdapter);
		teamHistoryrSwipeListView.setEmptyView(v.findViewById(R.id.team_vs_history_list_empty));

		return v;
	}

	private ViewBinder teamHistoryListAdapterBinder = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, final Object data,
				String textRepresentation) {
			switch (view.getId()) {
			case R.id.remove:
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Map<String, Object> removeItem = null;
						for (Map<String, Object> map : teamHistoryList) {
							if (map.get("remove").equals(data)) {
								removeItem = map;
								break;
							}
						}
						teamHistoryList.remove(removeItem);
						teamHistoryListAdapter.notifyDataSetChanged();
						teamHistoryrSwipeListView.closeOpenedItems();

						
					}
				});
				break;
			case R.id.text:
			case R.id.start_date:
			case R.id.start_time:
			case R.id.end_time:
				((TextView) view).setText((String) data);
				break;
			}
			
			return true;
		}
	};

	@Override
	public void loadTeamVSHistoryList(String[] teams) {
		List<Object> history = FileUtil.getGameHistoryFile(getActivity());

		for(Object item:history){
			Game gm = (Game)item;
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", gm.getHistoryRecordDesc());
			map.put("start_date", sdf.format(gm.getStartDate()).split(" ")[0]);
			map.put("start_time", sdf.format(gm.getStartDate()).split(" ")[1]);
			map.put("end_time", sdf.format(gm.getEndDate()).split(" ")[1]);
			map.put("remove", Math.random());
			teamHistoryList.add(map);
		}
		
		teamHistoryListAdapter.notifyDataSetChanged();
		
		
		
		Log.d(TAG, history.toString());

	}
}
