package com.code19.safe.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.code19.safe.bean.BlackBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/4.
 * 18:04
 */
public class BlackDao {

    private static final String TAG = "BlackDao";
    private final BlackDBHelper mHelper;

    public BlackDao(Context context) {
        mHelper = new BlackDBHelper(context);
    }

    public boolean insert(String number, int type) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BackListDB.BLACKTABLE.COLUMN_NUMBER, number);
        values.put(BackListDB.BLACKTABLE.COLUMN_TYPE, type);
        long insert = database.insert(BackListDB.BLACKTABLE.TABLE_NAME, null, values);
        Log.i(TAG, "插入数据 " + insert);
        database.close();
        return insert != -1;
    }

    public boolean update(String number, int type) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        //public int update (String table, ContentValues values, String whereClause, String[] whereArgs)
        String table = BackListDB.BLACKTABLE.TABLE_NAME;
        ContentValues values = new ContentValues();
        values.put(BackListDB.BLACKTABLE.COLUMN_NUMBER, number);
        values.put(BackListDB.BLACKTABLE.COLUMN_TYPE, type);
        String whereClause = BackListDB.BLACKTABLE.COLUMN_NUMBER + "=?";
        String[] whereArgs = new String[]{number};
        int update = database.update(table, values, whereClause, whereArgs);
        Log.i(TAG, "更新数据" + update);
        database.close();
        return update > 0;
    }

    public boolean delete(String number) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        int delete = database.delete(BackListDB.BLACKTABLE.TABLE_NAME, BackListDB.BLACKTABLE.COLUMN_NUMBER + "=?", new String[]{number});
        Log.i(TAG, "删除数据" + delete);
        database.close();
        return delete > 0;
    }

    public List<BlackBean> queryAll() {
        List<BlackBean> list = new ArrayList<BlackBean>();
        SQLiteDatabase database = mHelper.getReadableDatabase();
        String table = BackListDB.BLACKTABLE.TABLE_NAME;
        String sql = "select " + BackListDB.BLACKTABLE.COLUMN_NUMBER + "," + BackListDB.BLACKTABLE.COLUMN_TYPE + " from " + table;
        Log.i(TAG, "查询语句" + sql);
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                int type = cursor.getInt(1);
                Log.i(TAG, "number:" + number + ",type:" + type);
                BlackBean bean = new BlackBean();
                bean.number = number;
                bean.type = type;
                list.add(bean);
            }
            cursor.close();
        }
        database.close();
        return list;
    }

}
