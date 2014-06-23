package com.gamerecorder.model;

import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = GameResult.TABLE_NAME)
public class GameResult {

	public static final String TABLE_NAME = "game_result";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_MARK = "special_mark";
	public static final String COLUMN_KIND = "game_kind";
	public static final String COLUMN_START = "start_date";
	public static final String COLUMN_END = "end_date";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(columnName = COLUMN_KIND)
	private String gameKind;
	
	@DatabaseField(foreign = true, canBeNull = false)
	private GameTeam team1;

	@DatabaseField(foreign = true, canBeNull = false)
	private GameTeam team2;
	
	@DatabaseField(columnName = COLUMN_START)
	private Date startDate;
	
	@DatabaseField(columnName = COLUMN_END)
	private Date endDate;
	
	@DatabaseField(columnName = COLUMN_MARK)
	private int specialMark;
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<GameResultDetail> details;
	
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

	public GameTeam getTeam1() {
		return team1;
	}

	public void setTeam1(GameTeam team1) {
		this.team1 = team1;
	}

	public GameTeam getTeam2() {
		return team2;
	}

	public void setTeam2(GameTeam team2) {
		this.team2 = team2;
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

	public void setSpecialMark(int specialMark) {
		this.specialMark = specialMark;
	}

	public ForeignCollection<GameResultDetail> getDetails() {
		return details;
	}
	
	public GameResult(){}
}
