package com.code19.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PackageInfoUtils;
import com.code19.safe.utils.PreferenceUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.code19.safe.utils.GZIPUtils.Unzip;
import static com.code19.safe.utils.GZIPUtils.copyAssets2Files;
import static com.code19.safe.utils.GZIPUtils.release;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final int THELASTVERSION = 100;//已经是最新版
    private static final int UPDATELATER = 200;//用户点了稍后更新
    private static final int UPDATE = 300;//版本不一致时，需要更新
    private static final int NETERROR = 404;//网络异常
    private static final String ANTIVIRUSDB = "antivirus.db"; //病毒数据库
    private static final String COMMONNUMDB = "commonnum.db"; //常用号码数据库
    private static final String ADDRESSDB = "address.zip"; //号码归属地号码数据库

    TextView tv_version;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    showUpdateDialog();
                case THELASTVERSION:
                    // load2home();
                    break;
                case UPDATELATER:  //点了稍后更新直接进入主页
                    //load2home();
                    break;
                case NETERROR:
                    Log.i(TAG, "handler:" + msg.obj);
                    load2home();
                    break;
            }


        }
    };
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //生成一个快捷图标
        boolean shortcut = PreferenceUtils.getBoolean(this, Constants.SHORTCUT, false);
//        if (没有生成)
        Intent intent = new Intent();

        sendBroadcast(intent);

        //初始化View，显示版本号
        tv_version = (TextView) findViewById(R.id.splash_tv_version);
        int versionCode = PackageInfoUtils.getVersionCode(this);
        tv_version.setText("版本号:" + versionCode);
        //检测版本
        //checkVersion(); //测试版本，先不检测版本更新
        //加载主界面
        load2home();

        // 拷贝号码归属地数据库
        // copyNumberAddressDB();

        // 拷贝常用号码数据库
        //copyCommonNumberDB();
        //拷贝病毒数据库文件
        copyVirusDB();
    }

    // 1.拷贝号码归属地数据库
    private void copyNumberAddressDB() {
        //每次初始化数据，判断文件是否存在
        mFile = new File(getFilesDir(), ANTIVIRUSDB);
        if (mFile.exists()) {
            Log.i(TAG, "文件存在，不需要解压了");
            return;//如果文件存在不需要解压，就不需要下面的了
        }
        //解压号码归属地数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "子线程准备执行解压号码归属地文件");
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(mFile);
                    Unzip(getApplicationContext().getResources().getAssets().open(ADDRESSDB), os);
                    Log.i(TAG, "号码归属文件解压成功");
                } catch (Exception e) {
                    Log.i(TAG, "zip文件错误,尝试从网络获取...");
                } finally {
                    release(os);
                }
            }
        }).start();

    }

    // 2.拷贝常用号码数据库
    private void copyCommonNumberDB() {
        File localFile = new File(getFilesDir(), COMMONNUMDB);
        if (localFile.exists()) {
            Log.i(TAG, "文件存在，就不拷贝了...");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(new File(getFilesDir(), COMMONNUMDB));
                    copyAssets2Files(getApplicationContext().getResources().getAssets().open(COMMONNUMDB), os);
                } catch (IOException e) {
                    Log.i(TAG, "常用号码数据库文件找不到,就可以尝试从服务器获取");//如果找不到可以尝试从服务器上获取
                } finally {
                    release(os);
                }
            }
        }).start();
    }

    //3.拷贝病毒特征数据库
    private void copyVirusDB() {
        //拷贝前判断文件是否存在，如果存在就不拷贝了，节省资源
        File file = new File(getFilesDir(), ANTIVIRUSDB);
        if (file.exists()) {
            Log.i(TAG, "病毒库已经存在，不需要拷贝了 ");
            return;
        }
        //耗时操作需要在子线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "子线程执行了没有 ");
                FileOutputStream os = null;
                InputStream is = null;
                try {
                    AssetManager am = getAssets();
                    is = am.open(ANTIVIRUSDB);
                    os = new FileOutputStream(new File(getFilesDir(), ANTIVIRUSDB));
                    copyAssets2Files(is, os);
                    Log.i(TAG, "病毒特征库拷贝成功");
                } catch (IOException e) {
                    Log.i(TAG, "病毒特征库拷贝失败,尝试从服务器获取... ");
                } finally {
                    release(os);
                    release(is);
                }
            }
        }).start();
    }

    //检测版本
    private void checkVersion() {
        new Thread(new CheckVersionTask()).start();
    }

    //加载到主页
    private void load2home() {
        mHandler.postDelayed(new Runnable() {
            //主线程中执行,延迟1500毫秒后，启动主界面并关闭自己
            @Override
            public void run() {
                Log.d(TAG, "Thread" + Thread.currentThread());
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }


    //在子线程中访问网络
    private class CheckVersionTask implements Runnable {

        @Override
        public void run() {
            Log.i("SplashActivity:", "子线程运行");
            String uri = "http://188.188.7.1:8080/config.json";
            AndroidHttpClient androidHttpClient = AndroidHttpClient.newInstance("19code.com", SplashActivity.this);
            HttpConnectionParams.setConnectionTimeout(androidHttpClient.getParams(), 5000);//设置请求超时
            HttpConnectionParams.setSoTimeout(androidHttpClient.getParams(), 5000);//设置响应超时
            HttpGet get = new HttpGet(uri);
            try {
                HttpResponse response = androidHttpClient.execute(get);
                // 获得响应码
                int statusCode = response.getStatusLine().getStatusCode();
                if (200 == statusCode) { //响应码为200，表示访问成功
                    Log.i(TAG, "响应状态码：" + statusCode);
                    String json = EntityUtils.toString(response.getEntity(), "utf-8");
                    Log.i(TAG, "返回的json:" + json);
                    JSONObject jsonObject = new JSONObject(json);
                    int netVersion = jsonObject.getInt("versionCode");//得到服务器上的版本号
                    Log.i(TAG, "服务器上的版本：" + netVersion);
                    int localVersion = PackageInfoUtils.getVersionCode(SplashActivity.this);//得到手机安装的版本号
                    Log.i(TAG, "本机版本:" + localVersion + ",服务器版本:" + netVersion);
                    if (netVersion > localVersion) {//版本号不一样，需要提示用户更新
                        Log.i(TAG, "版本号不一致，提示更新");

                        Message message = mHandler.obtainMessage();
                        message.what = UPDATE;
                        message.sendToTarget();

                    } else {  //版本号一样,不需要更新，给handle发消息，进入主页
                        Message message = mHandler.obtainMessage();
                        message.what = THELASTVERSION;
                        message.sendToTarget();
                    }
                }
            } catch (IOException e) {
                Message message = mHandler.obtainMessage();
                message.obj = "网络异常，就直接进入主页把。";
                message.what = NETERROR;
                Log.i("SplashActivity", "访问异常,服务器开了吗？，你的设备能连接上这个网络？");
                message.sendToTarget();
                //e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                androidHttpClient.close();
            }
        }

    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);//设置为不能取消
        builder.setTitle("温馨提示：");
        builder.setMessage("有新的版本，是否需要更新(可以跟上新版本描述信息)");
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //更新操作
                downloadNewVersion();
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "用户点击了稍后更新按钮");
                //用户点了稍后更新按钮，直接进入主界面
                Message message = mHandler.obtainMessage();
                message.what = UPDATELATER;
                message.sendToTarget();
            }
        });
        builder.show();
    }

    private void downloadNewVersion() {
        Log.i(TAG, "用户点了更新按钮");
        //接下来开始做下载新版本apk
    }

}
