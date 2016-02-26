package com.code19.safe.activity;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.code19.safe.R;
import com.code19.safe.bean.TrafficBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TrafficActivity extends Activity {

    private PackageManager mPm;
    private ListView mTrafficListView;
    private List<TrafficBean> mDatas;
    private TextView mTitle;
    private TrafficAdapter mAdapter;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        initView();
        initData();
    }

    private void initView() {
        mTrafficListView = (ListView) findViewById(R.id.traffic_lv_app);
        mProgress = (ProgressBar) findViewById(R.id.traffic_pb);
        mTitle = (TextView) findViewById(R.id.traffic_title);
    }

    private void initData() {
        mAdapter = new TrafficAdapter();
        mTrafficListView.setAdapter(mAdapter);
        final File file = new File("/proc/uid_stat");
        if (!file.exists()) {
            mTitle.setText("手机不支持");
            mProgress.setVisibility(View.GONE);
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mProgress.setVisibility(View.VISIBLE);
                mDatas = new ArrayList<TrafficBean>();
            }

            @Override
            protected Void doInBackground(Void... params) {
                mPm = getPackageManager();
                List<PackageInfo> list = mPm.getInstalledPackages(0);
                for (PackageInfo pkg : list) {
                    TrafficBean bean = new TrafficBean();
                    ApplicationInfo applicationInfo = pkg.applicationInfo;
                    int uid = applicationInfo.uid;
                    if (new File("/proc/uid_stat/" + uid + "/tcp_rcv").exists()) {
                        //Log.i("------------", "应用：" + applicationInfo.loadLabel(mPm) + "-uid-" + uid);
                        bean.rcv = "接收:" + Formatter.formatFileSize(getApplicationContext(), getRcv(uid));
                    } else {
                        bean.rcv = "0KB";
                    }
                    if (new File("/proc/uid_stat/" + uid + "/tcp_snd").exists()) {
                        bean.snd = "发送:" + Formatter.formatFileSize(getApplicationContext(), getSnd(uid));
                    } else {
                        bean.snd = "0KB";
                    }
                    bean.icon = applicationInfo.loadIcon(mPm);
                    bean.name = (String) applicationInfo.loadLabel(mPm);
                    if (getRcv(uid) != 0 || getSnd(uid) != 0)
                        mDatas.add(bean);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mProgress.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private long getRcv(int uid) {
        try {
            File rcvFile = new File("/proc/uid_stat/" + uid + "/tcp_rcv"); //拿到发出流量存储的位置
            if (rcvFile.exists()) {
                BufferedReader rcvReader = new BufferedReader(new FileReader(rcvFile));
                return Long.valueOf(rcvReader.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long getSnd(int uid) {

        try {
            File sndFile = new File("/proc/uid_stat/" + uid + "/tcp_snd"); //拿到收到流量存储的位置
            if (sndFile.exists()) {
                BufferedReader sndReader = new BufferedReader(new FileReader(sndFile));
                return Long.valueOf(sndReader.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private class TrafficAdapter extends BaseAdapter {
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
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_traffic, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_traffic_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_traffic_name);
                holder.tvReceive = (TextView) convertView.findViewById(R.id.item_traffic_receive);
                holder.tvSend = (TextView) convertView.findViewById(R.id.item_traffic_send);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TrafficBean bean = mDatas.get(position);
            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            holder.tvReceive.setText(bean.rcv);
            holder.tvSend.setText(bean.snd);
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvReceive;
        TextView tvSend;
    }
}
