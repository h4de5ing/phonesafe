package com.code19.safe.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * Created by Gh0st on 2015/9/13.
 * 18:33
 */
public class ServiceStateUtils {
    public static boolean isRunning(Context context, Class<? extends Service> clazz) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : list) {
            ComponentName service = info.service;
            String className = service.getClassName();
            if (className.equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
}
