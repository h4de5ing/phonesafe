package com.code19.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.code19.safe.utils.Constants;
import com.code19.safe.utils.OkHttpClientManager;
import com.code19.safe.utils.PackageInfoUtils;
import com.code19.safe.utils.PreferenceUtils;
import com.squareup.okhttp.Request;

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
    private String appServicesAddress = getString(R.string.version_check);

    TextView tv_version;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    showUpdateDialog();
                    break;
                case THELASTVERSION:
                    load2home();
                    break;
                case UPDATELATER:  //点了稍后更新直接进入主页
                    load2home();
                    break;
                case NETERROR:
                    load2home();
                    break;
            }


        }
    };
    private File mFile;
    private String mNewVersionDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();  //初始化界面
        initEvent(); //初始化事件
        //initData(); //初始化数据
        //load2home();
    }

    //初始化界面
    private void initView() {
        //初始化View，显示版本号
        tv_version = (TextView) findViewById(R.id.splash_tv_version);
        int versionCode = PackageInfoUtils.getVersionCode(this);
        tv_version.setText(getString(R.string.version_name) + versionCode);
    }

    //初始化事件
    private void initEvent() {
        ShortCut(); //生成快捷方式
        checkVersion(); //测试版本
    }

    //第一次安装生成快捷图标
    private void ShortCut() {
        //生成一个快捷图标的icon，名称，点击后的动作
        boolean shortcut = PreferenceUtils.getBoolean(this, Constants.SHORTCUT, false);
        Log.i(TAG, "有没有图标:" + shortcut);
        if (!shortcut) {
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            //name
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.shortcut));
            //icon
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
            //点击的意图
            Intent clickIntent = new Intent(this, SplashActivity.class);
            clickIntent.setAction(getApplication().getPackageName());
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, true);
            sendBroadcast(intent);
            PreferenceUtils.putBoolean(this, Constants.SHORTCUT, true);
        }
    }

    //初始化数据
    private void initData() {
        // 拷贝号码归属地数据库
        copyNumberAddressDB();
        // 拷贝常用号码数据库
        copyCommonNumberDB();
        //拷贝病毒数据库文件
        copyVirusDB();

    }

    // 1.拷贝号码归属地数据库
    private void copyNumberAddressDB() {
        //每次初始化数据，判断文件是否存在
        mFile = new File(getFilesDir(), ANTIVIRUSDB);
        if (mFile.exists()) {
            return;//如果文件存在不需要解压，就不需要下面的了
        }
        //解压号码归属地数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(mFile);
                    Unzip(getApplicationContext().getResources().getAssets().open(ADDRESSDB), os);
                } catch (Exception e) {
                    //Log.i(TAG, "zip文件错误,尝试从网络获取...");
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
            return;
        }
        //耗时操作需要在子线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream os = null;
                InputStream is = null;
                try {
                    AssetManager am = getAssets();
                    is = am.open(ANTIVIRUSDB);
                    os = new FileOutputStream(new File(getFilesDir(), ANTIVIRUSDB));
                    copyAssets2Files(is, os);
                } catch (IOException e) {
                } finally {
                    release(os);
                    release(is);
                }
            }
        }).start();
    }


    //直接进入主页，延迟1000毫秒后，启动主界面并关闭自己
    private void load2home() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    //检测当前是否是最新版
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //利用google最新出的网络框架okHttp进行网络访问
                OkHttpClientManager.getAsyn(appServicesAddress+"/safe.json", new OkHttpClientManager.ResultCallback<String>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Log.i(TAG, "网络错误");
                        Message message = mHandler.obtainMessage();
                        message.what = NETERROR;
                        message.sendToTarget();
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int newVersion = jsonObject.getInt("newVersion");
                            mNewVersionDownload = jsonObject.getString("download");
                            int localVersion = PackageInfoUtils.getVersionCode(SplashActivity.this);//得到手机安装的版本号
                            if (newVersion > localVersion) { //版本号不一样，需要提示用户更新
                                Log.i(TAG, "有新版更新..服务器版本：" + newVersion);
                                Message message = mHandler.obtainMessage();
                                message.what = UPDATE;
                                message.sendToTarget();
                            } else {  //版本号一样,不需要更新，给handle发消息，直接进入主页
                                Log.i(TAG, "已经是最新版本");
                                Message message = mHandler.obtainMessage();
                                message.what = THELASTVERSION;
                                message.sendToTarget();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    //如果是最新版弹出提示框
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);//设置为不能取消
        builder.setTitle("温馨提示：");
        builder.setMessage("有新的版本，是否需要更新(可以跟上新版本描述信息)");
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadNewVersion();
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "稍后更新");
                Message message = mHandler.obtainMessage();
                message.what = UPDATELATER;
                message.sendToTarget();
            }
        });
        builder.show();
    }

    //如果用户点击了下载按钮，就下载并安装
    private void downloadNewVersion() {
        Log.i(TAG, "用户点了更新按钮");
        //接下来开始做下载新版本apk

        String cachePath = getCacheDir().getAbsolutePath();
        Log.i(TAG, "缓存地址:" + cachePath);
        OkHttpClientManager.getDownloadDelegate().downloadAsyn(appServicesAddress+mNewVersionDownload,cachePath, new OkHttpClientManager.ResultCallback<String>() {

            @Override
            public void onError(Request request, Exception e) {
                Log.i(TAG, "onError: ");


            }

            @Override
            public void onResponse(String response) {

            }
        });

    }

}
