package com.code19.safe.engine;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.code19.safe.bean.ProcessBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Gh0st on 2015/9/10.
 * 0:00
 */
public class ProcessProvider {

    private static final String TAG = "进程信息：";

    /**
     * @param context 上下文
     * @return 返回正在运行的进程数量
     */
    public static int getRuningProcessCount(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        Log.i(TAG, "getRuningProcessCount 运行进程数量" + runningAppProcesses.size());
        return runningAppProcesses.size();
    }

    /***
     * 获取所有的进程数量
     * 通过包管理器，获取四大组件的进程，并进行去重(利用set的特性)
     *
     * @return 所有进程的数量
     */
    public static int getTotalProcessCout(Context context) {

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);

        Set<String> set = new HashSet<String>(); //利用set去重

        for (PackageInfo pkg : packages) {

            ActivityInfo[] activities = pkg.activities;
            if (activities != null) {
                for (ActivityInfo activityInfo : activities) {
                    set.add(activityInfo.processName);
                }
            }

            ServiceInfo[] services = pkg.services;
            if (services != null) {
                for (ServiceInfo serviceInfo : services) {
                    set.add(serviceInfo.processName);
                }
            }

            ProviderInfo[] providers = pkg.providers;
            if (providers != null) {
                for (ProviderInfo providerInfo : providers) {
                    set.add(providerInfo.packageName);
                }
            }

            ActivityInfo[] receivers = pkg.receivers;
            if (receivers != null) {
                for (ActivityInfo receiver : receivers) {
                    set.add((receiver.processName));
                }
            }
            set.add(pkg.applicationInfo.processName);
        }
        return set.size();
    }

    /***
     * 获取正在运行的进程
     *
     * @param context 上下文
     */
    public static List<ProcessBean> getRunningProcess(Context context) {
        PackageManager pm = context.getPackageManager();

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ProcessBean> list = new ArrayList<ProcessBean>();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        ApplicationInfo applicationInfo = null;
        for (ActivityManager.RunningAppProcessInfo runapp : runningAppProcesses) {
            ProcessBean bean = new ProcessBean();
            try {
                applicationInfo = pm.getApplicationInfo(runapp.processName, 0);
                bean.name = applicationInfo.loadLabel(pm).toString();
                if (applicationInfo.loadIcon(pm) != null) {
                    bean.icon = applicationInfo.loadIcon(pm);
                } else {
                    Log.i(TAG, "系统的进程没有图标");
                }
                bean.processName = runapp.processName;

                //判断是否是系统进程
                int flags = applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    bean.isSystem = true;
                } else {
                    bean.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.i(TAG, runapp.processName + "应用名找不到");
                bean.name = runapp.processName;
                bean.isSystem = true;
            }
            Debug.MemoryInfo memory = am.getProcessMemoryInfo(new int[]{runapp.pid})[0];
            bean.memory = memory.getTotalPss() * 1024;
            list.add(bean);
        }
        return list;
    }

    /***
     * 读取剩余内存
     *
     * @param context 上下文
     * @return 返回剩余内存空间
     */
    public static long getFreeMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * @param context 上下文
     * @return 返回现在运行中的程序所占用的内存
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return outInfo.totalMem;
        } else {
            return loadLowVersionTotalMemory();
        }
    }

    /**
     * 杀死包名为packageName的进程
     *
     * @param context     上下文
     * @param packageName 包名
     */

    public static void killProcess(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);
        Log.i(TAG, "杀死了" + packageName + "进程");
    }


    //获取低版本系统的内存信息
    public static long loadLowVersionTotalMemory() {
        String s = null;
        File file = new File("/proc/meminfo");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = reader.readLine();
            s = line.replace("MemTotal:", "").toString().replace("kB", "").trim();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "/proc/meminfo文件找不到");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "文件操作失败");
            e.printStackTrace();
        }
        return Long.valueOf(s) * 1024;
    }

}
