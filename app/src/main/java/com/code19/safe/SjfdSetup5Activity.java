package com.code19.safe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;

/**
 * Created by Gh0st on 2015/8/31.
 * 2:47
 */
public class SjfdSetup5Activity extends SjfdBaseActivity {
    private static final String TAG = "SjfdSetup5Activity";
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup5);
        checkBox = (CheckBox) findViewById(R.id.setup5_cb_protecting);

        boolean pro = PreferenceUtils.getBoolean(this, Constants.PROTECTING);
        Log.i(TAG, "initView " + pro);
        if (pro) { //说明开启了，就需要复选中
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果他是选中状态，就保存开启了防盗保护
                    Log.i(TAG, "开启手机防盗");
                    PreferenceUtils.putBoolean(SjfdSetup5Activity.this, Constants.PROTECTING, true);
                } else {
                    Log.i(TAG, "关闭手机防盗");
                    PreferenceUtils.putBoolean(SjfdSetup5Activity.this, Constants.PROTECTING, false);
                }
            }
        });
    }

    @Override
    protected boolean doPre() {
        Intent intent = new Intent(SjfdSetup5Activity.this, SjfdSetup4Activity.class);
        startActivity(intent);
        return true;
    }


    @Override
    protected boolean doNext() {

        boolean protecting = PreferenceUtils.getBoolean(SjfdSetup5Activity.this, Constants.PROTECTING);
        Log.i(TAG, "保护状态:" + protecting);
        if (!protecting) {
            Toast.makeText(SjfdSetup5Activity.this, "请开启防盗保护", Toast.LENGTH_SHORT).show();
            return false;
        }
        Intent intent = new Intent(SjfdSetup5Activity.this, SjfdActivity.class);
        startActivity(intent);
        return true;
    }


}
