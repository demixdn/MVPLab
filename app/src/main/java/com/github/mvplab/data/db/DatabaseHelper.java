package com.github.mvplab.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Date: 06.02.2017
 * Time: 14:56
 *
 * @author Aleks Sander
 *         Project MVPLab
 */

class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context) {
        super(context, DatabaseConst.DATABASE.NAME, null, DatabaseConst.DATABASE.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createNewOne(db);
    }

    private void createNewOne(SQLiteDatabase db) {
        db.execSQL(DatabaseConst.QUERY.CREATE_TABLE_USERS);
        db.execSQL(DatabaseConst.QUERY.CREATE_TABLE_POSTS);
        db.execSQL(DatabaseConst.QUERY.CREATE_TABLE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteOld(db);
        createNewOne(db);
    }

    private void deleteOld(SQLiteDatabase db) {
        db.execSQL(DatabaseConst.QUERY.DROP_TABLE_COMMENTS);
        db.execSQL(DatabaseConst.QUERY.DROP_TABLE_POSTS);
        db.execSQL(DatabaseConst.QUERY.DROP_TABLE_USERS);
    }
}
