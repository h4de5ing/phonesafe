package com.code19.safe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;

/**
 * Created by Gh0st on 2015/9/7.
 * 20:58
 * 用户查询常用号码，四个方法
 * 查询父级数据
 * 查询孩子数据
 * 查询父级数据标题
 * 查询孩子数据内容
 */
public class CommonNumberDao {
    private static final String TAG = "CommonNumberDao";

    /**
     * @param context 上下文
     * @return 返回父级Item的数量
     */
    public static int getGroupCount(Context context) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, 0);
        String sql = "select count(1) from classlist";
        Cursor cursor = database.rawQuery(sql, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();
        return count;
    }

    /**
     * @param context       上下文
     * @param groupPosition 父级Item的位置
     * @return 返回此Item的孩子数量
     */
    public static int getChildCount(Context context, int groupPosition) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, 0);
        String sql = "select count(1) from table" + (groupPosition + 1);
        Cursor cursor = database.rawQuery(sql, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        database.close();
        Log.i(TAG, "孩子数：" + count);
        return count;
    }

    /**
     * @param context       根据传入的groupPostion返回name
     * @param groupPosition 父级Item位置
     * @return 父级item的名称
     */
    public static String getGroupTitle(Context context, int groupPosition) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, 0);
        String sql = "select name from classlist where idx=" + (groupPosition + 1);
        Cursor cursor = database.rawQuery(sql, null);
        String title = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                title = cursor.getString(0);
            }
            cursor.close();
        }
        database.close();
        Log.i(TAG, "父级item名称：" + title);
        return title;
    }

    public static String getChildContent(Context context, int groupPosition,int childPosition) {
        File file = new File(context.getFilesDir(), "commonnum.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, 0);
        String sql = "select name,number from table" + (groupPosition + 1)+" where _id="+(childPosition+1);
        Cursor cursor = database.rawQuery(sql, null);
        String content = null;
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String number = cursor.getString(1);
                content = name + "\n" + number;
            }
            cursor.close();
        }
        database.close();
        Log.i(TAG, "孩子的内容：" + content);
        return content;
    }

}
