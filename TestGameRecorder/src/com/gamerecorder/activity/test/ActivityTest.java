package com.gamerecorder.activity.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.gamerecorder.activity.MainActivity;
import com.robotium.solo.Solo;

public class ActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;

	public ActivityTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		this.solo = new Solo(this.getInstrumentation(), this.getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		this.solo.finishOpenedActivities();
	}

	public void testPreConditions() {
		Activity activity = solo.getCurrentActivity();
		
		int appNameId = activity.getResources().getIdentifier("app_name", "string", activity.getPackageName());
		String connect = activity.getResources().getString(appNameId);
		
		//assertTrue(this.solo.searchText(connect));
		
		EditText score1 = (EditText)this.solo.getView(com.gamerecorder.activity.R.id.team_score_1);
		EditText score2 = (EditText)this.solo.getView(com.gamerecorder.activity.R.id.team_score_2);
		
		
		this.solo.clearEditText(score1);
		this.solo.clearEditText(score2);
		
		this.solo.enterText(score1, "10");
		this.solo.enterText(score2, "20");
		
		assertTrue(this.solo.searchText("10"));
		
	}

}
