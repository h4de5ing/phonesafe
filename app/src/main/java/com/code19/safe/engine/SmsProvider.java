package com.code19.safe.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.smartandroid.sa.json.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/11.
 * 16:07
 */
public class SmsProvider {
    private static final String TAG = "SmsProvider";

    /**
     * 短信备份：数据存储，提供UI接口
     *
     * @param context  上下文
     * @param listener 监听器
     */
    private static void SmsBackup(final Context context, final OnBackupListener listener) {
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms");
                    Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
                    List<SmsBean> list = new ArrayList<SmsBean>();
                    if (cursor != null) {
                        int smsCount = cursor.getColumnCount();//短信的总数
                        SmsBean bean = new SmsBean(); //用于存储短信的ben对象
                        int progress = 0; //进度，保存的当前短信是第几条
                        while (cursor.moveToNext()) {
                            bean.address = cursor.getString(0);
                            bean.date = Long.valueOf(cursor.getString(1));
                            bean.type = cursor.getInt(2);
                            bean.body = cursor.getString(3);
                            list.add(bean);
                            progress++;
                            publishProgress(progress, smsCount);//将进度条的当前进度和进度max值实时传给主线程
                        }
                        cursor.close();
                    }
                    //数据持久化
                    Gson gson = new Gson();
                    String json = gson.toJson(list);
                    File file = new File(Environment.getExternalStorageDirectory(), "SmsBackup.json");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(json.getBytes());
                    fos.close();
                    return true; //走到这而备份成功
                } catch (Exception e) {
                    //e.printStackTrace();
                    return false;//走到这儿备份失败
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                //拿到publishProgress 传的数据
                if (listener != null) {
                    listener.onProgress(values[0], values[1]);
                }
            }

            //同步结束执行的结果
            @Override
            protected void onPostExecute(Boolean result) {
                if (listener != null) {
                    listener.onResult(result);
                }
            }
        }.execute();
    }

    //new TypeToken<List<SmsBean>>().getType().getClass();

    /**
     * 还原短信
     *
     * @param context 上下文
     */
    public static void SmsRestore(Context context, OnREstoreListener listener) {

    }


}

interface OnBackupListener {
    void onProgress(int progress, int max);

    void onResult(boolean result);
}

interface OnREstoreListener {
}

class SmsBean {
    public String address;
    public long date;
    public int type;
    public String body;
}