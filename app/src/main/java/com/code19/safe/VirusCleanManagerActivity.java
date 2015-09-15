package com.code19.safe;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code19.safe.bean.AntiVirusBean;
import com.code19.safe.db.AntiVirusDao;
import com.code19.safe.utils.Md5Utils;
import com.github.lzyzsd.circleprogress.ArcProgress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/9/13.
 * 0:16
 */
public class VirusCleanManagerActivity extends Activity {

    private static final String TAG = "VirusCleanManagerActivity";
    private PackageManager mPm;
    private VirusCleanAsyncTask mTask;
    private RelativeLayout mRlScanContainer;
    private ArcProgress mArcProgress;
    private TextView mTvScanPackage;
    private RelativeLayout mRlResultContainer;
    private TextView mTvResult;
    private Button mBtnRescan;
    private ListView mLvListview;
    private List<AntiVirusBean> mDatas;
    private int mVriusCount;
    private VirusCleanAdapter mAdapter;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private LinearLayout mLinearLayoutlAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virusclean);
        mPm = getPackageManager();
        initView();
        initData();
        initEvent();
    }

    //初始化界面
    private void initView() {
        //扫描部分
        mRlScanContainer = (RelativeLayout) findViewById(R.id.aa_scan_container);
        mArcProgress = (ArcProgress) findViewById(R.id.aa_scan_arc_progress);
        mTvScanPackage = (TextView) findViewById(R.id.aa_scan_packagename);
        //结果部分
        mRlResultContainer = (RelativeLayout) findViewById(R.id.aa_result_container);
        mTvResult = (TextView) findViewById(R.id.aa_tv_result);
        mBtnRescan = (Button) findViewById(R.id.aa_btn_rescan);
        //扫描的数据部分
        mLvListview = (ListView) findViewById(R.id.aa_list);

        //动画部分
        mLinearLayoutlAnimator = (LinearLayout) findViewById(R.id.aa_animatator_container);
        mIvLeft = (ImageView) findViewById(R.id.aa_animatator_iv_left);
        mIvRight = (ImageView) findViewById(R.id.aa_animatator_iv_right);
    }

    /**
     * 扫描所有的应用，获得应用的指纹，然后到数据库中去比对md5值
     */
    private void initData() {
        mDatas = new ArrayList<AntiVirusBean>();

        mAdapter = new VirusCleanAdapter();
        mLvListview.setAdapter(mAdapter);

        startScan();
    }

    //初始化事件
    private void initEvent() {
        //重新扫描
        mBtnRescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    private void startScan() {
        mTask = new VirusCleanAsyncTask();
        mTask.execute();
    }

    class VirusCleanAsyncTask extends AsyncTask<Void, AntiVirusBean, Void> {
        //Params, Progress, Result 三个参数
        //将当前任务的进度和进度的最大值传到UI界面
        int progress = 0, max = 0;

        @Override
        protected void onPreExecute() {
            //准备前将mDatas，和病毒数据清空
            mDatas.clear();
            mVriusCount = 0;

            //显示扫描部分
            mRlScanContainer.setVisibility(View.VISIBLE);
            mRlResultContainer.setVisibility(View.GONE);
            mLinearLayoutlAnimator.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            List<PackageInfo> list = mPm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
            max = list.size();
            for (PackageInfo pkg : list) {
                progress++;
                //获取apk文件，获取apk文件指纹
                //拿到apk文件
                String sourceDir = pkg.applicationInfo.sourceDir;
                //获取文件指纹
                FileInputStream in = null;
                try {
                    in = new FileInputStream(new File(sourceDir));
                    String md5 = Md5Utils.encode(in);
                    Log.i(TAG, "包名：" + pkg.packageName + ",Md5:" + md5);
                    //查询数据对比是否病毒
                    boolean safe = AntiVirusDao.isVirus(getApplicationContext(), md5);
                    ApplicationInfo applicationInfo = mPm.getApplicationInfo(pkg.packageName, 0);
                    String name = applicationInfo.loadLabel(mPm).toString();
                    Drawable icon = applicationInfo.loadIcon(mPm);
                    AntiVirusBean bean = new AntiVirusBean();
                    bean.icon = icon;
                    bean.safe = safe;
                    bean.name = name;
                    bean.packageName = pkg.packageName;
                    SystemClock.sleep(100);//线程小睡一会
                    //push出去的是bean数据，那么onProgressUpdate的参数类型就必须是bean类型的
                    //异步任务的Progress参数也必须是bean类型的
                    publishProgress(bean); //通知文本改变
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        in = null;
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(AntiVirusBean... values) {
            int percent = (int) (progress * 100f / max + 0.5f);
            mArcProgress.setProgress(percent);

            AntiVirusBean bean = values[0];
            mTvScanPackage.setText(bean.packageName);

            //给list添加数据
            if (bean.safe) {
                //如果是病毒,就把病毒计数器加一,并把它放到集合最前面去
                mVriusCount++;
                mDatas.add(0, bean);
            } else {
                mDatas.add(bean);
            }
            //UI更新
            mAdapter.notifyDataSetChanged();
            //listView滚动到底部
            mLvListview.smoothScrollToPosition(mDatas.size());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //listView滚动到顶部
            mLvListview.smoothScrollToPosition(0);
            //显示结果部分
            mLinearLayoutlAnimator.setVerticalGravity(View.VISIBLE);
            mRlResultContainer.setVisibility(View.VISIBLE);
            mRlScanContainer.setVisibility(View.GONE);

            //扫描结束开始显示动画,扫描结果做动画
            //获得view显示的bitmap,将view镜像成bitmap
            mRlScanContainer.setDrawingCacheEnabled(true); //可见
            mRlScanContainer.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = mRlScanContainer.getDrawingCache();
            mIvLeft.setImageBitmap(getLeftBitmap(bitmap));//分隔左边
            mIvRight.setImageBitmap(getRightBitmap(bitmap));//分隔右边
            //动画的打开，扫描部分
            openAnimation();

            //显示扫描结果
            if (mVriusCount > 0) {
                mTvResult.setTextColor(Color.RED);
                mTvResult.setText("发现病毒" + mVriusCount + "个，建议卸载病毒应用");
            } else {
                mTvResult.setTextColor(Color.GREEN);
                mTvResult.setText("你的手机很安全");
            }
        }
    }

    //设置左边的动画效果
    private Bitmap getLeftBitmap(Bitmap bitmap) {
        //1.准备画纸
        int width = (int) (bitmap.getWidth() / 2f + 0.5f);
        Bitmap createBitmap = Bitmap.createBitmap(width, bitmap.getHeight(), bitmap.getConfig());
        //2.准备画板，将画纸放到画板上
        Canvas canvas = new Canvas(createBitmap);
        //3.笔
        Paint paint = new Paint();
        //4.规则，矩阵
        Matrix matrix = new Matrix();
        //5.画
        canvas.drawBitmap(createBitmap, matrix, paint);
        return createBitmap;
    }

    //设置右边的动画效果
    private Bitmap getRightBitmap(Bitmap bitmap) {
        //1.准备画纸
        int width = (int) (bitmap.getWidth() / 2f + 0.5f);
        Bitmap createBitmap = Bitmap.createBitmap(width, bitmap.getHeight(), bitmap.getConfig());
        //2.准备画板，将画纸放到画板上
        Canvas canvas = new Canvas(createBitmap);
        //3.笔
        Paint paint = new Paint();
        //4.规则，矩阵
        Matrix matrix = new Matrix();
        matrix.setTranslate(-width, 0);
        //5.画
        canvas.drawBitmap(createBitmap, matrix, paint);
        return createBitmap;
    }

    //打开动画
    private void openAnimation() {
        AnimatorSet set = new AnimatorSet();
        mIvLeft.measure(0, 0);
        int leftWidth = mIvLeft.getMeasuredWidth();
        mIvRight.measure(0, 0);
        int rightWidth = mIvRight.getMeasuredWidth();
        set.playTogether(
                ObjectAnimator.ofFloat(mIvLeft, "translationX", 0, -leftWidth),
                ObjectAnimator.ofFloat(mIvLeft, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(mIvRight, "translationX", 0, rightWidth),
                ObjectAnimator.ofFloat(mIvRight, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(mRlResultContainer, "alpha", 0f, 1f));
        set.setDuration(3000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i(TAG, "开始动画了...l");
                mBtnRescan.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "动画结束了。。。。");
                mBtnRescan.setEnabled(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        set.start();
    }

    //关闭动画
    private void closeAnimation() {
        // mIvLeft:位移，透明度
        AnimatorSet set = new AnimatorSet();

        mIvLeft.measure(0, 0);
        int leftWidth = mIvLeft.getMeasuredWidth();
        mIvRight.measure(0, 0);
        int rightWidth = mIvRight.getMeasuredWidth();

        set.playTogether(
                ObjectAnimator.ofFloat(mIvLeft, "translationX", -leftWidth, 0),
                ObjectAnimator.ofFloat(mIvLeft, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(mIvRight, "translationX", rightWidth, 0),
                ObjectAnimator.ofFloat(mIvRight, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(mLinearLayoutlAnimator, "alpha", 1f, 0f));
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                mBtnRescan.setEnabled(false);
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {

            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                mBtnRescan.setEnabled(true);
                // 动画结束，扫描
                startScan();
            }

            @Override
            public void onAnimationCancel(Animator arg0) {

            }
        });
        set.setDuration(3000);
        set.start();
    }

    class VirusCleanAdapter extends BaseAdapter {

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
            //设置view的复用
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_antvirus, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_virus_iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_virus_tv_name);
                holder.tvSafe = (TextView) convertView.findViewById(R.id.item_virus_tv_safe);
                holder.isClean = (ImageView) convertView.findViewById(R.id.item_virus_iv_clean);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //给listview数据
            final AntiVirusBean bean = mDatas.get(position);
            holder.ivIcon.setImageDrawable(bean.icon);
            holder.tvName.setText(bean.name);
            holder.tvSafe.setTextColor(bean.safe ? Color.RED : Color.GREEN);
            holder.tvSafe.setText(bean.safe ? "病毒" : "安全");
            holder.isClean.setVisibility(bean.safe ? View.VISIBLE : View.GONE);
            holder.isClean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击按钮卸载，跳转到卸载对话框，参考系统应用PackageInstaller
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setAction("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + bean.packageName));
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvSafe;
        ImageView isClean;
    }
}

