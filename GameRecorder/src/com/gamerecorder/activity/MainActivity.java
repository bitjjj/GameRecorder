package com.gamerecorder.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

import com.gamerecorder.events.TeamListChangeEvent;
import com.gamerecorder.fragment.BasketballFragment;
import com.gamerecorder.fragment.FootballFragment;

import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";
	private static final String CURRENT_FRAGMENT_TAG = "fragmentPosition";

	private static int WAIT_SETTINGS_CODE = 101;
	private String[] fragmentTags = { "basketball", "football" };
	private int[] fragmentTitles = { R.string.basketball_name,
			R.string.football_name };
	private String[] fragmentNames = { BasketballFragment.class.getName(),
			FootballFragment.class.getName() };
	private List<Map<String, Object>> fragmentData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar ab = getActionBar();

		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setTitle("");

		fragmentData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < fragmentTags.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", getResources().getString(fragmentTitles[i]));
			map.put("tag", fragmentTags[i]);
			map.put("fragment", Fragment.instantiate(this, fragmentNames[i]));
			map.put("isAdded", "false");
			fragmentData.add(map);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, fragmentData,
				R.layout.nav_list_textview, new String[] { "title" },
				new int[] { android.R.id.text1 });
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ab.setListNavigationCallbacks(adapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				FragmentTransaction tx = getSupportFragmentManager()
						.beginTransaction();

				for (Map<String, Object> entry : fragmentData) {
					Object o = entry.get("fragment");
					if (o instanceof Fragment
							&& entry.get("isAdded").equals("true")) {
						tx.hide((Fragment) o);
					}
				}

				Map<String, Object> map = fragmentData.get(itemPosition);
				Object o = map.get("fragment");
				if (o instanceof Fragment) {
					if (map.get("isAdded").equals("false")) {
						tx.add(android.R.id.content, (Fragment) o,
								(String) map.get("tag"));
						map.put("isAdded", "true");
					} else {
						tx.show((Fragment) o);
					}

				}

				tx.commit();

				return true;
			}
		});

		ab.setSelectedNavigationItem(savedInstanceState != null ? savedInstanceState
				.getInt(CURRENT_FRAGMENT_TAG) : 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent settingsActivity = new Intent(this, SettingsActivity.class);
			startActivityForResult(settingsActivity, WAIT_SETTINGS_CODE);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, requestCode + " " + resultCode);
		if (requestCode == WAIT_SETTINGS_CODE) {
			// if (resultCode == RESULT_OK) {

			// }
			EventBus.getDefault().post(new TeamListChangeEvent());
		}

		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(CURRENT_FRAGMENT_TAG, getActionBar()
				.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);

	}
	
	public String getGameKind(){
		return fragmentTags[getActionBar().getSelectedNavigationIndex()];
	}

}
