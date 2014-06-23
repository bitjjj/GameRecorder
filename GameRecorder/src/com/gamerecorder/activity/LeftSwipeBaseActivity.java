package com.gamerecorder.activity;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

public class LeftSwipeBaseActivity extends SwipeBackActivity{

	
	@Override
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
	}
	
}
