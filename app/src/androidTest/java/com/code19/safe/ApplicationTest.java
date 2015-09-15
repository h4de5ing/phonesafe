package com.code19.safe;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;

import com.code19.safe.db.AntiVirusDao;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    void testAdd(Context context) {
        boolean add = AntiVirusDao.add(context, "d0d6cd48004240f095b89eabbf03146d", "黑马手机卫士");
        assertEquals(true,add);
    }
}