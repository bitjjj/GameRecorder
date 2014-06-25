package com.gamerecorder.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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

import com.gamerecorder.activity.R;
import com.gamerecorder.db.dao.GameResultDao;
import com.gamerecorder.db.dao.GameResultStatisticDao;
import com.gamerecorder.db.dao.GameTeamDao;
import com.gamerecorder.db.dao.GameTeammemberDao;
import com.gamerecorder.db.model.GameResult;
import com.gamerecorder.db.model.GameResultStatistic;
import com.gamerecorder.db.model.GameTeam;
import com.gamerecorder.db.model.GameTeammember;
import com.gamerecorder.events.TeamListChangeEvent;
import com.gamerecorder.interfaces.ListViewDelSelectedItemCallback;
import com.gamerecorder.model.Game;
import com.gamerecorder.util.Constants;
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
	
	//private Game gameModel;
	private GameResult gameResult;
	private GameTeamDao teamDao;
	private GameTeammemberDao teammemberDao;
	private GameResultDao resultDao;
	private GameResultStatisticDao resultStatDao;
	
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
		ArrayAdapter<String> gameTypesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.basketball_statistic_types));
		
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
				
				/*MainActivity mainActivity = (MainActivity)getActivity();
				gameModel = new Game(mainActivity.getGameKind(),
						team1Spinner.getSelectedItem().toString(), team2Spinner.getSelectedItem().toString());
				gameModel.startGame();*/
				gameResult = new GameResult(getResources().getString(R.string.basketball_name_en), 
						teamDao.queryByName(team1Spinner.getSelectedItem().toString()), 
						teamDao.queryByName(team2Spinner.getSelectedItem().toString()));
				resultDao.create(gameResult);
			}
			else{
				btn.setText(startLabel);
				team1Spinner.setEnabled(true);
				team2Spinner.setEnabled(true);
				
				score1EditText.setText("0");
				score2EditText.setText("0");
				
				teamScoreListAdapter.clear();
				teamScoreListAdapter.notifyDataSetChanged();
				
				
				gameResult.setEndDate(new Date());
				resultDao.update(gameResult);
				//gameModel.endGame();
				//List<Object> historyList = FileUtil.getGameHistoryFile(getActivity());
				//historyList.add(gameModel);
				//FileUtil.writeGameHistoryFile(getActivity(), historyList);
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
			
			TextViewInfo vInfo = (TextViewInfo)view;
			
			if(vInfo.getText().equals(getResources().getString(R.string.teammember_name_default))){
				return;
			}
			
			String tag = vInfo.getTag().toString(), memeberIndex = tag.substring(tag.lastIndexOf("_") + 1),
					name = vInfo.getText().toString(),desc;
			Spinner gameTypeSpinner = (Spinner)((ViewGroup)vInfo.getParent()).findViewWithTag("team_name_game_type_" + memeberIndex),
					scoreSpinner = (Spinner)((ViewGroup)vInfo.getParent()).findViewWithTag("team_name_score_" + memeberIndex),
					teamSpinner = tag.toString().contains("left") ? team1Spinner : team2Spinner;
			EditText scoreEditText = tag.toString().contains("left") ? score1EditText : score2EditText;
			int newScore = Integer.valueOf(scoreSpinner.getSelectedItem().toString().substring(0,1)),teammeberId = vInfo.getTeammeberId();
			
			boolean gotPoints = gameTypeSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.game_statistic_type_pts));
			if(gotPoints){
				scoreEditText.setText(Integer.valueOf(scoreEditText.getEditableText().toString()) + newScore + "");
				
				//gameModel.addScore(teamSpinner.getSelectedItem().toString(), newScore);
				//gameModel.addStatistics(teamSpinner.getSelectedItem().toString(), name, newScore, Game.BasketballType.PTS);
				if(tag.toString().contains("left")){
					gameResult.setScoreLeft(gameResult.getScoreLeft()+newScore);
				}
				else{
					gameResult.setScoreRight(gameResult.getScoreRight()+newScore);
				}
				resultDao.update(gameResult);

				desc = scoreSpinner.getSelectedItem().toString();
			}
			else{
				//gameModel.addStatistics(teamSpinner.getSelectedItem().toString(), name, null, getStatisticTypeIndex(gameTypeSpinner.getSelectedItem().toString()));
				
				desc = gameTypeSpinner.getSelectedItem().toString();
			}
			
			GameResultStatistic statistic = new GameResultStatistic(gameResult, teammemberDao.queryById(teammeberId), gotPoints ? newScore : null, gameTypeSpinner.getSelectedItem().toString());
			resultStatDao.create(statistic);
			
			teamScoreList.add(0,String.format(getResources().getString(R.string.stats_desc),teamSpinner.getSelectedItem().toString(),name,desc));
			teamScoreListAdapter.notifyDataSetChanged();
		}
	};
	
	
	public void onEvent(TeamListChangeEvent event) {
		
		if(gameStartButton.getText().equals(getResources().getString(R.string.start_label))){
			/*teamData = (Map)FileUtil.getSettingsFile(getActivity());
			
			List<String> teamNames = new ArrayList<String>();
			for(Map.Entry<String, List<String>> entry:teamData.entrySet()){
				teamNames.add(entry.getKey());
			}*/
			
			List<String> teamNames = new ArrayList<String>();
			for(GameTeam team:teamDao.queryByMark(Constants.DELETE_MARK)){
				teamNames.add(team.getName());
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
			//List<String> teammembers = teamData.get(teamName);
			//List<String> teammembers = new ArrayList<String>();
			GameTeam team = teamDao.queryByName(teamName);
			GameTeammember[] teammembers  = team.getMembers().toArray(new GameTeammember[0]);
			//for(GameTeammember teammember:teammemberArr){
			//	teammembers.add(teammember.getName());
			//}
			
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
			
			for(int i=0;i<teammembers.length;i++){
				for(View v:teammemberLabels){
					if(v.getTag().toString().equals("teammember_" + mark + "_name_" + (i+1))){
						((TextViewInfo)v).setText(teammembers[i].getName());
						((TextViewInfo)v).setTeammemberId(teammembers[i].getId());
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
	
	private int getStatisticTypeIndex(String typeName){
		String[] gameTypes = getResources().getStringArray(R.array.basketball_statistic_types);
		for(int i = 0; i < gameTypes.length; i++){
			if(gameTypes[i].equals(typeName)){
				return i;
			}
		}
		return -1;
	}
	
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		teamDao = new GameTeamDao(activity);
		teammemberDao = new GameTeammemberDao(activity);
		resultDao = new GameResultDao(activity);
		resultStatDao = new GameResultStatisticDao(activity);
	}

	@Override  
    public void onStop() { 
        super.onStop();  
        EventBus.getDefault().unregister(this);  
    }  
	
	
}
