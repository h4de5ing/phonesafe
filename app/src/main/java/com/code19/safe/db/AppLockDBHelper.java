package com.code19.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Gh0st on 2015/9/13.
 * 23:58
 */
public class AppLockDBHelper extends SQLiteOpenHelper {


    public AppLockDBHelper(Context context) {
        super(context,ApplockDB.DB_NAME, null, ApplockDB.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ApplockDB.Applock.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
