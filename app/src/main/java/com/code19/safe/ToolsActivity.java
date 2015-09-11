package com.code19.safe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.engine.SmsProvider;

/**
 * Created by Gh0st on 2015/9/4.
 * 23:59
 */
public class ToolsActivity extends Activity {

    private static final String TAG = "ToolsActivity";
    private TextView mNumber;
    private TextView mNormalnumber;
    private TextView mBakupsms;
    private TextView mRestore;

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
        mBakupsms = (TextView) findViewById(R.id.tools_tv_bakupsms);
        mRestore = (TextView) findViewById(R.id.tools_tv_restore);

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
        //短信备份
        mBakupsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "备份短信吧");
                final ProgressDialog dialog = new ProgressDialog(ToolsActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCanceledOnTouchOutside(false);//点击外侧取消dialog
                dialog.show();

                SmsProvider.SmsBackup(ToolsActivity.this, new SmsProvider.OnBackupListener() {
                    @Override
                    public void onProgress(int progress, int max) {
                        //可以在主线程中实时更新的当前进度和进度的最大值
                        dialog.setProgress(progress);
                        dialog.setMax(max);
                    }

                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            Toast.makeText(ToolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Log.i(TAG, "备份成功");
                        } else {
                            Toast.makeText(ToolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Log.i(TAG, "备份失败");
                        }

                    }
                });
            }
        });
        //还原短信
        mRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(ToolsActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCanceledOnTouchOutside(false);//点击外侧取消dialog
                dialog.show();
                Log.i(TAG, "还原短信");
                SmsProvider.SmsRestore(ToolsActivity.this, new SmsProvider.OnRestoreListener() {
                    // 在主线程中实时更新进度
                    @Override
                    public void onProgress(int progress, int max) {
                        dialog.setProgress(progress);
                        dialog.setMax(max);
                    }

                    @Override
                    public void onResult(Boolean result) {
                        if (result) {
                            Toast.makeText(ToolsActivity.this, "恢复成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(ToolsActivity.this, "恢复失败", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

            }
        });

    }
}
