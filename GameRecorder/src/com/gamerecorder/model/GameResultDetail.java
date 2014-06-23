package com.gamerecorder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = GameResultDetail.TABLE_NAME)
public class GameResultDetail {

	public static final String TABLE_NAME = "game_result_detail";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_MAKR = "special_mark";
	public static final String COLUMN_DESC = "desc";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	private String desc;
	
	@DatabaseField(columnName = COLUMN_MAKR)
	private int specialMark;
	
	@DatabaseField(foreign = true, canBeNull = false)
	private GameResult result;
	
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


	public GameResultDetail(){}
	
}
