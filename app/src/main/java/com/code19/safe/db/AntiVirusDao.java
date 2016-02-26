package com.code19.safe.db;

import android.content.ContentValues;
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

    private static final String TAG = "AntiVirusDao";

    public static boolean isVirus(Context context, String md5) {
        File file = new File(context.getFilesDir(), "antivirus.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select count(1) from datable where md5=?";
        Cursor cursor = database.rawQuery(sql, new String[]{md5});
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();
        return count > 0;
    }

    public static boolean add(Context context, String md5, String name) {
        File file = new File(context.getFilesDir(), "antivirus.db");

        SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
                null, SQLiteDatabase.OPEN_READWRITE);

        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("type",6);
        values.put("name", name);
        values.put("desc", "让用户无法删除的程序");
        long insert = db.insert("datable", null, values);
        db.close();
        return insert != -1;
    }
}

