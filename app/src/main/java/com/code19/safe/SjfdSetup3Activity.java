package com.code19.safe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;

/**
 * Created by Gh0st on 2015/8/31.
 * 2:47
 */
public class SjfdSetup3Activity extends SjfdBaseActivity {
    private static final String TAG = "SjfdSetup3Activity";
    private static final int REQUEST_CODE_CONTACT = 100;
    EditText et_safeNumber;
    private String safeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup3);

        et_safeNumber = (EditText) findViewById(R.id.setup3_et_safeNumber);
        String nu = PreferenceUtils.getString(this, Constants.SAFE_NUMBER);
        if (!TextUtils.isEmpty(nu)) {
            et_safeNumber.setText(nu);//如果已经设置过了，就将这个值回显在输入框中
            et_safeNumber.setSelection(nu.length());//将光标至于值后面}
        }
    }

    @Override
    protected boolean doPre() {
        Intent intent = new Intent(SjfdSetup3Activity.this, SjfdSetup2Activity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected boolean doNext() {
        safeNumber = et_safeNumber.getText().toString();
        if (TextUtils.isEmpty(safeNumber)) {
            Toast.makeText(this, "请输入或选中安全联系人", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.i(TAG, "您输入的安全号码是：" + safeNumber);
        PreferenceUtils.putString(SjfdSetup3Activity.this, Constants.SAFE_NUMBER, safeNumber);
        Intent intent = new Intent(SjfdSetup3Activity.this, SjfdSetup4Activity.class);
        startActivity(intent);
        return true;
    }

    //点击选择安全联系人按钮后，打开新的页面并等待页面返回结果

    public void selectContact(View v) {
        Log.i(TAG, "点击选择联系人按钮");
        Intent intent = new Intent(SjfdSetup3Activity.this, ContactsActivity.class);
        //startActivityForResult(intent, Activity.RESULT_OK);
        startActivityForResult(intent, REQUEST_CODE_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这里来处理返回来的数据,并显示给EditText
        Log.i(TAG, "拿到了返回来的数据了 " + data.getStringExtra("number"));
        if (requestCode == REQUEST_CODE_CONTACT) {
            //如果返回的代码是REQUEST_CODE_CONTACT，说明是返回来了数据
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String number = data.getStringExtra("number");
                    et_safeNumber.setText(number);//如果已经设置过了，就将这个值回显在输入框中
                    et_safeNumber.setSelection(number.length());//将光标至于值后面
                    Log.i(TAG, "返回来的数据onActivityResult " + number);
                    PreferenceUtils.putString(this, Constants.SAFE_NUMBER, number);
                    break;
            }
        }
    }
}
