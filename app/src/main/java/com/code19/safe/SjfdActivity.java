package com.code19.safe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;

/**
 * Created by Gh0st on 2015/8/31.
 * 0:34
 */
public class SjfdActivity extends Activity {

    private static final String TAG = "SjfdActivity";
    private TextView mTvSafeNumber;
    private RelativeLayout mRlProtecting;
    private ImageView mIvProtecting;
    private boolean mProtecting;
    private RelativeLayout mResetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd);
        initView();
        initEvent();
    }

    //初始化界面
    private void initView() {
        mTvSafeNumber = (TextView) findViewById(R.id.sjfd_tv_safeNumber);//显示安全号码的控件
        mRlProtecting = (RelativeLayout) findViewById(R.id.sjfd_rl_protecting);//切换保护状态的控件
        mIvProtecting = (ImageView) findViewById(R.id.sjfd_iv_protecting);//显示保护状态的图片
        mResetup = (RelativeLayout) findViewById(R.id.sjfd_rl_resetup);
        //拿到安全号码
        String safeNumber = PreferenceUtils.getString(this, Constants.SAFE_NUMBER);
        //显示安全号码
        mTvSafeNumber.setText(safeNumber);
        //拿到开启保护的状态
        mProtecting = PreferenceUtils.getBoolean(SjfdActivity.this, Constants.PROTECTING);
        //根据保护状态设置显示图像
        mIvProtecting.setImageResource(mProtecting ? R.mipmap.lock : R.mipmap.unlock);
    }

    //初始化事件
    private void initEvent() {
        //开启保护rl的点击事件，切换保护状态
        mRlProtecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //每次点击都去读取配置里面的值，判断是否是开启保护，如果是，就关闭，不是就开启
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
        //重新设置rl的点击事件，进入setup1
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
