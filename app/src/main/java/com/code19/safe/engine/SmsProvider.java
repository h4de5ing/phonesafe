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
                        int smsCount = cursor.getColumnCount();//短信的总数
                        int progress = 0; //进度，保存的当前短信是第几条
                        while (cursor.moveToNext()) {
                            SmsBean bean = new SmsBean(); //用于存储短信的ben对象
                            bean.address = cursor.getString(0);
                            bean.date = Long.valueOf(cursor.getString(1));
                            bean.type = cursor.getInt(2);
                            bean.body = cursor.getString(3);
                            list.add(bean);
                            progress++;
                            publishProgress(progress, smsCount);//将进度条的当前进度和进度max值实时传给主线程
                            SystemClock.sleep(300);
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
                    return true; //备份成功，返回ture
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;//备份失败，返回false
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
                    //1.读取文件
                    File file = new File(Environment.getExternalStorageDirectory(), "SmsBackup.json");
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String json = reader.readLine();
                    reader.close();
                    Log.i(TAG, "恢复的短信是：" + json);
                    Gson gson = new Gson();
                    List<SmsBean> list = gson.fromJson(json, new TypeToken<List<SmsBean>>() {
                    }.getType());
                    //将集合插入到数据库
                    Uri uri = Uri.parse("content://sms");
                    ContentResolver resolver = context.getContentResolver();
                    int count = list.size();//短信条数就是进度的最大值
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
                        publishProgress(progress, count);//将当前进度和进度最大值传回
                    }
                    return true;
                } catch (FileNotFoundException e) {
                    Log.i(TAG, "文件没有找到");
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    Log.i(TAG, "IO流异常");
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                //拿到进度
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
        //当进度运行时的回调
        void onProgress(int progress, int max);

        //当结束时的回调
        void onResult(boolean result);
    }

    public interface OnRestoreListener {
        //当进度运行时回调
        void onProgress(int progress, int max);

        //当结果是回调
        void onResult(Boolean result);
    }

    private static class SmsBean {
        public String address;
        public long date;
        public int type;
        public String body;
    }
}
