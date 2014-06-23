package com.gamerecorder.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.BoringLayout.Metrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gamerecorder.activity.MainActivity;
import com.gamerecorder.activity.R;
import com.gamerecorder.interfaces.ListViewDelSelectedItemCallback;
import com.gamerecorder.interfaces.onTeamListLoaded;
import com.gamerecorder.model.Game;
import com.gamerecorder.util.FileUtil;
import com.gamerecorder.widget.ListViewActionMode;
import com.gamerecorder.widget.TextViewInfo;

public class BasketballVSFragmentOld extends Fragment implements onTeamListLoaded,ListViewDelSelectedItemCallback{
	
	private final static String TAG = "BasketballVSFragment";
	private final static int CONTEXT_MENU_GROUP_ID = 988888;
	
	private Map<String, List<String>> teamData;
	
	private Spinner team1Spinner,team2Spinner;
	private ArrayAdapter<String> team1ListAdapter,team2ListAdapter;
	private EditText score1EditText,score2EditText;
	private Button gameStartButton;
	private ArrayList<View> teammemberLabels = new ArrayList<View>();
	private ArrayList<View> teammemberScoreButtons = new ArrayList<View>();
	
	private ListView teamScoreListView;
	private ArrayAdapter<String> teamScoreListAdapter;
	private ArrayList<String> teamScoreList;
	
	private Game gameModel;
	
	public static BasketballVSFragmentOld newInstance(int position) {
		BasketballVSFragmentOld f = new BasketballVSFragmentOld();
		Bundle b = new Bundle();
		//b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	
	@Override
	public View onCreateView( LayoutInflater inflater, 
		ViewGroup container,
		Bundle savedInstanceState ){
		
		View v = inflater.inflate( R.layout.fragment_basketball_vs_old, container, false );
		
		team1Spinner = (Spinner)v.findViewById(R.id.team_name_list_1);
		team2Spinner = (Spinner)v.findViewById(R.id.team_name_list_2);
		score1EditText = (EditText)v.findViewById(R.id.team_score_1);
		score2EditText = (EditText)v.findViewById(R.id.team_score_2);
		gameStartButton = (Button)v.findViewById(R.id.game_start);
		teamScoreListView = (ListView)v.findViewById(R.id.teammember_score_history);
		v.findViewsWithText(teammemberLabels, getResources().getString(R.string.teammember_name_default), View.FIND_VIEWS_WITH_TEXT);
		v.findViewsWithText(teammemberScoreButtons, getResources().getString(R.string.score), View.FIND_VIEWS_WITH_TEXT);

		gameStartButton.setOnClickListener(gameStartClickListener);
		
		for(View view:teammemberLabels){
			registerForContextMenu(view);
		}
		
		for(View view:teammemberScoreButtons){
			view.setOnClickListener(scoreClickListener);
		}
		
		loadTeamList();
		
		teamScoreList = new ArrayList<String>();
		teamScoreListAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, teamScoreList);
		teamScoreListView.setAdapter(teamScoreListAdapter);
		
		teamScoreListView.setLayoutAnimation(
		        new LayoutAnimationController(AnimationUtils.loadAnimation(getActivity(), R.anim.list_animation), 0.5f));
		
		teamScoreListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		//teamScoreListView.setMultiChoiceModeListener(new ActionModeCallback());
		teamScoreListView.setMultiChoiceModeListener(new ListViewActionMode<String>(getActivity(), teamScoreListView, teamScoreListAdapter,this));
		
		return v;
	}
	
	private View.OnClickListener gameStartClickListener = new View.OnClickListener(){
		@Override
		public void onClick(View view) {
			Button btn = (Button)view;
			String startLabel = getResources().getString(R.string.start_label);
			if(btn.getText().equals(startLabel) 
					&& team1Spinner.getSelectedItem().toString().equals(team2Spinner.getSelectedItem().toString())){
				Toast.makeText(getActivity(), R.string.team_same_tip, Toast.LENGTH_SHORT).show();
			}
			else if(btn.getText().equals(startLabel)){
				btn.setText(getResources().getString(R.string.end_label));
				team1Spinner.setEnabled(false);
				team2Spinner.setEnabled(false);
				
				MainActivity mainActivity = (MainActivity)getActivity();
				gameModel = new Game(mainActivity.getGameKind(),
						team1Spinner.getSelectedItem().toString(), team2Spinner.getSelectedItem().toString());
				gameModel.startGame();
			}
			else{
				btn.setText(startLabel);
				team1Spinner.setEnabled(true);
				team2Spinner.setEnabled(true);
				
				score1EditText.setText("0");
				score2EditText.setText("0");
				
				teamScoreListAdapter.clear();
				teamScoreListAdapter.notifyDataSetChanged();
				
				gameModel.endGame();
				List<Object> historyList = FileUtil.getGameHistoryFile(getActivity());
				historyList.add(gameModel);
				FileUtil.writeGameHistoryFile(getActivity(), historyList);
			}
		}
		
	};
	
	private View.OnClickListener scoreClickListener = new View.OnClickListener(){
		@Override
		public void onClick(View view) {
			
			if(gameStartButton.getText().equals(getResources().getString(R.string.start_label))){
				Toast.makeText(getActivity(), R.string.start_game_tip, Toast.LENGTH_SHORT).show();
				return;
			}
			
			Button btn = (Button)view;
			String tag = btn.getTag().toString(),text = btn.getText().toString(),name = null;
			int newScore = 0,allScore;
			EditText textBox = null;
			Spinner spinner = null;
			if(tag.contains("left")){
				textBox = score1EditText;
				spinner = team1Spinner;
			}
			else{
				textBox = score2EditText;
				spinner = team2Spinner;
			}
			
			if(text.contains("1")){
				newScore = 1;
			}
			else if(text.contains("2")){
				newScore = 2;
			}
			else if(text.contains("3")){
				newScore = 3;
			}
			
			allScore = Integer.valueOf(textBox.getEditableText().toString()) + newScore;
			
			textBox.setText(allScore + "");
			
			for(View v:teammemberLabels){
				if(v.getTag().toString().equals(btn.getTag().toString())){
					name = ((TextView)v).getText().toString();
					break;
				}
			}
			
			teamScoreListAdapter.add("【" + spinner.getSelectedItem().toString() + "】" + name + ": " + text);
			teamScoreListAdapter.notifyDataSetChanged();
		
			gameModel.addScore(spinner.getSelectedItem().toString(), newScore);
			gameModel.addStatistics(spinner.getSelectedItem().toString(), name, newScore, Game.BasketballType.PTS);
		}
		
	};


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}


	@Override
	public void loadTeamList() {
		
		if(gameStartButton.getText().equals(getResources().getString(R.string.start_label))){
			teamData = (Map)FileUtil.getSettingsFile(getActivity());
			
			List<String> teamNames = new ArrayList<String>();
			for(Map.Entry<String, List<String>> entry:teamData.entrySet()){
				teamNames.add(entry.getKey());
			}
			
			
			team1ListAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, teamNames);
			team1ListAdapter.setDropDownViewResource(R.layout.dropdown_item);
			
			team1Spinner.setAdapter(team1ListAdapter);
			team1Spinner.setOnItemSelectedListener(teamListSpinnerItemSelectedListener);
			
			team2ListAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, teamNames);
			team2ListAdapter.setDropDownViewResource(R.layout.dropdown_item);
			
			team2Spinner.setAdapter(team2ListAdapter);
			team2Spinner.setOnItemSelectedListener(teamListSpinnerItemSelectedListener);
		}

	}
	
	private Spinner.OnItemSelectedListener teamListSpinnerItemSelectedListener = new Spinner.OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> spinner, View view, int pos,long arg3) {
			String teamName = team1ListAdapter.getItem(pos),mark = "";
			List<String> teammembers = teamData.get(teamName);
			
			if(spinner.getId() == R.id.team_name_list_1){
				mark = "left";
			}
			else if(spinner.getId() == R.id.team_name_list_2){
				mark = "right";
			}
			
			for(View v:teammemberLabels){//reset
				if(v.getTag().toString().startsWith("teammember_" + mark + "_name_")){
					((TextView)v).setText(getResources().getString(R.string.teammember_name_default));
				}
			}
			
			for(int i=0;i<teammembers.size();i++){
				for(View v:teammemberLabels){
					if(v.getTag().toString().equals("teammember_" + mark + "_name_" + (i+1))){
						((TextView)v).setText(teammembers.get(i));
					}
				}
			}
			
		}
		
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};
	
	public String[] getGameTeams(){
		String[] teams = new String[2];
		teams[0] = team1Spinner.getSelectedItem().toString();
		teams[1] = team2Spinner.getSelectedItem().toString();
		return teams;
	}


	@Override
	public void onDeleteItems(SparseBooleanArray selectedIndexes) {
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	    menu.setHeaderTitle(getResources().getString(R.string.replace_teammember_title));
	    
	    List<String> teammembers = new ArrayList<String>();
	    if(v.getTag().toString().contains("left")){
	    	teammembers = teamData.get(team1Spinner.getSelectedItem());
	    }
	    else{
	    	teammembers = teamData.get(team2Spinner.getSelectedItem());
	    }
	    
	    for(int i = 0; i < teammembers.size(); i++ ){
	    	menu.add(CONTEXT_MENU_GROUP_ID, i+1, Menu.NONE, teammembers.get(i));
	    }
	 
	}
	
	@Override  
    public boolean onContextItemSelected(MenuItem item) {  
        
		if (item.getGroupId() == CONTEXT_MENU_GROUP_ID) {
			TextViewInfo.TextViewContextMenuInfo menuInfo = (TextViewInfo.TextViewContextMenuInfo)item.getMenuInfo();
			TextView tv = menuInfo.getTextView();
			tv.setText(item.getTitle());
		}
        return super.onContextItemSelected(item);  
    }  
	
	/*private class ActionModeCallback implements AbsListView.MultiChoiceModeListener{

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			SparseBooleanArray checkedItemPositions = teamScoreListView.getCheckedItemPositions();
			int itemCount = teamScoreListView.getCount();

			switch (item.getItemId()) {
			case R.id.menu_delete:

                for(int i = itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                    	teamScoreListAdapter.remove(teamScoreListAdapter.getItem(i));
                    }
                }
                checkedItemPositions.clear();
                teamScoreListAdapter.notifyDataSetChanged();
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
			int count = teamScoreListView.getCheckedItemCount();
			mode.setTitle(String.format(getActivity().getString(R.string.selected_items_tip), count));
			
		}
		
	}*/

}
