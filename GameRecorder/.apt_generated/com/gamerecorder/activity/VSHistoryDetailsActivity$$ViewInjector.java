// Generated code from Butter Knife. Do not modify!
package com.gamerecorder.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class VSHistoryDetailsActivity$$ViewInjector {
  public static void inject(Finder finder, final com.gamerecorder.activity.VSHistoryDetailsActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361813, "field 'listViewRight'");
    target.listViewRight = (com.gamerecorder.widget.PinnedSectionListView) view;
    view = finder.findRequiredView(source, 2131361811, "field 'resultTextView'");
    target.resultTextView = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131361810, "field 'dateTextView'");
    target.dateTextView = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131361812, "field 'listViewLeft'");
    target.listViewLeft = (com.gamerecorder.widget.PinnedSectionListView) view;
  }

  public static void reset(com.gamerecorder.activity.VSHistoryDetailsActivity target) {
    target.listViewRight = null;
    target.resultTextView = null;
    target.dateTextView = null;
    target.listViewLeft = null;
  }
}
