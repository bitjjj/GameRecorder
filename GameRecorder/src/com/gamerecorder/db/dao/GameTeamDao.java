package com.gamerecorder.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.gamerecorder.db.model.GameTeam;
import com.gamerecorder.util.DatabaseHelper;
import com.j256.ormlite.stmt.PreparedQuery;

public class GameTeamDao {

	private DatabaseHelper dbHelper;

	public GameTeamDao(Context ctx) {
		dbHelper = DatabaseHelper.getInstance(ctx);
	}

	public void create(GameTeam team) {
		try {
			dbHelper.getGameTeamDao().create(team);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(GameTeam team) {
		try {
			dbHelper.getGameTeamDao().update(team);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public GameTeam queryByName(String teamName) {
		try {
			return dbHelper.getGameTeamDao().queryBuilder().where()
					.eq(GameTeam.COLUMN_NAME, teamName).query().get(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<GameTeam> queryByMark(int mark) {
		PreparedQuery<GameTeam> query;
		List<GameTeam> teams = new ArrayList<GameTeam>();
		try {
			query = dbHelper.getGameTeamDao().queryBuilder().where().ne(GameTeam.COLUMN_MARK, mark).prepare();
			teams = dbHelper.getGameTeamDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return teams;
	}

}
