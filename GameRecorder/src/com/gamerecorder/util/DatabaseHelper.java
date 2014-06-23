/*******************************************************************************
 * Copyright (c) 2012 Manning
 * See the file license.txt for copying permission.
 ******************************************************************************/
package com.gamerecorder.util;

import java.sql.SQLException;
import java.util.concurrent.Callable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gamerecorder.model.GameList;
import com.gamerecorder.model.GameResult;
import com.gamerecorder.model.GameResultDetail;
import com.gamerecorder.model.GameTeam;
import com.gamerecorder.model.GameTeammember;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * A SqliteOpenHelper that we will use as a singleton in our application.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	public static final String TAG = "DatabaseHelper";
	public static final String DATABASE_NAME = "gamerecord.db";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseHelper instance;

	/**
	 * @param c
	 *            A context to use when initializing the database for the first
	 *            time.
	 * @return A single instance of DatabaseHelper.
	 */
	public static synchronized DatabaseHelper getInstance(Context c) {
		if (instance == null)
			instance = new DatabaseHelper(c);

		return instance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource) {
		// Here, TableUtils will use the Annotations on our classes to create
		// all of
		// the tables in our relation.
		try {
			/*
			 * When using foreign keys, we must be sure to create tables in the
			 * proper order. For example, since the ArticleCategory table has
			 * foreign keys to both the Article and Category tables, we must
			 * declare ArticleCategory after the two others.
			 */
			TableUtils.createTable(connectionSource, GameList.class);
			TableUtils.createTable(connectionSource, GameTeam.class);
			TableUtils.createTable(connectionSource, GameTeammember.class);
			TableUtils.createTable(connectionSource, GameResult.class);
			TableUtils.createTable(connectionSource, GameResultDetail.class);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to create tables.", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			// When using foreign keys, we must be sure to drop tables in the
			// proper
			// order, again.
			TableUtils.dropTable(connectionSource, GameList.class, false);
			TableUtils.dropTable(connectionSource, GameTeam.class, false);
			TableUtils.dropTable(connectionSource, GameTeammember.class, false);
			TableUtils.dropTable(connectionSource, GameResult.class, false);
			TableUtils.dropTable(connectionSource, GameResultDetail.class, false);

			onCreate(sqLiteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to drop tables.", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		db.execSQL("PRAGMA foreign_keys=ON;");
	}

	/**
	 * Creates a TransactionManager, and runs the given callback.
	 * 
	 * @param callback
	 *            A Callable to run from inside a transaction.
	 * @param <T>
	 *            The return type of the Callable.
	 * @return The result of the Callable that was passed in.
	 */
	public <T> T callInTransaction(Callable<T> callback) {
		try {
			return new TransactionManager(getConnectionSource())
					.callInTransaction(callback);
		} catch (SQLException e) {
			Log.e(TAG, "Exception occurred in transaction.", e);
			throw new RuntimeException(e);
		}
	}

	/* DAO object accessors with specific types. */

	public Dao<GameList, Integer> getGameListDao() throws SQLException {
		return getDao(GameList.class);
	}

	public Dao<GameResult, Integer> getGameResultDao() throws SQLException {
		return getDao(GameResult.class);
	}

	public Dao<GameResultDetail, Integer> getGameResultDetailDao() throws SQLException {
		return getDao(GameResultDetail.class);
	}

	public Dao<GameTeam, Integer> getGameTeamDao() throws SQLException {
		return getDao(GameTeam.class);
	}

	public Dao<GameTeammember, Integer> getGameTeammemberDao() throws SQLException {
		return getDao(GameTeammember.class);
	}

}
