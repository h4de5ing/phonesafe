package com.code19.safe;

import android.app.Application;
import android.util.Log;

/**
 * Created by Gh0st on 2015/9/16.
 * 10:27
 */
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.i(TAG, "有没有捕获的异常" + thread.getName() + ":" + ex.getStackTrace());
                ex.printStackTrace();
            }
        });
    }
}
