package com.code19.safe.engine;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.smartandroid.sa.json.Gson;
import com.smartandroid.sa.json.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
    public static void SmsBackup(final Context context, final OnBackupListener listener) {
        //耗时操作用异步任务
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    ContentResolver resolver = context.getContentResolver();
                    Uri uri = Uri.parse("content://sms");
                    Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
                    List<SmsBean> list = new ArrayList<SmsBean>();
                    if (cursor != null) {
                        int smsCount = cursor.getColumnCount();
                        int progress = 0;
                        while (cursor.moveToNext()) {
                            SmsBean bean = new SmsBean();
                            bean.address = cursor.getString(0);
                            bean.date = Long.valueOf(cursor.getString(1));
                            bean.type = cursor.getInt(2);
                            bean.body = cursor.getString(3);
                            list.add(bean);
                            progress++;
                            publishProgress(progress, smsCount);
                            SystemClock.sleep(300);
                        }
                        cursor.close();
                    }

                    Gson gson = new Gson();
                    String json = gson.toJson(list);
                    File file = new File(Environment.getExternalStorageDirectory(), "SmsBackup.json");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(json.getBytes());
                    fos.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
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


    /**
     * 还原短信
     *
     * @param context 上下文
     */
    //new TypeToken<List<SmsBean>>().getType().getClass();
    public static void SmsRestore(final Context context, final OnRestoreListener listener) {
        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "SmsBackup.json");
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String json = reader.readLine();
                    reader.close();
                    Log.i(TAG, "恢复的短信是：" + json);
                    Gson gson = new Gson();
                    List<SmsBean> list = gson.fromJson(json, new TypeToken<List<SmsBean>>() {
                    }.getType());
                    Uri uri = Uri.parse("content://sms");
                    ContentResolver resolver = context.getContentResolver();
                    int count = list.size();
                    int progress = 0;
                    for (int i = 0; i < list.size(); i++) {
                        SmsBean bean = list.get(i);
                        ContentValues values = new ContentValues();
                        values.put("address", bean.address);
                        values.put("date", bean.date);
                        values.put("type", bean.type);
                        values.put("body", bean.body);
                        resolver.insert(uri, values);
                        progress++;
                        publishProgress(progress, count);
                    }
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (listener != null) {
                    listener.onProgress(values[0], values[1]);
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (listener != null) {
                    listener.onResult(result);
                }


            }
        }.execute();
    }


    public interface OnBackupListener {
        void onProgress(int progress, int max);

        void onResult(boolean result);
    }

    public interface OnRestoreListener {
        void onProgress(int progress, int max);

        void onResult(Boolean result);
    }

    private static class SmsBean {
        public String address;
        public long date;
        public int type;
        public String body;
    }
}
