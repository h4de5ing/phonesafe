package com.code19.safe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/9/13.
 * 18:47
 */
public class AddressNumberStyleDialog extends Dialog {

    private ListView mListview;
    private ListAdapter mAdapter;
    private AdapterView.OnItemClickListener mListener;

    public AddressNumberStyleDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //去掉dialog的title，一定要在界面限制之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //获得window的layoutparam
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(params);

        setContentView(R.layout.dialog_number_address_style);
        mListview = (ListView) findViewById(R.id.dialog_listview);
        if (mAdapter != null) {
            mListview.setAdapter(mAdapter);
        }
        if (mListener != null) {
            mListview.setOnItemClickListener(mListener);
        }

    }

    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = adapter;
        if (mListener != null) {
            mListview.setAdapter(adapter);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mListener = listener;
        if (mListener != null) {
            mListview.setOnItemClickListener(listener);
        }
    }

}
