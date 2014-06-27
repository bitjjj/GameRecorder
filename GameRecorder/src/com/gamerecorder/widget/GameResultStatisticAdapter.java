package com.gamerecorder.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gamerecorder.activity.R;
import com.gamerecorder.db.model.GameResultStatistic;
import com.gamerecorder.util.Constants;

public class GameResultStatisticAdapter extends ArrayAdapter<GameResultStatistic> {

	private int resource;
	private LayoutInflater inflater;

	public GameResultStatisticAdapter(Context context, int resource,List<GameResultStatistic> objects) {
		super(context, resource, objects);
		this.resource = resource;
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textview;
		// 获取数据
		GameResultStatistic item = getItem(position);
		if (convertView == null) {
			textview = (TextView)inflater.inflate(resource,null);
		} else {
			textview = (TextView) convertView;
		}
		
		String desc;
		int score = item.getScore();
		if(score == Constants.NOT_PTS){
			desc = item.getStatisticType();
		}
		else{
			desc = String.valueOf(score) + getContext().getResources().getString(R.string.score_label);
		}
		
		String text = String.format(getContext().getResources().getString(R.string.stats_desc),item.getTeammember().getTeam().getName(),item.getTeammemeberName(),desc);
		textview.setText(text);
		textview.setTag(item.getId());

		return textview;
	}

}
