package com.gamerecorder.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.gamerecorder.model.GameTeam;
import com.gamerecorder.model.GameTeammember;
import com.gamerecorder.util.Constants;
import com.gamerecorder.util.DatabaseHelper;
import com.gamerecorder.util.FileUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

public class SettingsActivity extends LeftSwipeBaseActivity {

	private final String TAG = "SettingsActivity";

	private Spinner teamListSpinner;
	private List<String> teamList;
	private ArrayAdapter<String> teamListAdapter;
	private int teamListPosition = -1;
	private ImageButton addTeamButton;
	private ImageButton deleteTeamButton;

	private EditText teammemberNameEdit;
	private ImageButton addTeammemeberButton;
	private List<Map<String, Object>> teammemberList;
	private SwipeListView teammemberSwipeListView;
	private SimpleAdapter teammemberListAdapter;

	//private Map<String, List<String>> teamData = new HashMap<String, List<String>>();
	
	private int currentTeamId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);

		setActionBar();

		handleTeamViewSection(savedInstanceState);

		handleTeammemberViewSection(savedInstanceState);

	}

	private void setActionBar() {
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle(R.string.set_teammebers);

		getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
	}

	private void handleTeamViewSection(Bundle savedInstanceState) {
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("teamListPosition")) {
			teamListPosition = savedInstanceState
					.getInt("teamListPosition", -1);
		}

		teamList = new ArrayList<String>();
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("teamList")) {
			String[] teamListItems = savedInstanceState
					.getStringArray("teamList");
			for (String item : teamListItems) {
				teamList.add(item);
			}
		}

		teamListAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, teamList);
		teamListAdapter.setDropDownViewResource(R.layout.dropdown_item);
		
		teamListSpinner = (Spinner) findViewById(R.id.team_list);
		teamListSpinner.setAdapter(teamListAdapter);
		teamListSpinner
				.setOnItemSelectedListener(teamListSpinnerItemSelectedListener);
		if (teamListPosition != -1) {
			teamListSpinner.setSelection(teamListPosition);
		}

		addTeamButton = (ImageButton) findViewById(R.id.add_team);
		addTeamButton.setOnClickListener(addTeamButtonClickListener);

		deleteTeamButton = (ImageButton) findViewById(R.id.delete_team);
		deleteTeamButton.setOnClickListener(deleteTeamButtonClickListener);
	}

	private void handleTeammemberViewSection(Bundle savedInstanceState) {

		teammemberNameEdit = (EditText) findViewById(R.id.teammember_name);

		addTeammemeberButton = (ImageButton) findViewById(R.id.add_teammember);
		addTeammemeberButton.setOnClickListener(addTeammemberButtonClickListener);

		if (savedInstanceState != null
				&& savedInstanceState.containsKey("teammemberList")) {
			teammemberList = (List) savedInstanceState.getParcelableArrayList("teammemberList");
		} else {
			teammemberList = new ArrayList<Map<String, Object>>();
		}

		teammemberListAdapter = new SimpleAdapter(this, teammemberList,
				R.layout.team_members_list_item, new String[] { "text",
						"remove" }, new int[] { R.id.text, R.id.remove });
		teammemberListAdapter.setViewBinder(teammemberListAdapterBinder);

		teammemberSwipeListView = (SwipeListView) findViewById(R.id.teammembers_list);
		teammemberSwipeListView.setAdapter(teammemberListAdapter);
		teammemberSwipeListView
				.setEmptyView(findViewById(R.id.teammembers_list_empty));
	}

	private ViewBinder teammemberListAdapterBinder = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, final Object data,
				String textRepresentation) {
			switch (view.getId()) {
			case R.id.remove:
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Map<String, Object> removeItem = null;
						for (Map<String, Object> map : teammemberList) {
							if (map.get("remove").equals(data)) {
								removeItem = map;
								break;
							}
						}
						teammemberList.remove(removeItem);
						teammemberListAdapter.notifyDataSetChanged();
						teammemberSwipeListView.closeOpenedItems();

						//teamData.get(teamListAdapter.getItem(teamListPosition)).remove(removeItem.get("text"));
						
						try {
							Dao<GameTeammember, Integer> memberDao = DatabaseHelper.getInstance(SettingsActivity.this).getGameTeammemberDao();
							GameTeammember member = memberDao.queryForId(Integer.valueOf(data.toString()));
							member.setSpecialMark(Constants.DELETE_MARK);
							memberDao.update(member);
						} catch (SQLException e) {
							e.printStackTrace();
						}
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

	private Spinner.OnItemSelectedListener teamListSpinnerItemSelectedListener = new Spinner.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,long arg3) {

			teamListPosition = pos;
			
			teammemberList.clear();
			
			/*List<String> items = teamData.get(teamListAdapter.getItem(teamListPosition));
			for(String item:items){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("text", item);
				map.put("remove", Math.random());
				teammemberList.add(map);
			}*/
			String teamName = teamListAdapter.getItem(teamListPosition);
			DatabaseHelper helper = DatabaseHelper.getInstance(SettingsActivity.this);
			try {
				GameTeam team= helper.getGameTeamDao().queryBuilder().where().eq(GameTeam.COLUMN_NAME, teamName).query().get(0);
				GameTeammember[] members = team.getMembers().toArray(new GameTeammember[0]);
				
				for(GameTeammember member:members){
					if(member.getSpecialMark() != Constants.DELETE_MARK){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("text", member.getName());
						map.put("remove", member.getId());
						teammemberList.add(map);
					}
				}
				
				currentTeamId = team.getId();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			teammemberListAdapter.notifyDataSetChanged();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			teamListPosition = -1;
		}
	};

	private OnClickListener addTeamButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			final EditText et = new EditText(SettingsActivity.this);
			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = et.getText().toString();
					if (!"".equals(value)) {
						teamListAdapter.add(value);
						teamListAdapter.notifyDataSetChanged();

						//teamData.put(value, new ArrayList<String>());
						
						//ormlite
						GameTeam newTeam = new GameTeam(value);
						
						DatabaseHelper helper = DatabaseHelper.getInstance(SettingsActivity.this);
						Dao<GameTeam, Integer> teamDao;
						try {
							teamDao = helper.getGameTeamDao();
							teamDao.create(newTeam);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						
					}

				}
			};
			AlertDialog dlg = SettingsActivity.this.getAlertDlg(et, listener,
					null);
			dlg.show();

		}
	};

	private OnClickListener deleteTeamButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (teamListPosition != -1) {
				final TextView tv = new TextView(SettingsActivity.this);
				tv.setText(getResources().getString(R.string.delete_team_tip));

				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String item = teamListAdapter.getItem(teamListPosition);
						teamListAdapter.remove(item);
						teamListAdapter.notifyDataSetChanged();

						//teamData.remove(item);
						
						GameTeam team = new GameTeam(currentTeamId,item,Constants.DELETE_MARK);
						try {
							DatabaseHelper.getInstance(SettingsActivity.this).getGameTeamDao().update(team);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				};
				AlertDialog dlg = SettingsActivity.this.getAlertDlg(tv,
						listener, null);
				dlg.show();
			}
		}
	};

	private OnClickListener addTeammemberButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String teammemberName = teammemberNameEdit.getText().toString();
			if (!"".equals(teammemberName) && teamListPosition != -1) {
				

				//teamData.get(teamListAdapter.getItem(teamListPosition)).add(teammemberName);
				
				GameTeam team = new GameTeam();
				team.setId(currentTeamId);
				
				GameTeammember member = new GameTeammember(teammemberName, team);
				try {
					DatabaseHelper.getInstance(SettingsActivity.this).getGameTeammemberDao().create(member);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("text", teammemberName);
				map.put("remove", member.getId());
				teammemberList.add(map);
				teammemberListAdapter.notifyDataSetChanged();

				teammemberNameEdit.setText("");
			}
		}
	};

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Create an intent for the parent Activity
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			// Check if we need to create the entire stack
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This stack doesn't exist yet, so it must be synthesized
				TaskStackBuilder.create(this).addParentStack(this)
						.startActivities();
			} else {
				// Stack exists, so just navigate up
				NavUtils.navigateUpFromSameTask(this);
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/

	private AlertDialog getAlertDlg(final View view,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {
		AlertDialog dlg = new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(
						getResources().getString(R.string.input_team_name_tip))
				.setView(view)
				.setPositiveButton(
						getResources().getString(R.string.input_team_name_ok),
						positiveListener)
				.setNegativeButton(
						getResources().getString(
								R.string.input_team_name_cancel),
						negativeListener).create();

		return dlg;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("teamListPosition", teamListPosition);
		outState.putStringArray("teamList", teamList.toArray(new String[0]));
		outState.putParcelableArrayList("teammemberList",
				(ArrayList<? extends Parcelable>) teammemberList);
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// openFileInput("");
		Log.d(TAG, "onResume");

		loadTeamList();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");

		//FileUtil.writeSettingsFile(this, teamData);
	}
	
	private void loadTeamList(){
		//teamData = (Map)FileUtil.getSettingsFile(this);
		//restore team list
		teamListAdapter.clear();
		/*for(String name:teamData.keySet()){
			teamListAdapter.add(name);
		}*/
		DatabaseHelper helper = DatabaseHelper.getInstance(SettingsActivity.this);
		try {
			PreparedQuery<GameTeam> query = helper.getGameTeamDao().queryBuilder().where().ne(GameTeam.COLUMN_MARK,Constants.DELETE_MARK).prepare();
			List<GameTeam> teams = helper.getGameTeamDao().query(query);
			for(GameTeam team:teams){
				teamListAdapter.add(team.getName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		teamListAdapter.notifyDataSetChanged();
	}
	
}
