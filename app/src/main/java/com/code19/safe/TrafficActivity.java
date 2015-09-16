package com.code19.safe;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class TrafficActivity extends AppCompatActivity {

    private static final String TAG = "TrafficActivity";
    private PackageManager mPm;
    private TextView mTrafficListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        initView();
        initData();

        mPm = getPackageManager();
        Log.i(TAG, "-------------------由于这个类拿到的数据不准确，所以不使用-------------");
        Log.i(TAG, "所有网络接收的字节流量总数" + Formatter.formatFileSize(getApplicationContext(), TrafficStats.getTotalRxBytes()));
        Log.i(TAG, "所有网络接收的数据包总数" + Formatter.formatFileSize(getApplicationContext(), TrafficStats.getTotalRxPackets()));

        Log.i(TAG, "所有网络发出的字节流量总数" + Formatter.formatFileSize(getApplicationContext(), TrafficStats.getTotalTxBytes()));
        Log.i(TAG, "所有网络发出的数据包总数" + Formatter.formatFileSize(getApplicationContext(), TrafficStats.getTotalTxPackets()));

        //可以通过拿到系统底层的日志文件来获取，更加准确
        final File file = new File("/proc/uid_stat");
        if (!file.exists()) {
            Log.i(TAG, "目录不存在....你去哪儿拿日志");
            return;
        }
        List<PackageInfo> list = mPm.getInstalledPackages(0);
        long rcvCount = 0;
        long sndCount = 0;
        for (PackageInfo pkg : list) {
            ApplicationInfo applicationInfo = pkg.applicationInfo;
            try {
                int uid = applicationInfo.uid; //拿到uid
                File rcvFile = new File("/proc/uid_stat/" + uid + "/tcp_rcv"); //拿到发出流量存储的位置
                if (rcvFile.exists()) {
                    BufferedReader rcvReader = new BufferedReader(new FileReader(rcvFile));
                    Long rcvLong = Long.valueOf(rcvReader.readLine());
                    rcvCount += rcvLong;
                    String rcv = Formatter.formatFileSize(getApplicationContext(), rcvLong);
                    //String rcv = Formatter.formatFileSize(getApplicationContext(), Long.valueOf(rcvReader.readLine()));
                    Log.i(TAG, applicationInfo.loadLabel(mPm) + "的,uid" + uid + ",收到：" + rcv);
                }

                File sndFile = new File("/proc/uid_stat/" + uid + "/tcp_rcv"); //拿到收到流量存储的位置
                if (sndFile.exists()) {
                    BufferedReader sndReader = new BufferedReader(new FileReader(sndFile));
                    Long sndLong = Long.valueOf(sndReader.readLine());
                    sndCount += sndLong;
                    String snd = Formatter.formatFileSize(getApplicationContext(), sndLong);
                    //String snd = Formatter.formatFileSize(getApplicationContext(), Long.valueOf(sndReader.readLine()));
                    Log.i(TAG, applicationInfo.loadLabel(mPm) + "--uid:" + uid + ",发出:" + snd);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "收到的：" + Formatter.formatFileSize(getApplicationContext(), rcvCount) + ",发送：" + Formatter.formatFileSize(getApplicationContext(), sndCount));
    }

    private void initView() {
        mTrafficListView = (TextView) findViewById(R.id.traffic_lv_app);
    }

    private void initData() {

    }

}
