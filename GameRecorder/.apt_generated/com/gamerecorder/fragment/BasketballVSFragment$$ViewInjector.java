// Generated code from Butter Knife. Do not modify!
package com.gamerecorder.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class BasketballVSFragment$$ViewInjector {
  public static void inject(Finder finder, final com.gamerecorder.fragment.BasketballVSFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361819, "field 'team1Spinner'");
    target.team1Spinner = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131361820, "field 'team2Spinner'");
    target.team2Spinner = (android.widget.Spinner) view;
    view = finder.findRequiredView(source, 2131361821, "field 'score2EditText' and field 'score1EditText'");
    target.score2EditText = (android.widget.EditText) view;
    target.score1EditText = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131361822, "field 'gameStartButton' and method 'gameStartButton'");
    target.gameStartButton = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.gameStartButton(p0);
        }
      });
    view = finder.findRequiredView(source, 2131361844, "field 'teamScoreListView'");
    target.teamScoreListView = (android.widget.ListView) view;
  }

  public static void reset(com.gamerecorder.fragment.BasketballVSFragment target) {
    target.team1Spinner = null;
    target.team2Spinner = null;
    target.score2EditText = null;
    target.score1EditText = null;
    target.gameStartButton = null;
    target.teamScoreListView = null;
  }
}
