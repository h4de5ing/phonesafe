package com.code19.safe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/13.
 * 23:59
 */
public class ApplockDao {
    private Context mContext;
    private final AppLockDBHelper mHelper;

    public ApplockDao(Context context) {
        this.mContext = context;
        mHelper = new AppLockDBHelper(context);
    }

    //添加包名add
    public boolean add(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ApplockDB.Applock.COLUMN_PACKAGENAME, packageName);
        long insert = database.insert(ApplockDB.Applock.TABLE_NAME, null, values);
        database.close();
        return insert > 0;
    }

    //删除包名
    public boolean delete(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        int delete = database.delete(ApplockDB.Applock.TABLE_NAME, ApplockDB.Applock.COLUMN_PACKAGENAME + "=?", new String[]{packageName});
        database.close();
        return delete > 0;
    }

    //查询是否上锁
    public boolean findLock(String packageName) {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        String sql = "selcect count(1) from " + ApplockDB.Applock.TABLE_NAME + " where " + ApplockDB.Applock.COLUMN_PACKAGENAME + "=?";
        Cursor cursor = database.rawQuery(sql, new String[]{packageName});
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

    //查询所有上锁的包名
    public List<String> findAll() {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        String sql = "selcect " + ApplockDB.Applock.COLUMN_PACKAGENAME + " from " + ApplockDB.Applock.TABLE_NAME;
        Cursor cursor = database.rawQuery(sql, null);
        List<String> list = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
            cursor.close();
        }
        database.close();
        return list;
    }

}
