package com.code19.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.R;
import com.code19.safe.bean.BlackBean;
import com.code19.safe.db.BlackDao;

import java.util.List;


/**
 * Created by Gh0st on 2015/9/4.
 * 12:06
 * <p/>
 * 黑名单管理页面
 */
public class CallSmsSafeActivity extends Activity {
    private static final String TAG = "CallSmsSafeActivity";
    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_UPDATE = 200;
    private ListView mBlackList;
    private BlackDao mDao;
    private List<BlackBean> mDatas;
    private mBlackListAdapter mAdapter;
    private ImageView mEmpty;
    private ProgressBar mProgressBar;

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callsmssafe);
        //初始化view
        initView();
        //加载数据
        //initData(); //加载数据放到onstart中去，每次界面显示都去刷新数据
        //初始化事件
        initEvent();
        if (mDatas == null) {
            Log.i(TAG, "list为空，表示没有数据显示，就显示一张图片在背后吧");
            mBlackList.setEmptyView(mEmpty);
        }
    }


    //初始化界面,将数据库中的黑名单显示在这个页面上
    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.loading);
        mBlackList = (ListView) findViewById(R.id.css_listview);
        mDao = new BlackDao(CallSmsSafeActivity.this);
        mEmpty = (ImageView) findViewById(R.id.listViewEmpty);
    }

    //拿到全部数据，准备在listview中显示出来,由于是耗时操作，所以在子线程中执行
    //初始化数据,给listView初始化数据采用adapter
    private void initData() {
        mAdapter = new mBlackListAdapter();
        mBlackList.setAdapter(mAdapter);
        //准备拿数据了，就显示加载进入条
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1200);
                mDatas = mDao.queryAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //通知刷新了就可以隐藏进度了
                        mProgressBar.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    //初始化事件
    private void initEvent() {
        mBlackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置item的点击事件，还是打开添加黑名单页面，做更新操作，将点击的条目的号码，position位置，类型，传递到更新页面去
                Log.i(TAG, "点击了黑名单条目" + position);
                BlackBean bean = mDatas.get(position);
                Intent intent = new Intent(CallSmsSafeActivity.this, BlackListEditActivity.class);
                intent.setAction(BlackListEditActivity.ACTION_UPDATE);//传过去是更新
                intent.putExtra(BlackListEditActivity.KEY_NUMBER, bean.number);
                intent.putExtra(BlackListEditActivity.KEY_POSITION, position);
                intent.putExtra(BlackListEditActivity.KEY_TYPE, bean.type);
                startActivityForResult(intent, REQUEST_CODE_UPDATE);
            }
        });

    }

    //点击了添加黑名单图标
    public void clickadd(View v) {
        Log.i(TAG, "点击了添加黑名单按钮");
        Intent intent = new Intent(this, BlackListEditActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, REQUEST_CODE_ADD);//采用这种方法，一旦添加成功了，通知本页面刷新数据
    }

    private class mBlackListAdapter extends BaseAdapter {
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
                convertView = View.inflate(CallSmsSafeActivity.this, R.layout.item_css_callsms, null);
                holder = new ViewHolder();
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.item_css_delete);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.item_css_number);
                holder.tvType = (TextView) convertView.findViewById(R.id.item_css_type);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 给holder的view设置数据,首先判断需要拦截的类型，翻译成相应的文字显示在界面中
            final BlackBean bean = mDatas.get(position);
            String text = "";
            switch (bean.type) {
                case BlackBean.TYEP_CALL:
                    text = "电话拦截";
                    break;
                case BlackBean.TYEP_SMS:
                    text = "短信拦截";
                    break;
                case BlackBean.TYPE_ALL:
                    text = "电话+短信拦截";
                    break;
                default:
                    break;
            }
            holder.tvNumber.setText(bean.number);
            holder.tvType.setText(text);

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean delete = mDao.delete(bean.number);
                    if (delete) {// 数据库删除成功
                        Toast.makeText(CallSmsSafeActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        mDatas.remove(bean);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(CallSmsSafeActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvNumber;
        TextView tvType;
        ImageView ivDelete;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拿到传回来的添加成功的值，刷新界面,并拿到数据
        if (resultCode == REQUEST_CODE_ADD) {
            Log.i(TAG, "返回的数据是添加ok,可以刷新页面了。");
            String number = data.getStringExtra(BlackListEditActivity.KEY_NUMBER);
            int type = data.getIntExtra(BlackListEditActivity.KEY_TYPE, -1);
            BlackBean bean = new BlackBean();
            bean.number = number;
            bean.type = type;
            mAdapter.notifyDataSetChanged();
        } else if (resultCode == REQUEST_CODE_UPDATE) {  //拿到传回来的值是更新的结果
            Log.i(TAG, "返回更新的结果");

        }
    }
}

