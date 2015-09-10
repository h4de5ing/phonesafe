package com.code19.safe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Gh0st on 2015/9/4.
 * 23:59
 */
public class ToolsActivity extends Activity {

    private static final String TAG = "ToolsActivity";
    private TextView mNumber;
    private TextView mNormalnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        initView();
        initEvent();
    }

    private void initView() {
        mNumber = (TextView) findViewById(R.id.tools_tv_numberaddress);
        mNormalnumber = (TextView) findViewById(R.id.tools_tv_normalnumber);
    }

    private void initEvent() {
        //号码归属地查询
        mNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToolsActivity.this, NumberAddressActivity.class);
                startActivity(intent);
            }
        });
        //常用号码查询
        mNormalnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了常用号码查询");
                //new LinearLayout(),将某个控件移到最前面
                // removeView 方法 移除后
                // addView 方法 添加到0位置
                //加动画，在remove前和add后就能实现动画效果
                Intent intent = new Intent(ToolsActivity.this, NormalNumberActivity.class);
                startActivity(intent);

            }
        });
    }
}
