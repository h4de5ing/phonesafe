package com.code19.safe.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by Gh0st on 2015/9/6.
 * 19:28
 * 查询号码归属地数据库
 */
public class AddressDao {

    private static final String TAG = "AddressDao";
    private static String tmpNumber;

    public static String findAddress(Context context, String number) {
        String mAddress = "未知号码";
        File file = new File(context.getFilesDir(), "address.db");
        SQLiteDatabase database = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        //首先判断是否是手机号码，如果不是，就不需要进行数据库查询，节省资源
        boolean isPhoneNumber = number.matches("^1[34578]\\d{9}$");
        Log.i(TAG, "是否是电话号码：" + isPhoneNumber);
        if (isPhoneNumber) {//如果是手机号码就截取前7位进行判断进行查询
            tmpNumber = number.substring(0, 7);
            String sql = "select cardtype from info where mobileprefix=?";
            Cursor cursor = database.rawQuery(sql, new String[]{tmpNumber});
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    mAddress = cursor.getString(0);
                }
                cursor.close();
            }
        } else {//11的号码，另外判断
            switch (number.length()) {
                case 3:
                    mAddress = "报警电话";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务号码";
                    break;
                case 6:
                case 7:
                case 8:
                    mAddress = "本地座机";
                case 9:
                case 10:
                case 11:
                case 12:  //取前3或4位，根据区号判断
                    tmpNumber = number.substring(0, 3);
                    String sql = "select cardtype from info where area=?";
                    Cursor cursor = database.rawQuery(sql, new String[]{tmpNumber});
                    if (cursor != null) {
                        if (cursor.moveToNext()) {
                            mAddress = cursor.getString(0);
                        }
                        cursor.close();
                    }
                    if (TextUtils.isEmpty(mAddress)) {
                        tmpNumber = number.substring(0, 4);
                        sql = "select cardtype from info where area=?";
                        cursor = database.rawQuery(sql, new String[]{tmpNumber});
                        if (cursor != null) {
                            if (cursor.moveToNext()) {
                                mAddress = cursor.getString(0);
                            }
                            cursor.close();
                        }
                    }
                    break;
                default:
                    if (number.length() > 16) {
                        mAddress = "警惕号码";
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
            }
        }
        database.close();
        return mAddress;
    }
}
