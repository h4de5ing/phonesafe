package com.code19.safe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code19.safe.bean.CacheBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/14.
 * 10:30
 */
public class CleanCacheActivity extends Activity {

    private static final String TAG = "CleanCacheActivity";
    private RelativeLayout mRlScanPart;
    private RelativeLayout mRlScanResult;
    private ImageView mIvScanIcon;
    private ImageView mIvScanLine;
    private ProgressBar mPbScanProgress;
    private TextView mTvScanName;
    private TextView mTvScanSize;
    private TextView mTvResult;
    private Button mBtnResultClean;
    private ListView mListview;
    private Button mBtnCleanAll;

    private List<CacheBean> mDatas;

    private boolean isRunning;
    private int mCacheAppCount;
    private int mCacheTotalSize;
    private PackageManager mPm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        mPm = getPackageManager();
        inidView();
        initData();
        initEvent();
    }

    //初始化界面
    private void inidView() {
        //扫描界面
        mRlScanPart = (RelativeLayout) findViewById(R.id.cc_scan_container);
        mIvScanIcon = (ImageView) findViewById(R.id.cc_scan_iv_con);
        mIvScanLine = (ImageView) findViewById(R.id.cc_scan_iv_line);
        mTvScanName = (TextView) findViewById(R.id.cc_scan_tv_name);
        mTvScanSize = (TextView) findViewById(R.id.cc_scan_tv_size);
        mPbScanProgress = (ProgressBar) findViewById(R.id.cc_scan_pb_progress);
        //扫描结果
        mRlScanResult = (RelativeLayout) findViewById(R.id.cc_result_container);
        mTvResult = (TextView) findViewById(R.id.cc_result_tv_result);
        mBtnResultClean = (Button) findViewById(R.id.cc_result_btn_clean);
        //listview部分
        mListview = (ListView) findViewById(R.id.cc_listview);
        mBtnCleanAll = (Button) findViewById(R.id.cc_btn_clean);

    }

    //初始化数据，就是拿到所有的应用，将有缓存的放到前面来
    private void initData() {
        //给listview 加载数据
        CleanCacheAdapter adapter = new CleanCacheAdapter();
        mListview.setAdapter(adapter);
        startScan();

    }

    //开始扫描
    private void startScan() {
        ScanTask task = new ScanTask();
        task.execute();
    }

    //初始化事件
    private void initEvent() {

    }

    class CleanCacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //复用converView
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_cleancache, null);
                holder = new ViewHolder();
                holder.ivClean = (ImageView) convertView.findViewById(R.id.item_clean_clean);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_clean_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_clean_name);
                holder.tvSize = (TextView) convertView.findViewById(R.id.item_clean_size);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给listView设置数据
            final CacheBean bean = mDatas.get(position);
            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            holder.tvSize.setText("缓存大小：" + Formatter.formatFileSize(getApplicationContext(), bean.size));
            holder.ivClean.setVisibility(bean.size > 0 ? View.VISIBLE : View.GONE);
            holder.ivClean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到系统的清理页面去
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + bean.packageName));
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivClean;
        ImageView ivIcon;
        TextView tvName;
        TextView tvSize;

    }

    //Params, Progress, Result
    private class ScanTask extends AsyncTask<Void, CacheBean, Void> {
        //异步任务需要传两个值出去，进度条的当前值和最大值
        private int progress;
        private int max;

        @Override
        protected void onPreExecute() {
//            isRunning=true;
            //清理按钮不可用
//            mBtnCleanAll.setEnabled(false);
            //清空数据
            mCacheAppCount = 0;
            //给list添加年数据，ui更新
            if (mDatas == null) {
                mDatas = new ArrayList<CacheBean>();
            }
            mDatas.clear();
            //显示扫描部分，隐藏结果部分
            //扫描线做动画
        }

        @Override
        protected Void doInBackground(Void... params) {
            //在子线程中给mDatas加载数据

            //获取所有包
            List<PackageInfo> packages = mPm.getInstalledPackages(0);

            //设置进度条的最大值
            max = packages.size();
            for (PackageInfo pkg : packages) {
                Log.i(TAG, "doInBackground "+pkg.packageName);
//                if (!isRunning) {
//                    break;
//                }
                //progress++;

                try {
                    Method method = mPm.getClass().getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                    method.invoke(mPm, pkg.packageName, mStatsObserver);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

    }

    private final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
        public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
            long cacheSize = stats.cacheSize;
            String packageName = stats.packageName;


            Log.d(TAG, "package : " + packageName);
            Log.d(TAG, "cacheSize : " + cacheSize);
            Log.d(TAG, "---------------------------------");
//
//            try {
//                ApplicationInfo applicationInfo = mPm.getApplicationInfo(
//                        packageName, 0);
//
//                String name = applicationInfo.loadLabel(mPm).toString();
//                Drawable icon = applicationInfo.loadIcon(mPm);
//
//                CacheBean bean = new CacheBean();
//                bean.cacheSize = cacheSize;
//                bean.name = name;
//                bean.icon = icon;
//                bean.packageName = packageName;
//                mTask.updateProgress(bean);
//            } catch (NameNotFoundException e) {
//                e.printStackTrace();
//            }
        }
    };

    private class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName,
                                      final boolean succeeded) {
            Log.d("test------------", "onRemoveCompleted: " + packageName + "  " + succeeded);
        }
    }

}
