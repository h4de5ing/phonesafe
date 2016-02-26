package com.code19.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * android 4.4以后就收不到短信了。。需要设置成为默认短信应用程序，但是也是可以读取短信的。
 */

public class smsReceiver extends BroadcastReceiver {
    private static final String TAG = "smsReceiver";

    public smsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "收到短信了");
    }
}
