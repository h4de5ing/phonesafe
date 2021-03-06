package com.code19.safe.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Gh0st on 2015/9/14.
 * 15:45
 * md5加密的工具类
 */
public class Md5Utils {
    private static final String TAG = "Md5Utils---";

    public static String encode(String pwd) {
        try {
            MessageDigest instance = MessageDigest.getInstance("md5");
            byte[] digest = instance.digest(pwd.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int r = b & 0xff;
                String hex = Integer.toHexString(r);
                if (hex.length() == 1) {
                    hex = 0 + hex;
                }
                sb.append(r);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encode(InputStream in) {
        MessageDigest digester = null;
        try {
            digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            byte[] digest = digester.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int r = b & 0xff;
                String hex = Integer.toHexString(r);
                if (hex.length() == 1) {
                    hex = 0 + hex;
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
