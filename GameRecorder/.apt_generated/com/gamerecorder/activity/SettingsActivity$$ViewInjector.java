// Generated code from Butter Knife. Do not modify!
package com.gamerecorder.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class SettingsActivity$$ViewInjector {
  public static void inject(Finder finder, final com.gamerecorder.activity.SettingsActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361802, "field 'addTeamButton' and method 'addTeamButton'");
    target.addTeamButton = (android.widget.ImageButton) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.addTeamButton(p0);
        }
      });
    view = finder.findRequiredView(source, 2131361803, "field 'deleteTeamButton' and method 'deleteTeamButton'");
    target.deleteTeamButton = (android.widget.ImageButton) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.deleteTeamButton(p0);
        }
      });
    view = finder.findRequiredView(source, 2131361801, "field 'teamListSpinner'");
    target.teamListSpinner = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131361804, "field 'teammemberNameEdit'");
    target.teammemberNameEdit = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131361806, "field 'teammemberSwipeListView'");
    target.teammemberSwipeListView = (com.fortysevendeg.swipelistview.SwipeListView) view;
    view = finder.findRequiredView(source, 2131361805, "field 'addTeammemeberButton' and method 'addTeammemberButton'");
    target.addTeammemeberButton = (android.widget.ImageButton) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.addTeammemberButton(p0);
        }
      });
  }

  public static void reset(com.gamerecorder.activity.SettingsActivity target) {
    target.addTeamButton = null;
    target.deleteTeamButton = null;
    target.teamListSpinner = null;
    target.teammemberNameEdit = null;
    target.teammemberSwipeListView = null;
    target.addTeammemeberButton = null;
  }
}
