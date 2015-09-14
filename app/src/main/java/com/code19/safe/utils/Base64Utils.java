package com.code19.safe.utils;

import android.util.Base64;

/**
 * Created by Gh0st on 2015/9/14.
 * 16:25
 */
public class Base64Utils {

    public static String encode(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    public static String decode(String str) {
        byte[] decode = Base64.decode(str, Base64.DEFAULT);
        return decode.toString();
    }
}
