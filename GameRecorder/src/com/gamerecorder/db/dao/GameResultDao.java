package com.gamerecorder.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;

import com.gamerecorder.db.model.GameResult;
import com.gamerecorder.interfaces.Identity;
import com.gamerecorder.util.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
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
	
	public GameResult queryById(int id){
		try {
			return dbHelper.getGameResultDao().queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public List<GameResult> queryByMark(int mark) {
		PreparedQuery<GameResult> query;
		List<GameResult> results = new ArrayList<GameResult>();
		try {
			query = dbHelper.getGameResultDao().queryBuilder().where().ne(GameResult.COLUMN_MARK, mark).prepare();
			results = dbHelper.getGameResultDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public List<GameResult> queryByGameKind(String kind) {
		PreparedQuery<GameResult> query;
		List<GameResult> results = new ArrayList<GameResult>();
		try {
			query = dbHelper.getGameResultDao().queryBuilder().where().eq(GameResult.COLUMN_KIND, kind).prepare();
			results = dbHelper.getGameResultDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public void delById(final List<Identity> results) {
		try {
			final Dao<GameResult, Integer> dao = dbHelper.getGameResultDao();
			dao.callBatchTasks(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					for (Identity result : results) {
						dao.deleteById(result.getId());
					}
					return null;
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
