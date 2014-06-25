package com.gamerecorder.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gamerecorder.db.model.GameResult;

public class GameResultAdapter extends ArrayAdapter<GameResult> {

	private int resource;
	private LayoutInflater inflater;

	public GameResultAdapter(Context context, int resource,List<GameResult> objects) {
		super(context, resource, objects);
		this.resource = resource;
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textview;
		// 获取数据
		GameResult item = getItem(position);
		if (convertView == null) {
			textview = (TextView)inflater.inflate(resource,null);
		} else {
			textview = (TextView) convertView;
		}
		
		
		textview.setText(item.getHistoryRecordDesc());
		textview.setTag(item.getId());

		return textview;
	}

}
