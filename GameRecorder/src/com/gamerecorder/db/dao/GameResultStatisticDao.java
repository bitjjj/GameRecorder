package com.gamerecorder.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;

import com.gamerecorder.db.model.GameResultStatistic;
import com.gamerecorder.interfaces.Identity;
import com.gamerecorder.util.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
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

	public void del(GameResultStatistic stat) {
		try {
			dbHelper.getGameResultStatisticDao().delete(stat);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void delById(Identity stat) {
		try {
			dbHelper.getGameResultStatisticDao().deleteById(stat.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void delById(final List<Identity> stats) {
		try {
			final Dao<GameResultStatistic, Integer> dao = dbHelper.getGameResultStatisticDao();
			dao.callBatchTasks(new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					for (Identity stat : stats) {
						dao.deleteById(stat.getId());
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

	public List<GameResultStatistic> queryByMark(int mark) {
		PreparedQuery<GameResultStatistic> query;
		List<GameResultStatistic> stats = new ArrayList<GameResultStatistic>();
		try {
			query = dbHelper.getGameResultStatisticDao().queryBuilder().where()
					.ne(GameResultStatistic.COLUMN_MARK, mark).prepare();
			stats = dbHelper.getGameResultStatisticDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return stats;
	}

}
