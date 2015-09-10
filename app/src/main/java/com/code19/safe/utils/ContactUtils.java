package com.code19.safe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.code19.safe.bean.ContactBean;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/3.
 * 15:35
 * 获得联系人工具类
 */
public class ContactUtils {

    private static final String TAG = "ContactUtils";

    /***
     * 返回所有的联系人，
     * 1.拿到内容解析器
     * 2.直接用内容解析器查询query（需要什么参数，创建相应的对象即可）
     *
     * @param context 上下文
     * @return 返回带有数据的list集合
     */
    public static List<ContactBean> getAllContacts(Context context) {
        List<ContactBean> list = new ArrayList<ContactBean>();

        ContentResolver resolver = context.getContentResolver(); //1.拿到内容解析器

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,//电话号码
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //姓名
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID //联系人id
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null); // 2.直接用内容解析器查询
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(0); //拿到电话号码
                String name = cursor.getString(1);//拿到姓名
                long contactId = cursor.getLong(2);//拿到联系人id
                ContactBean bean = new ContactBean();
                Log.i(TAG, "姓名：" + name + ",电话" + number + ",ID:" + contactId);
                bean.number = number;
                bean.name = name;
                bean.contactId = contactId;
                list.add(bean);
            }
            cursor.close();
        }
        return list;
    }

    /**
     * 传连个参数，返回联系人头像
     *
     * @param context   上下文
     * @param contactId 联系人id
     * @return 返回图像
     */

    public static Bitmap getContactIcon(Context context, long contactId) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId + "");
        InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);

        return BitmapFactory.decodeStream(is);
    }

    //返回联系人Bean
    public static ContactBean getContactBean(Cursor cursor) {
        ContactBean bean = new ContactBean();
        bean.number = cursor.getString(1);
        bean.name = cursor.getString(2);
        bean.contactId = cursor.getLong(3);
        Log.i(TAG, "姓名：" + bean.name + ",电话：" + bean.number + ",id:" + bean.contactId);
        return bean;
    }

    //返回带有联系人信息的游标
    public static Cursor getAllCursor(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,//电话号码
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //姓名
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID //联系人id TODO
        };

        Log.i(TAG, "返回带有所有联系人的游标getAllCursor:" + projection[0] + projection[1] + projection[2]);
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        return resolver.query(uri, projection, selection, selectionArgs, sortOrder); // 2.直接用内容解析器查询
    }
}
