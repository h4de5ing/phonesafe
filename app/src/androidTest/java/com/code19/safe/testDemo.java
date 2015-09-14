package com.code19.safe;

import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import com.code19.safe.db.AppLockDBHelper;

/**
 * Created by Gh0st on 2015/9/14.
 * 0:09
 */
public class testDemo extends ApplicationTestCase {
    public testDemo(Class applicationClass) {
        super(applicationClass);
    }

    public void testDbHelper() {
        AppLockDBHelper helper = new AppLockDBHelper(getContext());
        SQLiteDatabase database = helper.getWritableDatabase();
    }
}
