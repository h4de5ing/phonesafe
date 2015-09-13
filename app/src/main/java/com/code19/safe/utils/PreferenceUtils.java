package com.code19.safe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Gh0st on 2015/8/31.
 * 1:15
 */
public class PreferenceUtils {

    private static SharedPreferences mPreferences;

    /**
     * 构造函数初始化sharedPreferences
     *
     * @param context 传上下文
     * @return 返回mPreferences对象
     */
    private static SharedPreferences getmPreferences(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getSharedPreferences("sjfd", Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    /***
     * 获得boolean类型的数据，没有时默认值
     *
     * @param context 上下文
     * @param key     默认值
     * @return 返回数据类型
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /***
     * 获得boolean类型的数据
     *
     * @param context  上下文
     * @param key      属性
     * @param defValue 默认值
     * @return 返回数据
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = getmPreferences(context);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 设置boolean类型的数据
     *
     * @param context  上下文
     * @param key      属性
     * @param defValue 值
     */
    public static void putBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sharedPreferences = getmPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, defValue);
        edit.commit();
    }

    /**
     * 获得String类型的值，没有值是为空
     *
     * @param context 上下文
     * @param key     属性
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);

    }

    /***
     * 获得string类型的数据
     *
     * @param context  上下文
     * @param key      属性
     * @param defValue 默认值
     * @return 返回数据
     */

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = getmPreferences(context);
        return sp.getString(key, defValue);
    }

    /**
     * 设置String类型的值
     *
     * @param context 上下文
     * @param key     属性
     * @param value   值
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = getmPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获得int类型的值，没有时默认值为-1
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * 获得String类型的数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {

        SharedPreferences sp = getSp(context);
        return sp.getInt(key, defValue);
    }

    /**
     * 设置int类型的值
     *
     * @param context 上下文
     * @param key     存储的键
     * @param value   存储的值
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = getSp(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static SharedPreferences getSp(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getSharedPreferences("sjws", context.MODE_PRIVATE);
        }
        return mPreferences;
    }
}
