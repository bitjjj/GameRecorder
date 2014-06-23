package com.gamerecorder.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamerecorder.activity.R;
import com.gamerecorder.interfaces.onSettingsSaved;

public class FootballFragment extends Fragment implements onSettingsSaved{
	
	@Override
	public View onCreateView( LayoutInflater inflater, 
		ViewGroup container,
		Bundle savedInstanceState ){
		return inflater.inflate( R.layout.fragment_football, container, false );
	}

	@Override
	public void onTeamListChanged() {
	}

}
