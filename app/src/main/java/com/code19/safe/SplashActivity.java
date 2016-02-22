package com.code19.safe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.code19.safe.bean.VersionBean;
import com.code19.safe.utils.OkHttpClientManager;
import com.code19.safe.utils.PackageInfoUtils;
import com.google.gson.Gson;
import com.squareup.okhttp.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.code19.safe.utils.GZIPUtils.Unzip;
import static com.code19.safe.utils.GZIPUtils.copyAssets2Files;
import static com.code19.safe.utils.GZIPUtils.release;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final String ANTIVIRUSDB = "antivirus.db"; //病毒数据库
    private static final String COMMONNUMDB = "commonnum.db"; //常用号码数据库
    private static final String ADDRESSDB = "address.zip"; //号码归属地号码数据库
    private String appServicesAddress = getString(R.string.version_check);

    TextView tv_version;

    private File mFile;
    private String mNewVersionDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();  //初始化界面
        init(); //初始化数据
    }

    private void initView() {
        int versionCode = PackageInfoUtils.getVersionCode(this);
        tv_version = (TextView) findViewById(R.id.splash_tv_version);
        tv_version.setText(getResources().getString(R.string.version_name, versionCode));
    }

    //初始化数据
    private void init() {
        // 拷贝号码归属地数据库
        //copyNumberAddressDB();
        // 拷贝常用号码数据库
       //copyCommonNumberDB();
        //拷贝病毒数据库文件
        //copyVirusDB();
        startActivity(new Intent(SplashActivity.this, HomeActivity.class)); //进入主页
    }

    // 1.拷贝号码归属地数据库
    private void copyNumberAddressDB() {
        mFile = new File(getFilesDir(), ANTIVIRUSDB);
        if (mFile.exists()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(mFile);
                    Unzip(getApplicationContext().getResources().getAssets().open(ADDRESSDB), os);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    release(os);
                }
            }
        }).start();
    }

    // 2.拷贝常用号码数据库
    private void copyCommonNumberDB() {
        if (new File(getFilesDir(), COMMONNUMDB).exists()) {
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
                    e.printStackTrace();
                } finally {
                    release(os);
                }
            }
        }).start();
    }

    //3.拷贝病毒特征数据库
    private void copyVirusDB() {
        if (new File(getFilesDir(), ANTIVIRUSDB).exists()) {
            return;
        }
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
                    e.printStackTrace();
                } finally {
                    release(os);
                    release(is);
                }
            }
        }).start();
    }

    //检测当前是否是最新版
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //利用google最新出的网络框架okHttp进行网络访问
                OkHttpClientManager.getAsyn(appServicesAddress + "/app.json", new OkHttpClientManager.ResultCallback<String>() {

                    @Override
                    public void onError(Request request, Exception e) {
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        VersionBean versionBean = gson.fromJson(response, VersionBean.class);
                        int Servicesersion = Integer.parseInt(versionBean.getAPPVersion().get(3).getVersion());
                        int localVersion = PackageInfoUtils.getVersionCode(SplashActivity.this);//得到手机安装的版本号
                        if (Servicesersion > localVersion) { //版本号不一样，需要提示用户更新
                            Log.i(TAG, "有新版更新..服务器版本：" + Servicesersion);
                        } else {  //版本号一样,不需要更新，给handle发消息，直接进入主页
                            Log.i(TAG, "已经是最新版本");
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
        OkHttpClientManager.getDownloadDelegate().downloadAsyn(appServicesAddress + mNewVersionDownload, cachePath, new OkHttpClientManager.ResultCallback<String>() {

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
