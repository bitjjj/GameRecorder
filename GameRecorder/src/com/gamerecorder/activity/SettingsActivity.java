package com.gamerecorder.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
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
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.gamerecorder.db.dao.GameTeamDao;
import com.gamerecorder.db.dao.GameTeammemberDao;
import com.gamerecorder.db.model.GameTeam;
import com.gamerecorder.db.model.GameTeammember;
import com.gamerecorder.util.Constants;

public class SettingsActivity extends LeftSwipeBaseActivity {

	private final String TAG = "SettingsActivity";

	@InjectView(R.id.team_list)
	Spinner teamListSpinner;
	private List<String> teamList;
	private ArrayAdapter<String> teamListAdapter;
	private int teamListPosition = -1;
	@InjectView(R.id.add_team)
	ImageButton addTeamButton;
	@InjectView(R.id.delete_team)
	ImageButton deleteTeamButton;

	@InjectView(R.id.teammember_name)
	EditText teammemberNameEdit;
	@InjectView(R.id.add_teammember)
	ImageButton addTeammemeberButton;
	private List<Map<String, Object>> teammemberList;
	@InjectView(R.id.teammembers_list)
	SwipeListView teammemberSwipeListView;
	private SimpleAdapter teammemberListAdapter;

	private GameTeamDao gameTeamDao;
	private GameTeammemberDao gameTeammemberDao;
	private int currentTeamId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		ButterKnife.inject(this);

		setActionBar();

		handleTeamViewSection(savedInstanceState);
		handleTeammemberViewSection(savedInstanceState);

		gameTeamDao = new GameTeamDao(this);
		gameTeammemberDao = new GameTeammemberDao(this);

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

		teamListSpinner.setAdapter(teamListAdapter);
		teamListSpinner
				.setOnItemSelectedListener(teamListSpinnerItemSelectedListener);
		if (teamListPosition != -1) {
			teamListSpinner.setSelection(teamListPosition);
		}

	}

	private void handleTeammemberViewSection(Bundle savedInstanceState) {

		if (savedInstanceState != null
				&& savedInstanceState.containsKey("teammemberList")) {
			teammemberList = (List) savedInstanceState
					.getParcelableArrayList("teammemberList");
		} else {
			teammemberList = new ArrayList<Map<String, Object>>();
		}

		teammemberListAdapter = new SimpleAdapter(this, teammemberList,
				R.layout.team_members_list_item, new String[] { "text",
						"remove" }, new int[] { R.id.text, R.id.remove });
		teammemberListAdapter.setViewBinder(teammemberListAdapterBinder);

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

						GameTeammember member = gameTeammemberDao
								.queryById(Integer.valueOf(data.toString()));
						member.setSpecialMark(Constants.DELETE_MARK);
						gameTeammemberDao.update(member);
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

		public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {

			teamListPosition = pos;

			teammemberList.clear();

			String teamName = teamListAdapter.getItem(teamListPosition);
			GameTeam team = gameTeamDao.queryByName(teamName);
			GameTeammember[] members = team.getMembers().toArray(
					new GameTeammember[0]);

			for (GameTeammember member : members) {
				if (member.getSpecialMark() != Constants.DELETE_MARK) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("text", member.getName());
					map.put("remove", member.getId());
					teammemberList.add(map);
				}
			}

			currentTeamId = team.getId();

			teammemberListAdapter.notifyDataSetChanged();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			teamListPosition = -1;
		}
	};

	@OnClick(R.id.add_team)
	public void addTeamButton(View v) {

		final EditText et = new EditText(SettingsActivity.this);
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = et.getText().toString();
				if (!"".equals(value)) {
					teamListAdapter.add(value);
					teamListAdapter.notifyDataSetChanged();

					GameTeam newTeam = new GameTeam(value);
					gameTeamDao.create(newTeam);

				}

			}
		};
		AlertDialog dlg = SettingsActivity.this.getAlertDlg(et, listener, null);
		dlg.show();

	}

	@OnClick(R.id.delete_team)
	public void deleteTeamButton(View v) {
		if (teamListPosition != -1) {
			final TextView tv = new TextView(SettingsActivity.this);
			tv.setText(getResources().getString(R.string.delete_team_tip));

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String item = teamListAdapter.getItem(teamListPosition);
					teamListAdapter.remove(item);
					teamListAdapter.notifyDataSetChanged();

					GameTeam team = new GameTeam(currentTeamId, item,
							Constants.DELETE_MARK);
					gameTeamDao.update(team);

				}
			};
			AlertDialog dlg = SettingsActivity.this.getAlertDlg(tv, listener,
					null);
			dlg.show();
		}
	}

	@OnClick(R.id.add_teammember)
	public void addTeammemberButton(View v) {
		String teammemberName = teammemberNameEdit.getText().toString();
		if (!"".equals(teammemberName) && teamListPosition != -1) {

			GameTeam team = new GameTeam();
			team.setId(currentTeamId);

			GameTeammember member = new GameTeammember(teammemberName, team);
			gameTeammemberDao.create(member);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", teammemberName);
			map.put("remove", member.getId());
			teammemberList.add(map);
			teammemberListAdapter.notifyDataSetChanged();

			teammemberNameEdit.setText("");
		}
	}

	private AlertDialog getAlertDlg(final View view,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {
		AlertDialog dlg = new AlertDialog.Builder(SettingsActivity.this)
				.setTitle(
						getResources().getString(R.string.input_team_name_tip))
				.setView(view)
				.setPositiveButton(
						getResources().getString(R.string.ok_label),
						positiveListener)
				.setNegativeButton(
						getResources().getString(
								R.string.cancel_label),
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
		loadTeamList();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void loadTeamList() {

		teamListAdapter.clear();

		List<GameTeam> teams = gameTeamDao.queryByMark(Constants.DELETE_MARK);
		for (GameTeam team : teams) {
			teamListAdapter.add(team.getName());
		}

		teamListAdapter.notifyDataSetChanged();
	}

}
