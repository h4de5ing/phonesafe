package com.code19.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code19.safe.R;
import com.code19.safe.service.CallSmsSafeService;
import com.code19.safe.service.NumBerAddressService;
import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;
import com.code19.safe.utils.ServiceStateUtils;
import com.code19.safe.view.AddressNumberStyleDialog;
import com.code19.safe.view.SettingItemView;

/**
 * Created by Gh0st on 2015/8/29.
 * 19:03
 */
public class SettingActivity extends Activity {

    private static final String TAG = "SettingActivity";
    private SettingItemView mSivAutoUpdate;
    private SettingItemView mSivBlacklist;
    private SettingItemView mSivNumberAddress;
    private SettingItemView mSivNumberAddressStyel;

    @Override
    protected void onStart() {
        super.onStart();
        //获得当前的数据
        mSivAutoUpdate.setToggleState(PreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE, true));
        //显示骚扰拦截服务状态
        mSivBlacklist.setToggleState(ServiceStateUtils.isRunning(getApplicationContext(), CallSmsSafeService.class));
        //显示号码归属地服务状态
        mSivNumberAddress.setToggleState(ServiceStateUtils.isRunning(getApplicationContext(), NumBerAddressService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //初始化view
        initView();
        //初始化事件
        initEvent();
    }

    private void initView() {
        mSivAutoUpdate = (SettingItemView) findViewById(R.id.setting_siv_autoupdate);
        mSivBlacklist = (SettingItemView) findViewById(R.id.setting_item_blacklist);
        mSivNumberAddress = (SettingItemView) findViewById(R.id.setting_item_numberAddress);
        mSivNumberAddressStyel = (SettingItemView) findViewById(R.id.setting_item_numberAddressStyle);
    }

    private void initEvent() {
        //自动更新事件
        mSivAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了自动更新");
                //UI图标开的时候就关，关的时候就开
                mSivAutoUpdate.toggle();
                //业务逻辑，下次进入时不检测网络更新--存储不自动更新的编辑,存储开关的状态
                PreferenceUtils.putBoolean(SettingActivity.this, Constants.AUTO_UPDATE, mSivAutoUpdate.getToggleState());
            }
        });
        //设置骚扰拦截点击事件
        mSivBlacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了骚扰拦截");
                //如果服务状态是开启就关闭，关闭状态就开启
                Intent service = new Intent(SettingActivity.this, CallSmsSafeService.class);
                mSivBlacklist.toggle();
                if (ServiceStateUtils.isRunning(getApplicationContext(), CallSmsSafeService.class)) {
                    stopService(service);
                } else {
                    startService(service);
                }
            }
        });
        //号码归属地服务点击事件
        mSivNumberAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了归属地");
                Intent service = new Intent(SettingActivity.this, NumBerAddressService.class);
                mSivNumberAddress.toggle();
                //如果服务状态是开启就关闭，关闭状态就开启
                if (ServiceStateUtils.isRunning(getApplicationContext(), NumBerAddressService.class)) {
                    stopService(service);
                } else {
                    startService(service);
                }
            }
        });
        //号码归属地显示样式
        mSivNumberAddressStyel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了归属地样式");
                //弹出一个从底部出来的dialog
                final AddressNumberStyleDialog dialog = new AddressNumberStyleDialog(SettingActivity.this);
                dialog.setAdapter(new NumberAddressStyleAdapter());
                dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        PreferenceUtils.putInt(getApplicationContext(), Constants.NUMBER_ADDRESS_STYLE, icon[position]);
                    }
                });
                dialog.show();
            }
        });
    }

    private String[] title = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    private int[] icon = new int[]{R.drawable.toast_address_normal,
            R.drawable.toast_address_orange, R.drawable.toast_address_blue,
            R.drawable.toast_address_gray, R.drawable.toast_address_green};

    private class NumberAddressStyleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(SettingActivity.this, R.layout.item_dialog_nuber_address, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.icIcon = (ImageView) convertView.findViewById(R.id.item_dialog_na_iv_icon);
                holder.ivSelected = (ImageView) convertView.findViewById(R.id.item_dialog_na_iv_selected);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.item_dialog_na_tv_title);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给holder中view设置数据
            holder.icIcon.setImageResource(icon[position]);
            holder.tvTitle.setText(title[position]);
            int style = PreferenceUtils.getInt(getApplicationContext(), Constants.NUMBER_ADDRESS_STYLE, icon[0]);
            holder.ivSelected.setVisibility(style == icon[position] ? View.VISIBLE : View.GONE);
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView icIcon;
        ImageView ivSelected;
        TextView tvTitle;
    }
}
