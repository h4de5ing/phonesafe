package com.code19.safe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
        mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName who = new ComponentName(this, SjfdSetup4Activity.class);
        if (mDpm.isAdminActive(who)) { //初始界面，如果是激活状态
            Log.i(TAG, "初始化界面的状态是激活");
            admin.setImageResource(R.mipmap.admin_activated);//初始化界面就判断是否是激活状态
        } else {
            Log.i(TAG, "初始化界面的状态是未激活----");
            admin.setImageResource(R.mipmap.admin_inactivated);//设置为没有激活状态的图标
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

    //点击激活按钮
    public void activity(View v) {
        //点击激活按钮需要做以下事情，一更新UI，二将激活状态保持
        Log.i(TAG, "点击了激活按钮 ");
        //如果没有激活就去激活
        ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
        if (mDpm.isAdminActive(who)) {
            mDpm.removeActiveAdmin(who);//移除激活状态
            mIvAdmin.setImageResource(R.mipmap.admin_inactivated);
        } else {//没有激活，就去激活
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
                case Activity.RESULT_OK:
                    //用户激活后，只需要更新UI即可
                    mIvAdmin.setImageResource(R.mipmap.admin_activated);
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }
}
