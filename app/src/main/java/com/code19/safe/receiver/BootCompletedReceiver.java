package com.code19.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.code19.safe.R;
import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;

/**
 * Created by Gh0st on 2015/9/5.
 * 17:00
 * 通过监听手机是否重启来判断是否有更换sim，如果sim更好了，就发送报警短信。
 * 然后在根据接受到的指令进行GPS追踪，播放报警声音，锁定屏幕，擦除数据等操作
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "手机重启了：");
        //判断是否开启了手机防盗保护,如果没有开启，不做任何操作
        boolean protecting = PreferenceUtils.getBoolean(context, Constants.PROTECTING);
        if (!protecting) {
            return;
        }

        //检测sim卡是否变更了
        //获得当前sim卡信息
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currentSimSerialNumber = tm.getSimSerialNumber();
        //获得存储的sim信息
        String sim = PreferenceUtils.getString(context, Constants.SIM_BIND);
        Log.i(TAG, "当前sim信息 " + currentSimSerialNumber + ",存储的sim卡信息" + sim);
        if (currentSimSerialNumber.equals(sim)) {//一致，没有被盗，如果不一致就被盗了，需要发送报警短信
            return;
        }
        //走到这儿说明被盗了，需要给安全号码发送报警短息
        //获得安全号码
        String safeNnmber = PreferenceUtils.getString(context, Constants.SAFE_NUMBER);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(safeNnmber, null, context.getResources().getString(R.string.safeSms), null, null);
        Log.i(TAG, "发送报警短信：" + context.getResources().getString(R.string.safeSms));
        //接下来就交给短信接收者等待丢失手机的用户发送指令smsReceiver
    }
}
