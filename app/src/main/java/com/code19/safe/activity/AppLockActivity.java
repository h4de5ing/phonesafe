package com.code19.safe.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.R;
import com.code19.safe.bean.AppBean;
import com.code19.safe.db.ApplockDao;
import com.code19.safe.engine.AppProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/13.
 * 21:02
 */
public class AppLockActivity extends Activity {

    private SegementView mSegementView;
    private ListView mLockListView;
    private ListView mUnlockListView;
    private TextView mTvTitle;
    private LinearLayout mLlProgress;
    private List<AppBean> mLockDatas;
    private List<AppBean> mUnlockDatas;
    private ApplockDao mDao;
    private ApplockAdapter mUnlockAdapter;
    private ApplockAdapter mLockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        mDao = new ApplockDao(this);
        initView();
        initEvent();
        initData();

    }

    private void initView() {
        mSegementView = (SegementView) findViewById(R.id.app_lock_toggle);
        mLockListView = (ListView) findViewById(R.id.al_listview_lock);
        mUnlockListView = (ListView) findViewById(R.id.al_listview_unlock);
        mTvTitle = (TextView) findViewById(R.id.al_tv_title);
        mLlProgress = (LinearLayout) findViewById(R.id.loadings);
    }

    private void initEvent() {
        mSegementView.setOnSegementSelectedListener(new SegementView.OnSegementSelectedListener() {
            @Override
            public void onSelected(boolean locked) {
                if (locked) {
                    mTvTitle.setText("已加锁" + mLockDatas.size() + "个");
                    mLockListView.setVisibility(View.GONE);
                    mUnlockListView.setVisibility(View.VISIBLE);
                } else {
                    mTvTitle.setText("未加锁" + mUnlockDatas.size() + "个");
                    mLockListView.setVisibility(View.GONE);
                    mUnlockListView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initData() {
        mUnlockAdapter = new ApplockAdapter(false);
        mUnlockListView.setAdapter(mUnlockAdapter);

        mLockAdapter = new ApplockAdapter(true);
        mLockListView.setAdapter(mLockAdapter);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mLlProgress.setVisibility(View.VISIBLE);
                mTvTitle.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(1200);
                List<AppBean> allApps = AppProvider.getAllLauncherApps(getApplicationContext());
                mLockDatas = new ArrayList<AppBean>();
                mUnlockDatas = new ArrayList<AppBean>();
                for (AppBean bean : allApps) {
                    if (mDao.findLock(bean.packageName)) {
                        mLockDatas.add(bean);
                    } else {
                        mUnlockDatas.add(bean);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mLlProgress.setVisibility(View.GONE);
                mTvTitle.setText("未加锁" + mUnlockDatas.size());
                mTvTitle.setVisibility(View.VISIBLE);
                mLockAdapter.notifyDataSetChanged();
                mUnlockAdapter.notifyDataSetChanged();
            }
        }.execute();

    }

    private class ApplockAdapter extends BaseAdapter {
        private boolean lock;

        public ApplockAdapter(boolean lock) {
            this.lock = lock;
        }

        @Override
        public int getCount() {
            if (lock) {
                if (mLockDatas != null) {
                    return mLockDatas.size();
                }
            } else {
                if (mUnlockDatas != null) {
                    return mUnlockDatas.size();
                }
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (lock) {
                if (mLockDatas != null) {
                    return mLockDatas.get(position);
                }
            } else {
                if (mUnlockDatas != null) {
                    return mUnlockDatas.get(position);
                }
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_app_lock, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_app_lock_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_app_lock_name);
                holder.ivLock = (ImageView) convertView.findViewById(R.id.item_app_lock_lock);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给view设置数据
            final AppBean bean = (AppBean) getItem(position);
            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            //图标
            if (lock) {
                //上锁，显示解锁图标
                holder.ivLock.setImageResource(R.mipmap.list_button_lock_default);
            } else {
                //未上锁，显示枷锁图标
                holder.ivLock.setImageResource(R.mipmap.list_button_unlock_default);
            }
            final View view = convertView;
            holder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Object tag = v.getTag();
                    //如果当前view正在做动画，点击无效
                    if (tag != null && tag == true) {
                        return;
                    }
                    if (lock) {
                        //动画的移动条目，从左到右，完成后再去做数据操作
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, -1,
                                Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, 0);
                        ta.setDuration(300);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                v.setTag(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //加锁界面，点击移动到未加锁界面
                                //DB:从数据库中移除
                                boolean delete = mDao.delete(bean.packageName);
                                if (delete) {
                                    Toast.makeText(getApplicationContext(), "移除成功", Toast.LENGTH_SHORT).show();
                                    mLockDatas.remove(bean);
                                    mUnlockDatas.add(bean);
                                    mLockAdapter.notifyDataSetChanged();
                                    mUnlockAdapter.notifyDataSetChanged();
                                    mTvTitle.setText("已加锁" + mLockDatas.size());
                                } else {
                                    Toast.makeText(getApplicationContext(), "移除失败", Toast.LENGTH_SHORT).show();
                                }
                                v.setTag(false);
                            }
                        });
                        view.startAnimation(ta);
                    } else {
                        //未加锁界面，点击，移动到加锁界面
                        //动画的移动条目，从左到右，动画完成后去做数据操作
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, 1,
                                Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, 0
                        );
                        ta.setDuration(300);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                v.setTag(true);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //DB:存储到已加锁的数据库
                                boolean add = mDao.add(bean.packageName);
                                if (add) {
                                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                                    //将未加锁的list中的bean移除，已加锁的list添加bean，让两个adapter去更新
                                    mUnlockDatas.remove(bean);
                                    mLockDatas.add(bean);
                                    mUnlockAdapter.notifyDataSetChanged();
                                    mLockAdapter.notifyDataSetChanged();
                                    mTvTitle.setText("未加锁" + mUnlockDatas.size());
                                } else {
                                    Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_SHORT).show();
                                }
                                v.setTag(false);
                            }
                        });
                        view.startAnimation(ta);
                    }
                }
            });
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView ivIcon;
        ImageView ivLock;
        TextView tvName;
    }
}
