package com.code19.safe;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.code19.safe.engine.PorcessBean;
import com.code19.safe.engine.ProcessProvider;
import com.code19.safe.view.PrograssInfoView;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Gh0st on 2015/9/9.
 * 18:00
 */
public class ProcessManagerActivity extends Activity {


    private static final String TAG = "ProcessManagerActivity";
    private PrograssInfoView mProcess;
    private PrograssInfoView mMemory;
    private List<PorcessBean> mRunningProcess;
    private ImageView mProcessClean;
    private StickyListHeadersListView mMListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.process_activity);
        initView();
        initData();
        initEvent();
    }

    //初始化界面
    private void initView() {
        mProcess = (PrograssInfoView) findViewById(R.id.process_piv_processcount);
        mMemory = (PrograssInfoView) findViewById(R.id.process_piv_memory);
        mMListView = (StickyListHeadersListView) findViewById(R.id.process_lv_processlistview);
        mProcessClean = (ImageView) findViewById(R.id.process_iv_clearn);
        mProcess.setTitle("进程:");
        mMemory.setTitle("内存:");

        //listView展示所有的进程信息
//        List<PorcessBean> runningProcess = ProcessProvider.getRunningProcess(this); //得到运行中的进程的信息
//        for (PorcessBean runapp : runningProcess) {
//            //icon，名称(没有就显示进程名) ，占用内存，是否系统应用
//            Log.i(TAG, "进程名:" + runapp.processName + ",应用名：" + runapp.name + ",系统：" + runapp.isSystem);
//        }
//        Log.i(TAG, "内存总空间：" + Formatter.formatFileSize(this, totalMemory));
//        Log.i(TAG, "低版本总内存空间:" + Formatter.formatFileSize(this, ProcessProvider.loadLowVersionTotalMemory()));
//        Log.i(TAG, runingProcessCount + "个进程在运行,总共" + totalProcessCout + "个进程");

        mRunningProcess = ProcessProvider.getRunningProcess(this);
    }

    //初始化数据
    private void initData() {
        //进程数量
        int runingProcessCount = ProcessProvider.getRuningProcessCount(this); //得到运行中的进程数
        int totalProcessCout = ProcessProvider.getTotalProcessCout(this); //得到进程的总数
        mProcess.setProgress((int) (runingProcessCount * 100f / totalProcessCout + 0.5));
        mProcess.setLeft("运行进程" + runingProcessCount + "个");
        mProcess.setRight("进程总共" + totalProcessCout + "个");

        //内存容量
        long freeMemory = ProcessProvider.getFreeMemory(this);//得到剩余内存容量
        long totalMemory = ProcessProvider.getTotalMemory(this); //得到总的内存容量
        long usedMemory = totalMemory - freeMemory;
        mMemory.setProgress((int) (usedMemory * 100f / totalMemory + 0.5));
        mMemory.setLeft("占用内存" + Formatter.formatFileSize(this, freeMemory));
        mMemory.setRight("内存总容量:" + Formatter.formatFileSize(this, totalMemory));

        ProcessListViewAdapter processListViewAdapter = new ProcessListViewAdapter();
        mMListView.setAdapter(processListViewAdapter);

    }

    //初始化事件
    private void initEvent() {
        mProcessClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了清理按钮");
            }
        });
    }


    private class ProcessListViewAdapter extends BaseAdapter implements StickyListHeadersAdapter {
        @Override
        public int getCount() {
            return 0;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public long getHeaderId(int position) {
            return 0;
        }
    }
}