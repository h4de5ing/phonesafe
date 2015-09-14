package com.code19.safe;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Gh0st on 2015/9/13.
 * 21:02
 */
public class AppLockActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initView();

    }

    //初始化视图
    private void initView() {

    }
}
