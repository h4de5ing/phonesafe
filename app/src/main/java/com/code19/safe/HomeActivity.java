package com.code19.safe;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.bean.HomeBean;
import com.code19.safe.utils.Constants;
import com.code19.safe.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gh0st on 2015/8/29.
 * 13:55
 * TODO 后期优化要做的事情，给所有点击条目设置selector点击效果
 */
public class HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";
    GridView mGridView;
    ImageView mIVLogo;
    List<HomeBean> mDatas;
    private final static String[] TITLES = new String[]{"手机防盗", "骚扰拦截",
            "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"};
    private final static String[] DESCS = new String[]{"远程定位手机", "全面拦截骚扰",
            "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"};
    private final static int[] ICONS = new int[]{R.mipmap.ed,
            R.mipmap.e5, R.mipmap.es, R.mipmap.ej, R.mipmap.et,
            R.mipmap.ec, R.mipmap.ez, R.mipmap.ew};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //初始化view
        initView();

        //logo动画
        logoAnimator();

        //加载数据
        initData();

        //处理home_item的事件，主线程做了太多工作，如何优化？ TODO:
        initEvent();
    }

    //1.初始化视图
    private void initView() {
        mIVLogo = (ImageView) findViewById(R.id.home_iv_logo); //TODO 点击头像切换选择头像
        mGridView = (GridView) findViewById(R.id.home_gridview);
    }

    //2.logo的动画
    private void logoAnimator() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIVLogo, "rotationY", 0, 360);
        animator.setDuration(3000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    int i = 3;

    //3.点击setting图标后启动setting界面,效果不好，待修改 TODO:
    public void clickSetting(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    //4.初始化数据，主要是加载主界面下方的Item条目
    private void initData() {
        //加载list数据
        mDatas = new ArrayList<HomeBean>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeBean homeBean = new HomeBean();//对象用来保存数据
            homeBean.icon = ICONS[i];
            homeBean.title = TITLES[i];
            homeBean.desc = DESCS[i];
            mDatas.add(homeBean);//集合用来保存对象，mDatas中有数据了
            Log.i(TAG, "添加一个条目：" + TITLES[i]);//输出日志，看看有没有添加成功
        }
        //给gridView 加载数据,adapter---List<数据> -->显示什么，数据就是什么类型javabean
        mGridView.setAdapter(new MyAdapter());//listView和GridView是通过适配器来显示数据的

    }

    //通过继承BaseAdapter的MyAdapter来创建对象
    class MyAdapter extends BaseAdapter {

        //数据的数量
        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        //通过position得到条目
        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return 0;
        }

        //返回itemID
        @Override
        public long getItemId(int position) {
            return position;
        }

        //得到控件
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                //没有复用，需要去加载布局 需要适配屏幕 TODO:
                convertView = View.inflate(HomeActivity.this, R.layout.item_home, null);
            }
            //得到home_item中的icon，title，desc控件
            ImageView icon = (ImageView) convertView.findViewById(R.id.item_home_iv_icon);
            TextView title = (TextView) convertView.findViewById(R.id.item_home_tv_title);
            TextView desc = (TextView) convertView.findViewById(R.id.item_home_tv_desc);

            //将mDatas中的数据取出
            HomeBean homeBean = mDatas.get(position);
            //然后将取出来的数据设置到控件上
            icon.setImageResource(homeBean.icon);
            title.setText(homeBean.title);
            desc.setText(homeBean.desc);

            //然后返回带有数据的控件
            return convertView;
        }

    }

    //处理每一个home_item的事件
    private void initEvent() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        performSjfd();
                        break;
                    case 1:
                        preformSrlj();
                        break;
                    case 2:
                        preformRjgj();
                        break;
                    case 3:
                        preformJcgl();
                        break;
                    case 4:
                        
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        performCygj();
                        break;

                }
            }
        });
    }

    //Item 1 弹出手机防盗对话框
    private void performSjfd() {
        String pwd = PreferenceUtils.getString(this, Constants.SJFD_PwD);
        if (TextUtils.isEmpty(pwd)) {
            //如果为空，弹出对话框，设置密码
            Log.i(TAG, "提示设置密码");
            showInitPwdDialog();
        } else {
            Log.i(TAG, "提示输入密码");
            showEnterPwdDialog();
        }
    }

    //Item 2 骚扰拦截
    private void preformSrlj() {
        Intent intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);
        startActivity(intent);

    }

    // Item 3 软件管家
    private void preformRjgj() {
        Intent intent = new Intent(HomeActivity.this, AppManagerActivity.class);
        startActivity(intent);
    }

    // Item 4 进程管理
    private void preformJcgl() {
        Intent intent = new Intent(HomeActivity.this, ProcessManagerActivity.class);
        startActivity(intent);
    }

    // Item 8 常用工具
    private void performCygj() {
        Intent intent = new Intent(HomeActivity.this, ToolsActivity.class);
        startActivity(intent);

    }

    //初始化，设置密码
    private void showInitPwdDialog() {
        //Toast.makeText(this, "设置密码", Toast.LENGTH_SHORT).show();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_init_pwd, null);//得到对话框布局

        builder.setView(view); //将布局显示到对话中

        //在布局中得到相应控件
        final EditText initPwd = (EditText) view.findViewById(R.id.sjfd_init_pwd);
        final EditText confirmPwd = (EditText) view.findViewById(R.id.sjfd_init_confirm);
        Button ok = (Button) view.findViewById(R.id.sjfd_init_ok);
        Button cancel = (Button) view.findViewById(R.id.sjfd_init_cancel);

        //显示对话框
        final AlertDialog dialog = builder.show();

        //确定按钮事件
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = initPwd.getText().toString().trim();
                String pwd2 = confirmPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    initPwd.requestFocus();//如果密码为空，初始化密码框获得焦点
                    return;
                }
                if (TextUtils.isEmpty(pwd2)) {
                    confirmPwd.requestFocus();//如果确认密码为空，确认密码框获得焦点
                    return;
                }
                if (!pwd.equals(pwd2)) {
                    //密码不一致，弹出提示
                    Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                //如果走到这里，说明两次密码一直，保存密码
                PreferenceUtils.putString(HomeActivity.this, Constants.SJFD_PwD, pwd);

                //保存密码后，隐藏对话框，启动新界面
                dialog.dismiss();
                Intent intent = new Intent(HomeActivity.this, SjfdSetup1Activity.class);
                startActivity(intent);
                Log.i(TAG, "启动设置向导界面");
                Log.i(TAG, "initPwd:" + initPwd.getText() + ",confirmPwd:" + confirmPwd.getText());
            }
        });

        //取消按钮事件
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消按钮事件很简单，取消对话框就是
                dialog.dismiss();
            }
        });
    }

    //输入进入密码
    private void showEnterPwdDialog() {
        //创建对话框对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //拿到对话框布局
        View view = View.inflate(this, R.layout.dialog_enter_pwd, null);
        //拿到布局上面的控件
        final EditText enterPwd = (EditText) view.findViewById(R.id.sjfd_enter_pwd);
        Button ok = (Button) view.findViewById(R.id.sjfd_enter_ok);
        Button cancel = (Button) view.findViewById(R.id.sjfd_enter_cancel);
        //将对话框布局与对话框对象绑定
        builder.setView(view);
        //显示对话框
        final AlertDialog dialog = builder.show();
        //设置ok按钮的监听事件
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拿到保存的密码，方便下面做校验
                String pass = PreferenceUtils.getString(HomeActivity.this, Constants.SJFD_PwD);
                String enterPass = enterPwd.getText().toString();
                if (TextUtils.isEmpty(enterPass)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pass.equals(enterPass)) {
                    //密码错误
                    Log.i(TAG, "密码错误");
                    Toast.makeText(HomeActivity.this, "您输入的密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    //密码正确 关闭密码输入对话框并跳转到手机防盗页面
                    dialog.dismiss();
                    Log.i(TAG, "密码正确" + enterPass);
                    Intent intent = new Intent(HomeActivity.this, SjfdActivity.class);
                    startActivity(intent);
                }
                Log.i(TAG, "你输入的密码：" + enterPwd.getText());
            }
        });
        //输入密码对话框，取消按钮，直接取消对话框，返回主页
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    //清除手机防盗的设置，方便调试.TODO
    public void clearn(View v) {
        PreferenceUtils.putString(this, Constants.SJFD_PwD, "");
        Log.i(TAG, "手机防盗设置清除成功，可以重新设置了");
        Toast.makeText(this, "调试功能:清除手机防盗密码", Toast.LENGTH_SHORT).show();
    }
}
