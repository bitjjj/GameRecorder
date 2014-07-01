package com.gamerecorder.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.SimpleAdapter;

import com.gamerecorder.events.TeamListChangeEvent;
import com.gamerecorder.fragment.BasketballFragment;
import com.gamerecorder.util.Constants;
import com.gamerecorder.util.FileUtil;
import com.gamerecorder.util.ScreenSnapUtil;

import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity {

	private static final String TAG = "MainActivity";
	private static final String CURRENT_FRAGMENT_TAG = "fragmentPosition";

	private static int WAIT_SETTINGS_CODE = 101;
	private String[] fragmentTags = Constants.GAME_NAMES_EN;
	//private int[] fragmentTitles = { R.string.basketball_name,R.string.badminton_name, R.string.tabletennis_name };
	//private String[] fragmentNames = { BasketballFragment.class.getName(),BadmintonFragment.class.getName(),TableTennisFragment.class.getName() };
	private int[] fragmentTitles = { R.string.basketball_name};
	private String[] fragmentNames = { BasketballFragment.class.getName()};
	private List<Map<String, Object>> fragmentData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar ab = getActionBar();

		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		ab.setTitle("");

		fragmentData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < fragmentNames.length; i++) {
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
		getMenuInflater().inflate(R.menu.main_menu, menu);

		MenuItem item = menu.findItem(R.id.menu_item_share);
		ShareActionProvider provider = (ShareActionProvider) item
				.getActionProvider();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_label) + "(" + Constants.GITHUB_LINK + ")");
		provider.setShareIntent(intent);

		// provider.setShareHistoryFileName(null);
		OnShareTargetSelectedListener listener = new OnShareTargetSelectedListener() {
			public boolean onShareTargetSelected(ShareActionProvider source,
					Intent intent) {
				startActivity(intent);
				return true;
			}
		};
		provider.setOnShareTargetSelectedListener(listener);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_item_settings:
			Intent settingsActivity = new Intent(this, SettingsActivity.class);
			startActivityForResult(settingsActivity, WAIT_SETTINGS_CODE);

			ScreenSnapUtil.shoot(this, FileUtil.getScreenSnapFile(this));

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

	public String getGameKind() {
		return fragmentTags[getActionBar().getSelectedNavigationIndex()];
	}

	public static void shareMsg(Context context, String activityTitle,
			String msgTitle, String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals("")) {
			intent.setType("text/plain"); // 纯文本
		} else {
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile()) {
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}

}
