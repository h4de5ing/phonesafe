package com.code19.safe.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/9/10.
 * 14:54
 */
public class ProtectingService extends Service {


    private static final String TAG = "ProtectingService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "保护服务开启...");
        Notification notification = new Notification();
        notification.tickerText = "手机卫士保护你的手机安全";
        //通知的布局
        notification.icon = R.mipmap.ic_launcher;
        notification.contentView = new RemoteViews(getPackageName(), R.layout.notification);
        //通知的延迟加载,延迟意图
        notification.contentIntent = PendingIntent.getActivity(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        startForeground(100, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "保护服务关闭...");
        super.onDestroy();
    }
}
