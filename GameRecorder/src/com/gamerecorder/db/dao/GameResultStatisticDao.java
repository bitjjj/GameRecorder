package com.gamerecorder.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.gamerecorder.db.model.GameResult;
import com.gamerecorder.db.model.GameResultStatistic;
import com.gamerecorder.db.model.GameTeam;
import com.gamerecorder.util.DatabaseHelper;
import com.j256.ormlite.stmt.PreparedQuery;

public class GameResultStatisticDao {

	private DatabaseHelper dbHelper;

	public GameResultStatisticDao(Context ctx) {
		dbHelper = DatabaseHelper.getInstance(ctx);
	}

	public void create(GameResultStatistic stat) {
		try {
			dbHelper.getGameResultStatisticDao().create(stat);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update(GameResultStatistic stat) {
		try {
			dbHelper.getGameResultStatisticDao().update(stat);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<GameResultStatistic> queryByMark(int mark) {
		PreparedQuery<GameResultStatistic> query;
		List<GameResultStatistic> stats = new ArrayList<GameResultStatistic>();
		try {
			query = dbHelper.getGameResultStatisticDao().queryBuilder().where().ne(GameResultStatistic.COLUMN_MARK, mark).prepare();
			stats = dbHelper.getGameResultStatisticDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return stats;
	}

}
