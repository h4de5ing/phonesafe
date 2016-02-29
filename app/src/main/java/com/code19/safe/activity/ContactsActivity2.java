package com.code19.safe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.R;
import com.code19.safe.bean.ContactBean;
import com.code19.safe.utils.ContactUtils;

/**
 * Created by Administrator on 2015/9/1.
 * 读取联系人，优化版 TODO
 */
public class ContactsActivity2 extends Activity {

    private static final String KEY_NUMBER = "number";
    private static final String TAG = "ContactsActivity2";
    private ListView mListView;
    private Cursor mCursor;
    private ProgressBar mProgressBar;
    private mCursorAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initView();
        initEvent();
        initData();
    }

    private void initView() {

        mListView = (ListView) findViewById(R.id.lv_contacts);
        // mProgressBar = (ProgressBar) findViewById(R.id.cs_progressbar);
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ContactsActivity2.this, "点击了条目" + position, Toast.LENGTH_SHORT).show();

                mCursor.moveToPosition(position);//改变游标指向
                ContactBean bean = ContactUtils.getContactBean(mCursor);
                Log.i(TAG, "bean中的值 " + bean.number);
                Intent intent = new Intent();
                intent.putExtra(KEY_NUMBER, bean.number);
                setResult(Activity.RESULT_OK, intent);

                finish();
            }
        });
    }

    //给listView设置数据
    private void initData() {
        adapter = new mCursorAdapter(this, null);
        mListView.setAdapter(adapter);
        // mProgressBar.setVisibility(View.VISIBLE);//显示进度条

        //子线程更新数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                mCursor = ContactUtils.getAllCursor(ContactsActivity2.this);
                Log.i(TAG, "mCursor中的内容：" + mCursor);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // mProgressBar.setVisibility(View.GONE);
                        //通知数据改变了
                        adapter.changeCursor(mCursor);
                    }
                });
            }
        }).start();
    }

    private class mCursorAdapter extends CursorAdapter {

        public mCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return View.inflate(ContactsActivity2.this, R.layout.item_contact_selected, null);
        }

        /**
         * @param view    显示的view
         * @param context 上下文
         * @param cursor  游标已经自动的move到对于的positon
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ImageView ivIcon = (ImageView) view.findViewById(R.id.item_cs_iv_icon);
            TextView tvName = (TextView) view.findViewById(R.id.item_cs_tv_name);
            TextView tvNumber = (TextView) view.findViewById(R.id.item_cs_tv_number);
            ContactBean bean = ContactUtils.getContactBean(mCursor);

            tvName.setText(bean.name);
            tvNumber.setText(bean.number);
            Bitmap icon = ContactUtils.getContactIcon(ContactsActivity2.this, bean.contactId);
            if (icon != null) {
                ivIcon.setImageBitmap(icon);
            }
        }
    }


}