package com.code19.safe.db;

/**
 * Created by Gh0st on 2015/9/13.
 * 23:59
 */
public class ApplockDB {
    public static final String DB_NAME = "applock";
    public static int VERSION = 1;

    public interface Applock {
        String TABLE_NAME = "applock";
        String COLUMN_ID = "_id";
        String COLUMN_PACKAGENAME = "packageName";

        String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement,"+COLUMN_PACKAGENAME+" text unique)";
    }
}
