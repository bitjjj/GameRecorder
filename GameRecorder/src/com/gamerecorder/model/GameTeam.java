package com.gamerecorder.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = GameTeam.TABLE_NAME)
public class GameTeam {

	public static final String TABLE_NAME = "game_team";
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
	
	@ForeignCollectionField(eager = false)
	private ForeignCollection<GameTeammember> members;

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
	
	public ForeignCollection<GameTeammember> getMembers() {
		return members;
	}

	public GameTeam(String name){
		super();
		
		this.name = name;
	}
	
	public GameTeam(int id,String name,int mark){
		super();
		this.id = id;
		this.name = name;
		this.specialMark = mark;
	}
	
	public GameTeam(){}
	
}
