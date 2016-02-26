package com.code19.safe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.code19.safe.R;
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
            et_safeNumber.setText(nu);
            et_safeNumber.setSelection(nu.length());
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
        PreferenceUtils.putString(SjfdSetup3Activity.this, Constants.SAFE_NUMBER, safeNumber);
        Intent intent = new Intent(SjfdSetup3Activity.this, SjfdSetup4Activity.class);
        startActivity(intent);
        return true;
    }

    public void selectContact(View v) {
        Intent intent = new Intent(SjfdSetup3Activity.this, ContactsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONTACT) {
            switch (resultCode) {
                case RESULT_OK:
                    String number = data.getStringExtra("number");
                    et_safeNumber.setText(number);
                    et_safeNumber.setSelection(number.length());
                    PreferenceUtils.putString(this, Constants.SAFE_NUMBER, number);
                    break;
            }
        }
    }
}
