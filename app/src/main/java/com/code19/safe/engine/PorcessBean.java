package com.code19.safe.engine;

import android.graphics.drawable.Drawable;

/**
 * Created by Gh0st on 2015/9/10.
 * 17:53
 */
public class PorcessBean {
    public Drawable icon; //进程图标
    public String name; //进程名称
    public String processName; //进程所在的app的包名
    public long memory; //内存信息
    public boolean isSystem; //是否系统应用
    public boolean isSelected; //是否选中
}
