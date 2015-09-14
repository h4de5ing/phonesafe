package com.code19.safe.utils;

/**
 * Created by Gh0st on 2015/9/14.
 * 16:30
 */
public class EncryptUtils {
    //异或加密
    public static String encode(String pwd, int key) {
        key = key % 128;
        byte[] bytes = pwd.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] ^= key;
        }
        return new String(bytes);
    }
}
