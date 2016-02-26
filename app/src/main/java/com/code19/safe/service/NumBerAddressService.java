package com.code19.safe.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Gh0st on 2015/9/10.
 * 15:54
 */
public class NumBerAddressService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
