package com.gamerecorder.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.gamerecorder.db.model.GameResult;
import com.gamerecorder.db.model.GameTeam;
import com.gamerecorder.util.DatabaseHelper;
import com.j256.ormlite.stmt.PreparedQuery;

public class GameResultDao {

	private DatabaseHelper dbHelper;

	public GameResultDao(Context ctx) {
		dbHelper = DatabaseHelper.getInstance(ctx);
	}

	public void create(GameResult result) {
		try {
			dbHelper.getGameResultDao().create(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(GameResult result) {
		try {
			dbHelper.getGameResultDao().update(result);
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
