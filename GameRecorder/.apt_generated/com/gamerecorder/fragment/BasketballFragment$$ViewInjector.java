// Generated code from Butter Knife. Do not modify!
package com.gamerecorder.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class BasketballFragment$$ViewInjector {
  public static void inject(Finder finder, final com.gamerecorder.fragment.BasketballFragment target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131361815, "field 'tabs'");
    target.tabs = (com.astuetz.PagerSlidingTabStrip) view;
    view = finder.findRequiredView(source, 2131361816, "field 'pager'");
    target.pager = (android.support.v4.view.ViewPager) view;
  }

  public static void reset(com.gamerecorder.fragment.BasketballFragment target) {
    target.tabs = null;
    target.pager = null;
  }
}
