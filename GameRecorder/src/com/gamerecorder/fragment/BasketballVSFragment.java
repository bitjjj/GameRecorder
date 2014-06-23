package com.gamerecorder.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.gamerecorder.events.TeamListChangeEvent;
import com.gamerecorder.interfaces.ListViewDelSelectedItemCallback;
import com.gamerecorder.interfaces.onTeamListLoaded;
import com.gamerecorder.model.Game;
import com.gamerecorder.util.FileUtil;
import com.gamerecorder.util.ViewUtils;
import com.gamerecorder.widget.ListViewActionMode;
import com.gamerecorder.widget.TextViewInfo;

import de.greenrobot.event.EventBus;

public class BasketballVSFragment extends Fragment implements ListViewDelSelectedItemCallback{
	
	private final static String TAG = "BasketballVSFragment";
	private final static int CONTEXT_MENU_GROUP_ID = 988888;
	
	private Map<String, List<String>> teamData;
	
	private Spinner team1Spinner,team2Spinner;
	private ArrayAdapter<String> team1ListAdapter,team2ListAdapter;
	private EditText score1EditText,score2EditText;
	private Button gameStartButton;
	private ArrayList<View> teammemberLabels = new ArrayList<View>();
	private ArrayList<View> teammemberScoreSpinners;
	private ArrayList<View> teammemberGameTypeSpinners;
	
	private ListView teamScoreListView;
	private ArrayAdapter<String> teamScoreListAdapter;
	private ArrayList<String> teamScoreList;
	
	private Game gameModel;
	
	public static BasketballVSFragment newInstance(int position) {
		BasketballVSFragment f = new BasketballVSFragment();
		Bundle b = new Bundle();
		//b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		EventBus.getDefault().register(this);  
	}

	@Override
	public View onCreateView( LayoutInflater inflater, 
		ViewGroup container,
		Bundle savedInstanceState ){
		
		View v = inflater.inflate( R.layout.fragment_basketball_vs, container, false );
		
		team1Spinner = (Spinner)v.findViewById(R.id.team_name_list_1);
		team2Spinner = (Spinner)v.findViewById(R.id.team_name_list_2);
		score1EditText = (EditText)v.findViewById(R.id.team_score_1);
		score2EditText = (EditText)v.findViewById(R.id.team_score_2);
		gameStartButton = (Button)v.findViewById(R.id.game_start);
		teamScoreListView = (ListView)v.findViewById(R.id.teammember_score_history);
		v.findViewsWithText(teammemberLabels, getActivity().getResources().getString(R.string.teammember_name_default), View.FIND_VIEWS_WITH_TEXT);
		teammemberScoreSpinners = ViewUtils.getViewsByTag((ViewGroup)v, getResources().getString(R.string.team_name_score_tag));
		teammemberGameTypeSpinners = ViewUtils.getViewsByTag((ViewGroup)v, getResources().getString(R.string.team_name_game_type_tag));
		
		ArrayAdapter<String> gameScoresAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.basketball_scores));
		ArrayAdapter<String> gameTypesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.basketball_game_types));
		
		gameStartButton.setOnClickListener(gameStartClickListener);
		
		for(View view:teammemberLabels){
			registerForContextMenu(view);
			view.setOnClickListener(teammemberClickListener);
		}
		
		for(View view:teammemberScoreSpinners){
			((Spinner)view).setAdapter(gameScoresAdapter);
		}
		
		for(View view:teammemberGameTypeSpinners){
			((Spinner)view).setAdapter(gameTypesAdapter);
		}
		
		onEvent(new TeamListChangeEvent());
		
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
	
	private View.OnClickListener teammemberClickListener = new View.OnClickListener(){
		@Override
		public void onClick(View view) {
			if(gameStartButton.getText().equals(getResources().getString(R.string.start_label))){
				Toast.makeText(getActivity(), R.string.start_game_tip, Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			String tag = view.getTag().toString(), memeberIndex = tag.substring(tag.lastIndexOf("_") + 1),
					name = ((TextView)view).getText().toString(),desc;
			Spinner gameTypeSpinner = (Spinner)((ViewGroup)view.getParent()).findViewWithTag("team_name_game_type_" + memeberIndex),
					scoreSpinner = (Spinner)((ViewGroup)view.getParent()).findViewWithTag("team_name_score_" + memeberIndex),
					teamSpinner = tag.toString().contains("left") ? team1Spinner : team2Spinner;
			EditText scoreEditText = tag.toString().contains("left") ? score1EditText : score2EditText;
			int newScore = Integer.valueOf(scoreSpinner.getSelectedItem().toString().substring(0,1));
			
			boolean gotPoints = gameTypeSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.basketball_pts));
			if(gotPoints){
				scoreEditText.setText(Integer.valueOf(scoreEditText.getEditableText().toString()) + newScore + "");
				
				gameModel.addScore(teamSpinner.getSelectedItem().toString(), newScore);
				gameModel.addStatistics(teamSpinner.getSelectedItem().toString(), name, newScore, Game.BasketballType.PTS);
				
				desc = scoreSpinner.getSelectedItem().toString();
			}
			else{
				gameModel.addStatistics(teamSpinner.getSelectedItem().toString(), name, null, getBasketballTypeIndex(gameTypeSpinner.getSelectedItem().toString()));
				
				desc = gameTypeSpinner.getSelectedItem().toString();
			}
			
			teamScoreList.add(0,String.format(getResources().getString(R.string.stats_desc),teamSpinner.getSelectedItem().toString(),name,desc));
			teamScoreListAdapter.notifyDataSetChanged();
		}
	};
	
	
	public void onEvent(TeamListChangeEvent event) {
		
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
	
	private int getBasketballTypeIndex(String typeName){
		String[] gameTypes = getResources().getStringArray(R.array.basketball_game_types);
		for(int i = 0; i < gameTypes.length; i++){
			if(gameTypes[i].equals(typeName)){
				return i;
			}
		}
		return -1;
	}
	
	@Override  
    public void onStop() { 
        super.onStop();  
        EventBus.getDefault().unregister(this);  
    }  
	
	
}
