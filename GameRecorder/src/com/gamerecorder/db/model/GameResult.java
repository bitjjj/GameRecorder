package com.gamerecorder.db.model;

import java.util.Date;

import com.gamerecorder.interfaces.Identity;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = GameResult.TABLE_NAME)
public class GameResult implements Identity{

	public static final String TABLE_NAME = "game_result";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_MARK = "special_mark";
	public static final String COLUMN_KIND = "game_kind";
	public static final String COLUMN_START = "start_date";
	public static final String COLUMN_END = "end_date";
	public static final String COLUMN_SCORE1 = "scoreLeft";
	public static final String COLUMN_SCORE2 = "scoreRight";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(columnName = COLUMN_KIND)
	private String gameKind;
	
	@DatabaseField(foreign = true, canBeNull = false,foreignAutoRefresh = true,maxForeignAutoRefreshLevel = 1)
	private GameTeam teamLeft;

	@DatabaseField(foreign = true, canBeNull = false,foreignAutoRefresh = true,maxForeignAutoRefreshLevel = 1)
	private GameTeam teamRight;
	
	@DatabaseField(columnName = COLUMN_START)
	private Date startDate;
	
	@DatabaseField(columnName = COLUMN_END)
	private Date endDate;
	
	@DatabaseField(columnName = COLUMN_MARK)
	private int specialMark;
	
	@DatabaseField(defaultValue = "0")
	private int scoreLeft,scoreRight;	
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<GameResultStatistic> details;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGameKind() {
		return gameKind;
	}

	public void setGameKind(String gameKind) {
		this.gameKind = gameKind;
	}

	public GameTeam getTeamLeft() {
		return teamLeft;
	}

	public void setTeamLeft(GameTeam teamLeft) {
		this.teamLeft = teamLeft;
	}

	public GameTeam getTeamRight() {
		return teamRight;
	}

	public void setTeamRight(GameTeam teamRight) {
		this.teamRight = teamRight;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getSpecialMark() {
		return specialMark;
	}

	public int getScoreLeft() {
		return scoreLeft;
	}

	public void setScoreLeft(int scoreLeft) {
		this.scoreLeft = scoreLeft;
	}

	public int getScoreRight() {
		return scoreRight;
	}

	public void setScoreRight(int scoreRight) {
		this.scoreRight = scoreRight;
	}

	
	public void setSpecialMark(int specialMark) {
		this.specialMark = specialMark;
	}

	public ForeignCollection<GameResultStatistic> getDetails() {
		return details;
	}
	
	public GameResult(String gameKind,GameTeam teamLeft,GameTeam teamRight){
		this();
		this.gameKind = gameKind;
		this.teamLeft = teamLeft;
		this.teamRight = teamRight;
		this.startDate = new Date();
	}
	
	public GameResult(){}
	
	public String getHistoryRecordDesc(){
		StringBuilder result = new StringBuilder();
		
		result.append(teamLeft.getName() + "    " + scoreLeft);
		result.append(" VS ");
		result.append(scoreRight + "    " + teamRight.getName());	

		return result.toString();
		
	}
	
	public boolean isGamingTeams(String[] teamNames){
		boolean isSame = true;
		for(String team:teamNames){
			if(!teamLeft.getName().contains(team) && !teamRight.getName().contains(team)){
				isSame = false;
			}
		}
		return isSame;
	}
	
}
