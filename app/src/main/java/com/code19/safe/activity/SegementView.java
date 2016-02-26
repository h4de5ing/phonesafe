package com.code19.safe.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/9/13.
 * 21:27
 */
public class SegementView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "SegementView";
    private TextView mTvLock;
    private TextView mTvUnlock;
    private OnSegementSelectedListener mListener;
    private boolean isLocked;

    public SegementView(Context context) {
        super(context, null);
    }

    public SegementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_segement, this);

        mTvLock = (TextView) findViewById(R.id.segement_tv_lock);
        mTvUnlock = (TextView) findViewById(R.id.segement_tv_unlock);

        mTvLock.setSelected(true);
        mTvUnlock.setSelected(false);
        isLocked = false;

        initEvent();
    }

    private void initEvent() {
        mTvLock.setOnClickListener(this);
        mTvUnlock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mTvLock) {
            if (!isLocked) {
                //没有上锁的情况下，才可以上锁
                //点击上锁的
                mTvLock.setSelected(true);
                mTvUnlock.setSelected(false);
                Log.i(TAG, "上锁-------------");
                if (mListener != null) {
                    mListener.onSelected(true);
                }
                isLocked = true;
            }

        } else if (v == mTvUnlock) {
            if (isLocked) {
                mTvLock.setSelected(false);
                mTvUnlock.setSelected(true);
            Log.i(TAG, "未上锁-------------");
                if (mListener != null) {
                    mListener.onSelected(false);
                }
                isLocked = false;
            }
        }
    }


    //暴露接口使用的方式
    public void setOnSegementSelectedListener(OnSegementSelectedListener listener) {
        this.mListener = listener;
    }

    /**
     * 当选中时的回调，如果为ture上锁
     */
    public interface OnSegementSelectedListener {
        void onSelected(boolean locked);
    }
}
