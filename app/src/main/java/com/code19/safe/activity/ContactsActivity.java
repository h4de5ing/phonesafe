package com.code19.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.R;
import com.code19.safe.bean.ContactBean;
import com.code19.safe.utils.ContactUtils;

import java.util.List;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ContactsActivity extends Activity {

    private static final String KEY_NUMBER = "number";
    private ListView mListView;
    private List<ContactBean> mDatas;
    private ContactSelectedAdapter mAdapter;

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
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ContactsActivity.this, "点击了条目" + position, Toast.LENGTH_SHORT).show();
                ContactBean bean = mDatas.get(position);
                Intent data = new Intent();
                data.putExtra(KEY_NUMBER, bean.number);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    private void initData() {
        mAdapter = new ContactSelectedAdapter();
        mListView.setAdapter(mAdapter);
        mDatas = ContactUtils.getAllContacts(ContactsActivity.this);
        mAdapter.notifyDataSetChanged();
    }


    private class ContactSelectedAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(ContactsActivity.this, R.layout.item_contact_selected, null);
            }
            ImageView ivIcon = (ImageView) convertView.findViewById(R.id.item_cs_iv_icon);
            TextView tvName = (TextView) convertView.findViewById(R.id.item_cs_tv_name);
            TextView tvNumber = (TextView) convertView.findViewById(R.id.item_cs_tv_number);
            ContactBean bean = mDatas.get(position);
            tvName.setText(bean.name);
            tvNumber.setText(bean.number);
            Bitmap icon = ContactUtils.getContactIcon(ContactsActivity.this, bean.contactId);
            if (icon != null) {
                ivIcon.setImageBitmap(icon);
            }
            return convertView;
        }
    }


}