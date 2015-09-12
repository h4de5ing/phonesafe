package com.code19.safe;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.code19.safe.bean.ProcessBean;
import com.code19.safe.engine.ProcessProvider;
import com.code19.safe.view.PrograssInfoView;

import java.util.ArrayList;
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
    private ImageView mProcessClean;
    private List<ProcessBean> mDates;
    private List<ProcessBean> mUserDatas;
    private StickyListHeadersListView mListView;
    private boolean isShowSystem = true;
    private ProcessListViewAdapter mProcessListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_process);
        initView();
        initData();
        initEvent();
    }

    //初始化界面
    private void initView() {
        mProcess = (PrograssInfoView) findViewById(R.id.process_piv_processcount);
        mMemory = (PrograssInfoView) findViewById(R.id.process_piv_memory);
        mProcessClean = (ImageView) findViewById(R.id.process_iv_clearn);
        mProcess.setTitle("进程:");
        mMemory.setTitle("内存:");

        mListView = (StickyListHeadersListView) findViewById(R.id.process_lv_processlistview);
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

        //加载listview
        mProcessListViewAdapter = new ProcessListViewAdapter();
        mListView.setAdapter(mProcessListViewAdapter);
        //加载数据，在子线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDates = ProcessProvider.getRunningProcess(ProcessManagerActivity.this);
                mUserDatas = new ArrayList<ProcessBean>();
                List<ProcessBean> mSystemDatas = new ArrayList<>();
                for (ProcessBean bean : mDates) {
                    if (bean.isSystem) {
                        mSystemDatas.add(bean);
                    } else {
                        mUserDatas.add(bean);
                    }
                }
//                mDates.clear();
//                mDates.add(mUserDatas);
//                mDates.add(mSystemDatas);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessListViewAdapter.notifyDataSetChanged();//通知界面数据发生改变
                    }
                });
            }
        }).start();

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
            if (isShowSystem) {//显示所有数据
                if (mDates != null) {
                    return mDates.size();
                }
            } else {//只显示用户进程
                if (mDates != null) {
                    return mUserDatas.size();
                }
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (isShowSystem) {
                if (mDates != null) {
                    return mDates.get(position);
                }
            } else {
                if (mUserDatas != null) {
                    return mUserDatas.get(position);
                }
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                //拿到converview的样式
                convertView = View.inflate(ProcessManagerActivity.this, R.layout.process_info, null);
                convertView.setTag(holder);
                //给holder绑定控件
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.process_item_iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.process_item_tv_name);
                holder.tvMeory = (TextView) convertView.findViewById(R.id.process_item_tv_memory);
                holder.cbSelected = (CheckBox) convertView.findViewById(R.id.process_item_cb_selected);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ProcessBean bean = (ProcessBean) getItem(position);

            //给holder的控件加载数据
            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            holder.tvMeory.setText("占用内存:" + Formatter.formatFileSize(ProcessManagerActivity.this, bean.memory));
            //将安全卫士的进程排除在外
            if (bean.name.equals(getPackageName())) {
                holder.cbSelected.setVisibility(View.GONE);
            } else {
                holder.cbSelected.setVisibility(View.VISIBLE);
            }
            holder.cbSelected.setChecked(bean.isSelected);
            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            //头部时可以复用的
            if (convertView == null) {
                convertView = new TextView(ProcessManagerActivity.this);
                convertView.setPadding(4, 4, 4, 4);
            }
            ProcessBean bean = (ProcessBean) getItem(position);
            TextView tv = (TextView) convertView;
            tv.setText(bean.isSystem ? "系统进程" : "用户进程");
            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            //返回头部样式的唯一标记
            ProcessBean bean = (ProcessBean) getItem(position);
            return bean.isSelected ? 0 : 1;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvMeory;
        CheckBox cbSelected;
    }
}