package com.code19.safe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.bean.ProcessBean;
import com.code19.safe.engine.ProcessProvider;
import com.code19.safe.view.PrograssInfoView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
    private int mRuningProcessCount;
    private int mTotalProcessCout;
    private long mFreeMemory;
    private long mTotalMemory;
    private List<ProcessBean> mMSystemDatas;
    private CheckBox mSelected;

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
        mSelected = (CheckBox) findViewById(R.id.process_item_cb_selected);
        mProcess.setTitle("进程:");
        mMemory.setTitle("内存:");

        mListView = (StickyListHeadersListView) findViewById(R.id.process_lv_processlistview);
    }

    //初始化事件
    private void initEvent() {
        //清理按钮的事件
        mProcessClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击了清理按钮");
                long freeMemory = 0;
                int count = 0;
                ListIterator<ProcessBean> iterator = mDates.listIterator();
                while (iterator.hasNext()) {
                    ProcessBean bean = iterator.next();
                    if (bean.isSelected) {
                        //不知道有没有问题 TODO
                        ProcessProvider.killProcess(ProcessManagerActivity.this, bean.processName);
                        //从集合中移除
                        iterator.remove();
                        mDates.remove(bean);
                        freeMemory += bean.memory;
                        count++;
                    }
                }
                if (count > 0) {
                    String tip = "结束了" + count + "个进程，释放了" + Formatter.formatFileSize(ProcessManagerActivity.this, freeMemory) + "内存";
                    Toast.makeText(ProcessManagerActivity.this, tip, Toast.LENGTH_SHORT).show();
                }
                mProcessListViewAdapter.notifyDataSetChanged();
            }
        });
        //listView的item事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "点击了条目" + position);
                ProcessBean bean= (ProcessBean) mProcessListViewAdapter.getItem(position);
                if(bean.name.equals(getPackageName())){
                    return ;
                }
                bean.isSelected=!bean.isSelected;
                mProcessListViewAdapter.notifyDataSetChanged();
            }
        });
    }

    //初始化数据
    private void initData() {
        loadProcessCount();
        loadMemory();

        //加载listview
        mProcessListViewAdapter = new ProcessListViewAdapter();
        mListView.setAdapter(mProcessListViewAdapter);
        //加载数据，在子线程中执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDates = ProcessProvider.getRunningProcess(ProcessManagerActivity.this);
                mUserDatas = new ArrayList<ProcessBean>();
                mMSystemDatas = new ArrayList<ProcessBean>();
                for (ProcessBean bean : mDates) {
                    if (bean.isSystem) {
                        mMSystemDatas.add(bean);
                    } else {
                        mUserDatas.add(bean);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProcessListViewAdapter.notifyDataSetChanged();//通知界面数据发生改变
                    }
                });
                //清空集合，然后按先放用户应用进程，后放系统进程
                mDates.clear();
                mDates.addAll(mUserDatas);
                mDates.addAll(mMSystemDatas);
            }
        }).start();

    }

    private void loadMemory() {
        //内存容量
        mFreeMemory = ProcessProvider.getFreeMemory(this);
        mTotalMemory = ProcessProvider.getTotalMemory(this);
        long usedMemory = mTotalMemory - mFreeMemory;
        mMemory.setProgress((int) (usedMemory * 100f / mTotalMemory + 0.5));
        mMemory.setLeft("占用内存" + Formatter.formatFileSize(this, mFreeMemory));
        mMemory.setRight("内存总容量:" + Formatter.formatFileSize(this, mTotalMemory));
    }

    private void loadProcessCount() {
        //进程数量
        mRuningProcessCount = ProcessProvider.getRuningProcessCount(this);
        mTotalProcessCout = ProcessProvider.getTotalProcessCout(this);
        mProcess.setProgress((int) (mRuningProcessCount * 100f / mTotalProcessCout + 0.5));
        mProcess.setLeft("运行进程" + mRuningProcessCount + "个");
        mProcess.setRight("进程总共" + mTotalProcessCout + "个");
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
                convertView = View.inflate(ProcessManagerActivity.this, R.layout.item_process_info, null);
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
                convertView.setBackgroundColor(Color.GRAY);
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
            return bean.isSystem ? 0 : 1;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvMeory;
        CheckBox cbSelected;
    }
}