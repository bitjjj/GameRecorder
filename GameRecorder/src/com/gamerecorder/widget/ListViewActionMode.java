package com.gamerecorder.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gamerecorder.activity.R;
import com.gamerecorder.interfaces.Identity;
import com.gamerecorder.interfaces.ListViewDelSelectedItemCallback;

public class ListViewActionMode<T extends Identity> implements AbsListView.MultiChoiceModeListener{
	
	private Context ctx;
	private ListView listView;
	private ArrayAdapter<T> listAdapter;
	private ListViewDelSelectedItemCallback delCallback;
	
	public ListViewActionMode(Context ctx,ListView listView,ArrayAdapter<T> listAdapter,ListViewDelSelectedItemCallback delCallback) {
		this.ctx = ctx;
		this.listView = listView;
		this.listAdapter = listAdapter;
		this.delCallback = delCallback;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
		int itemCount = listView.getCount();

		switch (item.getItemId()) {
		case R.id.menu_delete:
			List<Identity> delItems = new ArrayList<Identity>();
            for(int i = itemCount-1; i >= 0; i--){
                if(checkedItemPositions.get(i)){
                	delItems.add(listAdapter.getItem(i));
                	listAdapter.remove(listAdapter.getItem(i));
                }
            }
            checkedItemPositions.clear();
            listAdapter.notifyDataSetChanged();
            
            delCallback.deleteSelectedItems(delItems);
            
            mode.finish();
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.cab_team_score_history_menu, menu);
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position,
			long id, boolean checked) {
		int count = listView.getCheckedItemCount();
		mode.setTitle(String.format(ctx.getString(R.string.selected_items_tip), count));
		
	}
}
