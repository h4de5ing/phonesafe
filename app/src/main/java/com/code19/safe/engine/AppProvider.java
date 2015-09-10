package com.code19.safe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.code19.safe.bean.AppBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/7.
 * 19:02
 * 需要的到的信息：
 * public Drawable icon; //应用图标
 * public String name; //应用名称
 * public String packageName;//包名
 * public long size; //应用大小
 * public boolean inInstallSD;//是否安装在SD卡上
 * public boolean isSystem;//是否是系统程序
 */
public class AppProvider {
    private static final String TAG = "app信息获取";
    private Context mContext;
    private static PackageManager sPackageManager;

    public AppProvider(Context context) {
        mContext = context;
    }

    public static List<AppBean> getAllApps(Context context) {
        //获取系统应用
        List<AppBean> list = new ArrayList<AppBean>();
        //获得包管理器
        sPackageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = sPackageManager.getInstalledApplications(sPackageManager.GET_UNINSTALLED_PACKAGES);
        //sPackageManager.getInstalledPackages(0);
        //排序
        //Collections.sort(installedApplications, new ApplicationInfo.DisplayNameComparator(sPackageManager));

        for (ApplicationInfo app : installedApplications) {
            //创建appBean，用于存储应用的 icon,name,packagename,size,isinstallsd,issystem等信息
            AppBean appBean = new AppBean();
            appBean.icon = app.loadIcon(sPackageManager);
            appBean.name = (String) app.loadLabel(sPackageManager);
            appBean.packageName = app.packageName;
            appBean.size = new File(app.sourceDir).length();
            Log.i(TAG, "app资源路径 " + app.sourceDir + "," + app.dataDir);
            //appBean.size = new File(app.dataDir).length();
            int flags = app.flags;//得到当前应用的flags能力
            //将当前应用的能力与上系统的所有能力，取出当前应用的能力
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                appBean.isSystem = true;
            } else {
                appBean.isSystem = false;
            }

            // appBean.isSystem = (flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                appBean.inInstallSD = true;
            } else {
                appBean.inInstallSD = false;
            }
            list.add(appBean);
        }
        Log.i(TAG, "bean数据添加完成" + list.size());
        return list;
    }


}
