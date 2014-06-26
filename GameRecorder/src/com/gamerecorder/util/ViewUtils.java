package com.gamerecorder.util;

import java.util.ArrayList;

import butterknife.ButterKnife.Setter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ViewUtils {

	public static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
		ArrayList<View> views = new ArrayList<View>();
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}

			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.toString().contains(tag)) {
				views.add(child);
			}

		}
		return views;
	}

	public static final Setter<View, ArrayAdapter> SET_SPINNER_ADAPTER = new Setter<View, ArrayAdapter>() {
		@Override
		public void set(View view, ArrayAdapter adapter, int index) {
			((Spinner) view).setAdapter(adapter);
		}
	};

}
