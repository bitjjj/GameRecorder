package com.gamerecorder.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.astuetz.PagerSlidingTabStrip;
import com.gamerecorder.activity.R;
import com.gamerecorder.events.TeamVSHistoryChangeEvent;

import de.greenrobot.event.EventBus;

public class BasketballFragment extends Fragment{

	private final static String TAG = "BasketballFragment";
	@InjectView(R.id.tabs) PagerSlidingTabStrip tabs;
	@InjectView(R.id.pager) ViewPager pager;
	private BasketballPagerAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_basketball, container,false);

		ButterKnife.inject(this, v);
		
		//tabs = (PagerSlidingTabStrip) v.findViewById(R.id.tabs);
		//pager = (ViewPager) v.findViewById(R.id.pager);
		adapter = new BasketballPagerAdapter(getActivity().getSupportFragmentManager());

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		tabs.setOnPageChangeListener(mPageChangeListener);
		
		return v;
	}

	OnPageChangeListener mPageChangeListener = new OnPageChangeListener(){

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			Log.d(TAG, "onPageSelected " + position);
			if(position == 1){
				FragmentPagerAdapter f = (FragmentPagerAdapter)pager.getAdapter();
				BasketballVSFragment bf0 = (BasketballVSFragment)f.instantiateItem(pager,0);
				EventBus.getDefault().post(new TeamVSHistoryChangeEvent(bf0.getGameTeams()));
			}
		}
		
	};

	public class BasketballPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = {
				getResources().getString(R.string.vs_label),
				getResources().getString(R.string.vs_history_label)};
		private final String[] fragments = {
				BasketballVSFragment.class.getName(),
				BasketballVSHistoryFragment.class.getName()};

		public BasketballPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {

			return Fragment.instantiate(BasketballFragment.this.getActivity(),
					fragments[position]);

		}

	}

}
