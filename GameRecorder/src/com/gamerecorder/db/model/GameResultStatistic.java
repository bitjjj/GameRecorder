package com.gamerecorder.db.model;

import java.util.Date;

import com.gamerecorder.interfaces.Identity;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = GameResultStatistic.TABLE_NAME)
public class GameResultStatistic implements Identity{

	public static final String TABLE_NAME = "game_result_statistic";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_MARK = "special_mark";
	public static final String COLUMN_DESC = "desc";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	private String desc;
	
	@DatabaseField(columnName = COLUMN_MARK)
	private int specialMark;
	
	@DatabaseField(foreign = true, canBeNull = false,columnDefinition = "integer references " + GameResult.TABLE_NAME + "(" + GameResult.COLUMN_ID + ") on delete cascade")
	private GameResult result;
	
	@DatabaseField(foreign = true, canBeNull = false)
	public GameTeammember teammember;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getSpecialMark() {
		return specialMark;
	}

	public void setSpecialMark(int specialMark) {
		this.specialMark = specialMark;
	}

	public GameResult getResult() {
		return result;
	}

	public void setResult(GameResult result) {
		this.result = result;
	}
	
	private void setDesc(Integer score,String type){
		String record = teammember.getName();
		if(score != null){
			record += "$"+score;
		}
		else{
			record += "$-999999";
		}
		record += "$"+type + "$" + new Date().getTime();
		setDesc(record);
	}
	
	public GameResultStatistic(GameResult result,GameTeammember teammember,Integer score,String type){
		this();
		this.result = result;
		this.teammember = teammember;
		setDesc(score, type);
	}


	public GameResultStatistic(){}
	
	public String getTeamName(){
		return teammember.getTeam().getName();
	}
	
	public String getTeammemeberName(){
		return teammember.getName();
	}
	
	public String getStatisticType(){
		return this.desc.split("\\$")[2];
	}
	
	public int getScore(){
		return Integer.valueOf(this.desc.split("\\$")[1]);
	}
	
	public Date getTime(){
		return new Date(Integer.valueOf(this.desc.split("\\$")[3]));
	}
	
}
