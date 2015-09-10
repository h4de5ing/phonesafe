package com.code19.safe;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.bean.AppBean;
import com.code19.safe.engine.AppProvider;
import com.code19.safe.view.PrograssInfoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Gh0st on 2015/9/7.
 * 17:43
 */
public class AppManagerActivity extends Activity {

    private static final String TAG = "AppManager";
    private PrograssInfoView mPivRom;
    private PrograssInfoView mPivSd;
    private ListView mListview;
    private List<AppBean> mDatas;
    private AppInfoAdapter mAdapter;
    private ProgressBar mProgressBar;
    private ArrayList<AppBean> mSystemDatas;
    private ArrayList<AppBean> mUserDatas;
    private TextView mTvTitle;
    private PackageRemoveReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        //初始化view
        initView();
        //加载数据
        initData();

        //加载事件
        initEvent();

        //注册包删除的广播接收者
        mReceiver = new PackageRemoveReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册广播
        unregisterReceiver(mReceiver);
    }

    private void initView() {
        mPivRom = (PrograssInfoView) findViewById(R.id.am_piv_rom);
        mPivSd = (PrograssInfoView) findViewById(R.id.am_piv_sd);
        mListview = (ListView) findViewById(R.id.am_listview);
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mTvTitle = (TextView) findViewById(R.id.am_apptype);
        mTvTitle.setHeight(25);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void initEvent() {
        //listView 滑动事件
        mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当listview滚动时做的操作
                if (mUserDatas == null || mSystemDatas == null) {
                    return;
                }
                if (firstVisibleItem >= 0 && firstVisibleItem <= mUserDatas.size()) {
                    //如果在第一条和最后一条之间，就显示用户程序的标题
                    mTvTitle.setText("用户程序(" + mUserDatas.size() + ")个");
                } else {
                    mTvTitle.setText("系统程序(" + mSystemDatas.size() + ")个");
                }
            }
        });
        //listView中item点击事件
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppBean bean = (AppBean) mAdapter.getItem(position);
                if (bean == null) {
                    //点击了titie
                    Log.i(TAG, "点击了title");
                    return;
                }
                //点击条目，弹出一个层
                showPopup(bean, view);
            }
        });

    }

    //点击item后的效果
    private void showPopup(final AppBean bean, View anchor) {
        Log.i(TAG, "弹出了" + bean.name + "的样式");
        //设置点击后弹出层的样式
        View contentView = View.inflate(this, R.layout.popup_app_option, null);

        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;

        //创建弹出的popupWindow对象，并设置属性
        final PopupWindow window = new PopupWindow(contentView, width, height);
        window.setFocusable(true); //设置获得焦点
        window.setOutsideTouchable(true);//外侧可以点击，由于他是铺满整个屏幕，所有点击外面并不会消失
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));//设置背景
        //设置动画
        //window.setAnimationStyle(R.style.PopupAnimation);
        //设置点击后再什么位置显示
        window.showAsDropDown(anchor, 60, -anchor.getHeight());
        //打开事件
        contentView.findViewById(R.id.popup_ll_open);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了打开");
                Intent intent = getPackageManager().getLaunchIntentForPackage(bean.packageName);
                //如果某个应用没有启动，就没有launchIntent,则intent为空
                if (intent == null) {
                    Log.i(TAG, "你点击了没有界面的应用" + bean.packageName);
                    Toast.makeText(AppManagerActivity.this, "无法启动无界面应用", Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(intent);
                window.dismiss();
            }
        });
        //卸载事件
        contentView.findViewById(R.id.popup_ll_uninstall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***
                 * 来自系统源码 PackageInstaller中的清单文件
                 *<intent-filter>
                 <action android:name="android.intent.action.VIEW" />
                 <action android:name="android.intent.action.DELETE" />
                 <category android:name="android.intent.category.DEFAULT" />
                 <data android:scheme="package" />
                 </intent-filter>
                 */
                Log.i(TAG, "点击了卸载");
                if (bean.isSystem) {
                    Toast.makeText(AppManagerActivity.this, "无法卸载系统应用", Toast.LENGTH_SHORT).show();
                    window.dismiss();
                    return;
                }
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                Log.i(TAG, "即将卸载：" + bean.packageName);
                intent.setData(Uri.parse("package:" + bean.packageName));
                startActivity(intent);
                window.dismiss();
            }
        });
        //分享事件
        contentView.findViewById(R.id.popup_ll_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了分享"); // TODO: 这里使用社交分享
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:"));
                intent.putExtra("sms_body", "这个应用很好玩，推荐你的朋们来玩吧");
                startActivity(intent);
                window.dismiss();
            }
        });
        //信息事件
        contentView.findViewById(R.id.popup_ll_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了信息");
                /** 来自系统源码settings中的清单文件
                 *<activity android:name=".applications.InstalledAppDetails"
                 android:theme="@android:style/Theme.NoTitleBar"
                 android:label="@string/application_info_label">
                 <intent-filter>
                 <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
                 <category android:name="android.intent.category.DEFAULT" />
                 <data android:scheme="package" />
                 </intent-filter>
                 </activity>
                 */
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + bean.packageName));
                startActivity(intent);
                window.dismiss();
            }
        });
    }

    private void initData() {

        loadRomSize(); //加载手机内存使用情况
        loadSDSize(); //加载sd卡内存使用情况

        //初始化listview数据
        mAdapter = new AppInfoAdapter();
        mListview.setAdapter(mAdapter);
        //子线程加载数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                //得到所有数据后，区分出系统应用和非系统应用，分别存储
                mDatas = AppProvider.getAllApps(AppManagerActivity.this);
                mUserDatas = new ArrayList<AppBean>(); //存储用户应用
                mSystemDatas = new ArrayList<AppBean>(); //存取系统应用

                for (AppBean bean : mDatas) {
                    if (bean.isSystem) {
                        Log.i(TAG, bean.name + ">是系统应用");
                        mSystemDatas.add(bean);
                    } else {
                        Log.i(TAG, bean.name + "<>是用户应用");
                        mUserDatas.add(bean);
                    }
                }
                Log.i(TAG, "系统应用:" + mSystemDatas.size() + "，用户应用：" + mUserDatas.size());

                //主线程更新界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //mTvTitle.setText("用户程序" + mUserDatas.size() + "个");
                        mAdapter.notifyDataSetChanged();
                        mTvTitle.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }

        }).start();
    }

    private void loadRomSize() {
        File dataDirectory = Environment.getDataDirectory();
        long totalSpace = dataDirectory.getTotalSpace();
        long freeSpace = dataDirectory.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;
        int romProgress = (int) (usedSpace * 100f / totalSpace + 0.5f);
        mPivRom.setTitle("内存：");
        mPivRom.setLeft("已用空间:" + Formatter.formatFileSize(AppManagerActivity.this, usedSpace));
        mPivRom.setRight("总容量" + Formatter.formatFileSize(AppManagerActivity.this, totalSpace));
        mPivRom.setProgress(romProgress);
    }

    private void loadSDSize() {

        File storageDirectory = Environment.getExternalStorageDirectory();
        long totalSpace = storageDirectory.getTotalSpace(); //总共空间
        long freeSpace = storageDirectory.getFreeSpace();  //剩余空间
        long usedSpace = totalSpace - freeSpace; //已用空间
        int sdProgress = (int) (usedSpace * 100f / totalSpace + 0.5f);
        mPivSd.setTitle("SD卡:");
        mPivSd.setLeft("已用空间" + Formatter.formatFileSize(AppManagerActivity.this, usedSpace));
        mPivSd.setRight("总容量" + Formatter.formatFileSize(AppManagerActivity.this, totalSpace));
        mPivSd.setProgress(sdProgress);
        Log.i(TAG, "剩余空间：" + Formatter.formatFileSize(AppManagerActivity.this, freeSpace));
        Log.i(TAG, "总容量：" + Formatter.formatFileSize(AppManagerActivity.this, totalSpace));
    }

    private class AppInfoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int count = 0;
            if (mUserDatas != null && mSystemDatas.size() > 0) {
                count += mUserDatas.size();
                count++;
            }
            if (mSystemDatas != null && mSystemDatas.size() > 0) {
                count += mSystemDatas.size();
                count++;
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            }
            if (position > 0 && position <= mUserDatas.size()) {
                return mUserDatas.get(position - 1);
            } else {
                if (position == mUserDatas.size()) {
                    return null;
                }
                return mSystemDatas.get(position - 2 - mUserDatas.size());
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            if (position == 0) {
                //返回用户程序的条目
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.parseColor("#ffd0d1d0"));
                tv.setPadding(4, 4, 4, 4);
                tv.setText("用户程序(" + mUserDatas.size() + ")个");
                return tv;
            }
            if (position == mUserDatas.size() + 1) {
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.parseColor("#ffd0d1d0"));
                tv.setPadding(4, 4, 4, 4);
                tv.setText("系统程序(" + mSystemDatas.size() + ")个");
                return tv;
            }
            //处理listview复用
            ViewHolder holder = null;
            if (convertView == null || convertView instanceof TextView) {
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                //给holder设置view
                holder.ImIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_app_title);
                holder.tvInstallSD = (TextView) convertView.findViewById(R.id.item_app_desc);
                holder.tvSize = (TextView) convertView.findViewById(R.id.item_app_size);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //holder给view设置数据
            //AppBean bean = mDatas.get(position);//拿到mDatas中的bean对象
            AppBean bean = (AppBean) getItem(position);
            Log.i(TAG, "converView:" + convertView.getClass().getName());
            if (bean.name == null) {
                holder.tvName.setText(bean.packageName);
            } else {
                holder.tvName.setText(bean.name);
            }
            holder.tvInstallSD.setText(bean.inInstallSD ? "内部存储" : "内部设备");
            holder.tvSize.setText(Formatter.formatFileSize(AppManagerActivity.this, bean.size));
            holder.ImIcon.setImageDrawable(bean.icon);
            Log.i(TAG, "应用名：" + bean.name + ",是否是系统应用：" + bean.isSystem + ",大小:" + bean.size);
            return convertView;
        }
    }

    static class ViewHolder {
        /*
        * public Drawable icon; //应用图标
        * public String name; //应用名称
        * public String packageName;//包名
        * public long size; //应用大小
        * public boolean inInstallSD;//是否安装在SD卡上
        * public boolean isSystem;//是否是系统程序
        * */
        ImageView ImIcon;
        TextView tvName;
        TextView tvSize;
        TextView tvInstallSD;
    }

    //设置应用监听器，如果有应用卸载了，更新UI
    private class PackageRemoveReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String dataString = intent.getDataString();
            String pkg = dataString.replace("package:", "").trim();
            Log.i(TAG, dataString + "移除了，：" + pkg); // package:com.itensen.test移除了
            ListIterator<AppBean> iterator = mUserDatas.listIterator();
            while (iterator.hasNext()) {
                AppBean next = iterator.next();
                Log.i(TAG, "next" + next);
                if (pkg.equals(next.packageName)) {
                    iterator.remove();
                    break;
                }
            }
            mAdapter.notifyDataSetChanged();
            loadRomSize();
            loadRomSize();
        }
    }
}
