package com.code19.safe.db;

/**
 * Created by Gh0st on 2015/9/4.
 * 17:59
 */
public interface BackListDB {
    String DB_NAME = "black.db";
    int DB_VERSION = 1;

    /**
     * String sql = "create table black(_id integer primary key autoincrement,number text unique,type integer)";
     * db.execSQL(sql);
     */
    interface BLACKTABLE {
        String TABLE_NAME = "black";//表名

        String COLUMN_ID = "_id";//id
        String COLUMN_NUMBER = "number";//号码列
        String COLUMN_TYPE = "type";//类型列

        String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement," + COLUMN_NUMBER + " text unique," + COLUMN_TYPE + " integer" + ")";
    }
}
