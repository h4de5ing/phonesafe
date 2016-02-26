package com.code19.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code19.safe.R;
import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;

/**
 * Created by Gh0st on 2015/8/31.
 * 0:34
 */
public class SjfdActivity extends Activity {

    private static final String TAG = "SjfdActivity";
    private RelativeLayout mRlProtecting;
    private ImageView mIvProtecting;
    private RelativeLayout mResetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd);
        initView();
        initEvent();
    }

    private void initView() {
        TextView mTvSafeNumber = (TextView) findViewById(R.id.sjfd_tv_safeNumber);
        mRlProtecting = (RelativeLayout) findViewById(R.id.sjfd_rl_protecting);
        mIvProtecting = (ImageView) findViewById(R.id.sjfd_iv_protecting);
        mResetup = (RelativeLayout) findViewById(R.id.sjfd_rl_resetup);
        String safeNumber = PreferenceUtils.getString(this, Constants.SAFE_NUMBER);
        mTvSafeNumber.setText(safeNumber);
        boolean mProtecting = PreferenceUtils.getBoolean(SjfdActivity.this, Constants.PROTECTING);
        mIvProtecting.setImageResource(mProtecting ? R.mipmap.lock : R.mipmap.unlock);
    }

    private void initEvent() {
        mRlProtecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean p=PreferenceUtils.getBoolean(SjfdActivity.this, Constants.PROTECTING);
                if (p) {
                    Log.i(TAG, "关闭了防盗保护");
                    PreferenceUtils.putBoolean(SjfdActivity.this, Constants.PROTECTING, false);
                    mIvProtecting.setImageResource(R.mipmap.unlock);
                } else {
                    Log.i(TAG, "开启了防盗保护");
                    PreferenceUtils.putBoolean(SjfdActivity.this, Constants.PROTECTING, true);
                    mIvProtecting.setImageResource(R.mipmap.lock);
                }

            }
        });
        mResetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SjfdActivity.this, SjfdSetup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
