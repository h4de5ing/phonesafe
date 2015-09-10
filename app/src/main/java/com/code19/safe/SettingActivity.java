package com.code19.safe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;
import com.code19.safe.view.SettingItemView;

/**
 * Created by Gh0st on 2015/8/29.
 * 19:03
 */
public class SettingActivity extends Activity {

    private static final String TAG = "SettingActivity";
    private SettingItemView mSivAutoUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //初始化view
        initView();
        //初始化事件
        initEvent();
    }

    private void initView() {
        mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);
        //获得当前的数据
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE, true));
    }

    private void initEvent() {
        mSivAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了自动更新");
                //UI图标开的时候就关，关的时候就开
                mSivAutoUpdate.toggle();
                //业务逻辑，下次进入时不检测网络更新--存储不自动更新的编辑,存储开关的状态
                PreferenceUtils.putBoolean(SettingActivity.this, Constants.AUTO_UPDATE, mSivAutoUpdate.getToggleState());
            }
        });

    }
}
