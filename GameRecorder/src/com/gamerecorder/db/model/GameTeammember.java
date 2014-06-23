package com.gamerecorder.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = GameTeammember.TABLE_NAME)
public class GameTeammember {

	public static final String TABLE_NAME = "game_teammember";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_MARK = "special_mark";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESC = "desc";
	
	@DatabaseField(generatedId = true)
	public int id;
	
	@DatabaseField
	public String name,desc;
	
	@DatabaseField(columnName = COLUMN_MARK,defaultValue = "0")
	public int specialMark;
	
	@DatabaseField(foreign = true, canBeNull = false)
	public GameTeam team;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public GameTeam getTeam() {
		return team;
	}

	public void setTeam(GameTeam team) {
		this.team = team;
	}
	
	public GameTeammember(String name,GameTeam team){
		this();
		this.name = name;
		this.team = team;
	}

	public GameTeammember(){}
	
}
