package com.code19.safe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * Created by Gh0st on 2015/9/14.
 * 17:01
 * select count(1) from datable where md5=?
 */
public class AntiVirusDao {

    public static boolean isVirus(Context context, String md5) {
        File file = new File(context.getFilesDir(), "antivirus.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select count(1) from datable where md5=?";
        Cursor cursor = database.rawQuery(sql, new String[]{md5});
        int i = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                i = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();
        return i > 0;
    }
}

