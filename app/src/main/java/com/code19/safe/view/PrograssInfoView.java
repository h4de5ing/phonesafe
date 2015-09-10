package com.code19.safe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/9/7.
 * 17:54
 * prograssInfoView ，自定义Item类
 */
public class PrograssInfoView extends LinearLayout {

    private ProgressBar mProgressBar;
    private TextView mTvTitle;
    private TextView mTvLeft;
    private TextView mTvRight;

    public PrograssInfoView(Context context) {
        this(context, null);
    }

    public PrograssInfoView(Context context, AttributeSet att) {
        super(context, att);
        //加载xml挂载到类上
        View.inflate(context, R.layout.view_progress_info,this);
        mProgressBar = (ProgressBar) findViewById(R.id.view_pi_pb_progress);
        mTvTitle = (TextView) findViewById(R.id.view_pi_tv_title);
        mTvLeft = (TextView) findViewById(R.id.view_pi_tv_left);
        mTvRight = (TextView) findViewById(R.id.view_pi_tv_right);
    }

    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void setLeft(String left) {
        mTvLeft.setText(left);
    }

    public void setRight(String right) {
        mTvRight.setText(right);
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public void setMax(int max) {
        mProgressBar.setMax(max);
    }
}
