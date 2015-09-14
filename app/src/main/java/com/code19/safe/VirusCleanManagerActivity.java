package com.code19.safe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.code19.safe.bean.AntiVirusBean;
import com.code19.safe.db.AntiVirusDao;
import com.code19.safe.utils.Md5Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/13.
 * 0:16
 */
public class VirusCleanManagerActivity extends Activity {

    private static final String TAG = "VirusCleanManagerActivity";
    private PackageManager mPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virusclean);
        mPm = getPackageManager();
        initView();
        //initData();

    }

    //初始化界面
    private void initView() {

    }

    /**
     * 扫描所有的应用，获得应用的指纹，然后到数据库中去比对md5值
     */
    private void initData() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                List<PackageInfo> list = mPm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                for (PackageInfo pkg : list) {
                    //获取apk文件，获取apk文件指纹
                    //拿到apk文件
                    String sourceDir = pkg.applicationInfo.sourceDir;
                    Log.i(TAG, "initData " + sourceDir);
                    FileInputStream in = null;
                    int progress = 0;
                    try {
                        in = new FileInputStream(new File(sourceDir));
                        String md5 = Md5Utils.encode(in);
                        //查询数据对比是否病毒
                        boolean virus = AntiVirusDao.isVirus(getApplicationContext(), md5);
                        ApplicationInfo applicationInfo = mPm.getApplicationInfo(pkg.packageName, 0);
                        String name = applicationInfo.loadLabel(mPm).toString();
                        Drawable icon = applicationInfo.loadIcon(mPm);
                        String packageName = pkg.packageName;
                        AntiVirusBean bean = new AntiVirusBean();
                        bean.icon = icon;
                        bean.name = name;
                        bean.packageName = packageName;

                        SystemClock.sleep(100);//线程小睡一会
                        publishProgress();//传数据
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            in = null;
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {

            }
        };
    }
}

