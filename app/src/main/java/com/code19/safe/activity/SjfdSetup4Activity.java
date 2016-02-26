package com.code19.safe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.code19.safe.R;
import com.code19.safe.receiver.SafeAdminReceiver;

/**
 * Created by Gh0st on 2015/8/31.
 * 2:47
 */
public class SjfdSetup4Activity extends SjfdBaseActivity {
    private static final String TAG = "SjfdSetup4Activity";
    private static final int REQUEST_CODE_ENABLE_ADMIN = 100;
    private ImageView mIvAdmin;
    private DevicePolicyManager mDpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup4);
        ImageView admin = (ImageView) findViewById(R.id.admin_activated);
        mDpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName who = new ComponentName(this, SjfdSetup4Activity.class);
        if (mDpm.isAdminActive(who)) {
            Log.i(TAG, "初始化界面的状态是激活");
            admin.setImageResource(R.mipmap.admin_activated);
        } else {
            Log.i(TAG, "初始化界面的状态是未激活----");
            admin.setImageResource(R.mipmap.admin_inactivated);
         }
        initEvent();
    }

    private void initEvent() {
        mIvAdmin = (ImageView) findViewById(R.id.admin_activated);
        ComponentName who = new ComponentName(this, SjfdSetup4Activity.class);
        mIvAdmin.setImageResource(mDpm.isAdminActive(who) ? R.mipmap.admin_activated : R.mipmap.admin_inactivated);
    }

    @Override
    protected boolean doPre() {
        Intent intent = new Intent(SjfdSetup4Activity.this, SjfdSetup3Activity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected boolean doNext() {
        ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
        if (!mDpm.isAdminActive(who)) {
            Toast.makeText(this, "要开启手机防盗，必须激活管理员", Toast.LENGTH_SHORT).show();
            return false;
        }

        Intent intent = new Intent(SjfdSetup4Activity.this, SjfdSetup5Activity.class);
        startActivity(intent);
        return true;
    }

    public void activity(View v) {
        ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
        if (mDpm.isAdminActive(who)) {
            mDpm.removeActiveAdmin(who);
            mIvAdmin.setImageResource(R.mipmap.admin_inactivated);
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键激活");
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
        }
    }

    //激活后的做的事事情
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            switch (resultCode) {
                case RESULT_OK:
                    mIvAdmin.setImageResource(R.mipmap.admin_activated);
                case RESULT_CANCELED:
                    break;
            }
        }
    }
}
