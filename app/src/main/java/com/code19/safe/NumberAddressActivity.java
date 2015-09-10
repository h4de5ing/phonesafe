package com.code19.safe;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.db.AddressDao;

/**
 * Created by Gh0st on 2015/9/6.
 * 12:12
 */
public class NumberAddressActivity extends Activity {

    private static final String TAG = "NumberAddressActivity";
    private EditText mNumber;
    private Button mQuery;
    private TextView mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools_numberaddress);
        initView();
        iniEvent();
    }

    private void initView() {
        mNumber = (EditText) findViewById(R.id.tools_na_et_number);
        mQuery = (Button) findViewById(R.id.tools_na_btn_query);
        mAddress = (TextView) findViewById(R.id.tools_na_tv_address);
    }

    private void iniEvent() {
        mQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mNumber.getText().toString();
                Log.i(TAG, "点击了查询号码归属地" + number);
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(NumberAddressActivity.this, "请输入查询的号码", Toast.LENGTH_SHORT).show();
                    //当编辑框为空时，做抖动效果
                    Animation shake = AnimationUtils.loadAnimation(NumberAddressActivity.this, R.anim.shake);
                    findViewById(R.id.tools_na_et_number).startAnimation(shake);

                    return;
                }
                //查询号码，在dao类中已经做了号码判断，返回判断的结果
                String address = AddressDao.findAddress(NumberAddressActivity.this, number);
                mAddress.setText("归属地：" + address);
            }
        });

    }
}
