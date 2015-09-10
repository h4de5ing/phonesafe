package com.code19.safe;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;

/**
 * Created by Gh0st on 2015/8/31.
 * 2:47
 * 步骤二需要实现的功能，
 * 点击绑定和解绑文字，切换图片 并 修改配置
 * 点击上一步 和下一步 ，切换页面 和 动画 TODO 没有sim卡的设备，图标会改变，但是不会绑定成功
 */
public class SjfdSetup2Activity extends SjfdBaseActivity {
    private static final String TAG = "SjfdSetup2Activity";
    ImageView mImageView;
    String mBindSIM;
    private RelativeLayout mRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup2);
        mImageView = (ImageView) findViewById(R.id.setup2_icon);
        mBindSIM = PreferenceUtils.getString(this, Constants.SIM_BIND);
        if (TextUtils.isEmpty(mBindSIM)) {  //如果sim信息为空，就显示解锁图形
            mImageView.setImageResource(R.mipmap.unlock);
        } else {
            mImageView.setImageResource(R.mipmap.lock);
        }
    }

    @Override
    protected boolean doPre() {
        //首先执行上一步，开启界面
        // 返回真,播放动画并结束自己
        //返回假，不执行
        Intent intent = new Intent(SjfdSetup2Activity.this, SjfdSetup1Activity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected boolean doNext() {
        String sim = PreferenceUtils.getString(this, Constants.SIM_BIND);
        if (TextUtils.isEmpty(sim)) {
            Log.i(TAG, "SIM卡的值" + sim);
            Toast.makeText(SjfdSetup2Activity.this, "必须要绑定sim才能是想手机防盗功能", Toast.LENGTH_SHORT).show();
            return false;
        }
        Intent intent = new Intent(SjfdSetup2Activity.this, SjfdSetup3Activity.class);
        startActivity(intent);

        return true;
    }


    /**
     * 点击文字实现绑定和解绑sim
     * 在界面初始化时获取到手机的sim信息，和配置文件里面sim卡信息
     * 如果没有绑定，就绑定，如果绑定了，就取消绑定
     *
     * @param v textView
     */
    public void onBind(View v) {
        //如果从配置文件里面获取到的为空，就需要绑定,就需要从系统中获取
        mBindSIM = PreferenceUtils.getString(this, Constants.SIM_BIND);
        if (TextUtils.isEmpty(mBindSIM)) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String simSerialNumber = tm.getSimSerialNumber();//获得手机卡的唯一标识号
            Log.i(TAG, "绑定的SIM卡信息：" + simSerialNumber);
            PreferenceUtils.putString(this, Constants.SIM_BIND, simSerialNumber);//保存到配置文件里面
            mImageView.setImageResource(R.mipmap.lock);
        } else {
            //如果配置不为空就保存空值到配置文件里面，清除
            PreferenceUtils.putString(this, Constants.SIM_BIND, ""); // "" 和null的区别 TODO：
            mImageView.setImageResource(R.mipmap.unlock);
            Log.i(TAG, "清空SIM信息");
        }

    }
}
