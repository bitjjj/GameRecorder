package com.gamerecorder.db.dao;

import java.sql.SQLException;

import android.content.Context;

import com.gamerecorder.db.model.GameTeammember;
import com.gamerecorder.util.DatabaseHelper;

public class GameTeammemberDao {

	private DatabaseHelper dbHelper;
	
	public GameTeammemberDao(Context ctx){
		dbHelper = DatabaseHelper.getInstance(ctx);
	}
	
	public void create(GameTeammember teammember){
		try {
			dbHelper.getGameTeammemberDao().create(teammember);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void update(GameTeammember member){
		try {
			dbHelper.getGameTeammemberDao().update(member);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public GameTeammember queryById(int id){
		try {
			return dbHelper.getGameTeammemberDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
}
