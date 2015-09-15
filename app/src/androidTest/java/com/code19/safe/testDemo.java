package com.code19.safe;

import android.content.Context;
import android.test.ApplicationTestCase;

import com.code19.safe.db.AntiVirusDao;

/**
 * Created by Gh0st on 2015/9/14.
 * 0:09
 */
public class testDemo extends ApplicationTestCase {


    public testDemo(Class applicationClass) {
        super(applicationClass);
    }


    void testAdd(Context context) {
        boolean add = AntiVirusDao.add(context, "d0d6cd48004240f095b89eabbf03146d", "黑马手机卫士");
    }
}
