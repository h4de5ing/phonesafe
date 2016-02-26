package com.code19.safe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gh0st on 2015/9/4.
 * 17:58
 */
public class BlackDBHelper extends SQLiteOpenHelper {

    public BlackDBHelper(Context context) {
        super(context, BackListDB.DB_NAME, null, BackListDB.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BackListDB.BLACKTABLE.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
