package com.code19.safe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Gh0st on 2015/9/7.
 * 18:58
 */
public class AppBean {
    public Drawable icon; //应用图标
    public String name; //应用名称
    public String packageName;//包名
    public long size; //应用大小
    public boolean inInstallSD;//是否安装在SD卡上
    public boolean isSystem;//是否是系统程序
}
