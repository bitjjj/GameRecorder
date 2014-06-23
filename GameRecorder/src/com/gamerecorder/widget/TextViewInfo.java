package com.gamerecorder.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.widget.TextView;

public class TextViewInfo extends TextView {

	public TextViewInfo(Context context) {
		super(context);
	}

	public TextViewInfo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public TextViewInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
		return new TextViewContextMenuInfo(this);
	}

	public static class TextViewContextMenuInfo implements
			ContextMenu.ContextMenuInfo {

		private TextView view;

		public TextViewContextMenuInfo(TextView v) {
			this.view = v;
		}

		public TextView getTextView() {
			return view;
		}

	}

}
