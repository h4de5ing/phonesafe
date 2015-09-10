package com.code19.safe.utils;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by Gh0st on 2015/9/6.
 * 18:29
 * GZIPUtils工具类，包含两个方法
 * 1.将压缩文件解压出来。
 * 2.将资产中文件拷贝到应用目录中去
 */
public class GZIPUtils {
    private static final String TAG = "GZIPUtils";

    public static void Unzip(InputStream is, OutputStream os) {
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = gis.read(buff)) != -1) {
                os.write(buff, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            Log.i(TAG, "归属地文件解压错误");
            //e.printStackTrace();
        } finally {
            release(is);
            release(os);
            Log.i(TAG, "解压完成");
        }

    }

    /**
     * @param is 输入流
     * @param os 输出流
     */
    public static void copyAssets2Files(InputStream is, OutputStream os) {
        try {
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            Log.i(TAG, "文件流操作出错");
            //e.printStackTrace();
        } finally {
            release(is);
            release(os);
            Log.i(TAG, "拷贝完成");
        }
    }

    //释放文件输入输出流
    private static void release(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            io = null;
        }

    }
}
