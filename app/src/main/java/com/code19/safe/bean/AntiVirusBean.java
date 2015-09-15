package com.code19.safe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Gh0st on 2015/9/14.
 * 18:48
 */
public class AntiVirusBean {
    public Drawable icon;
    public String name;
    public boolean safe;//是否是病毒
    public String packageName;
    public boolean Clean;//如果是病毒就需要清除操作
}
