package com.code19.safe;import android.animation.ObjectAnimator;import android.app.Activity;import android.app.AlertDialog;import android.content.Intent;import android.os.Bundle;import android.text.TextUtils;import android.util.Log;import android.view.View;import android.view.ViewGroup;import android.widget.AdapterView;import android.widget.BaseAdapter;import android.widget.Button;import android.widget.EditText;import android.widget.GridView;import android.widget.ImageView;import android.widget.TextView;import com.code19.safe.bean.HomeBean;import com.code19.safe.utils.Constants;import com.code19.safe.utils.PreferenceUtils;import java.util.ArrayList;import java.util.List;/** * Created by Gh0st on 2015/8/29. * 13:55 * TODO 后期优化要做的事情，给所有点击条目设置selector点击效果 */public class HomeActivity extends Activity {    private static final String TAG = "HomeActivity";    GridView mGridView;    ImageView mIVLogo;    List<HomeBean> mDatas;    private final static String[] TITLES = new String[]{"手机防盗", "骚扰拦截",            "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"};    private final static String[] DESCS = new String[]{"远程定位手机", "全面拦截骚扰",            "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"};    private final static int[] ICONS = new int[]{            R.mipmap.home_item_lost,            R.mipmap.home_item_blacklist,            R.mipmap.home_app_manager,            R.mipmap.home_item_process_manager,            R.mipmap.home_item_gprs,            R.mipmap.home_item_virus_claen,            R.mipmap.home_item_cache_clean,            R.mipmap.home_item_comm_tools};    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_home);        //初始化view        initView();        //logo动画        logoAnimator();        //加载数据        initData();        //处理home_item的事件，主线程做了太多工作，如何优化？ TODO:        initEvent();    }    //1.初始化视图    private void initView() {        mIVLogo = (ImageView) findViewById(R.id.home_iv_logo);        mGridView = (GridView) findViewById(R.id.home_gridview);    }    //2.logo的动画    private void logoAnimator() {        ObjectAnimator animator = ObjectAnimator.ofFloat(mIVLogo, "rotationY", 0, 360);        animator.setDuration(3000);        animator.setRepeatCount(ObjectAnimator.INFINITE);        animator.setRepeatMode(ObjectAnimator.REVERSE);        animator.start();    }    int i = 3;    //3.点击setting图标后启动setting界面    public void clickSetting(View v) {        Intent intent = new Intent(this, SettingActivity.class);        startActivity(intent);    }    //4.初始化数据，主要是加载主界面下方的Item条目    private void initData() {        //加载list数据        mDatas = new ArrayList<HomeBean>();        for (int i = 0; i < ICONS.length; i++) {            HomeBean homeBean = new HomeBean();//对象用来保存数据            homeBean.icon = ICONS[i];            homeBean.title = TITLES[i];            homeBean.desc = DESCS[i];            mDatas.add(homeBean);//集合用来保存对象，mDatas中有数据了        }        //给gridView 加载数据,adapter---List<数据> -->显示什么，数据就是什么类型javabean        mGridView.setAdapter(new MyAdapter());//listView和GridView是通过适配器来显示数据的    }    class MyAdapter extends BaseAdapter {        @Override        public int getCount() {            return mDatas == null ? 0 : mDatas.size();        }        @Override        public Object getItem(int position) {            return mDatas == null ? 0 : mDatas.get(position);        }        @Override        public long getItemId(int position) {            return position;        }        @Override        public View getView(int position, View convertView, ViewGroup parent) {            if (convertView == null) {                //没有复用，需要去加载布局 需要适配屏幕 TODO:                convertView = View.inflate(HomeActivity.this, R.layout.item_home, null);            }            ImageView icon = (ImageView) convertView.findViewById(R.id.item_home_iv_icon);            TextView title = (TextView) convertView.findViewById(R.id.item_home_tv_title);            TextView desc = (TextView) convertView.findViewById(R.id.item_home_tv_desc);            HomeBean homeBean = mDatas.get(position);            icon.setImageResource(homeBean.icon);            title.setText(homeBean.title);            desc.setText(homeBean.desc);            return convertView;        }    }    private void initEvent() {        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {            @Override            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                Intent intent = null;                switch (position) {                    case 0:                        performSjfd();                        break;                    case 1:                        intent = new Intent(HomeActivity.this, CallSmsSafeActivity.class);                        break;                    case 2:                        intent = new Intent(HomeActivity.this, AppManagerActivity.class);                        break;                    case 3:                        intent = new Intent(HomeActivity.this, ProcessManagerActivity.class);                        break;                    case 4:                        intent = new Intent(HomeActivity.this, TrafficActivity.class);                        break;                    case 5:                        intent = new Intent(HomeActivity.this, VirusCleanManagerActivity.class);                        break;                    case 6:                        intent = new Intent(HomeActivity.this, CleanCacheActivity.class);                        break;                    case 7:                        intent = new Intent(HomeActivity.this, ToolsActivity.class);                        break;                }                if (intent != null) {                    startActivity(intent);                }            }        });    }    //Item 1 弹出手机防盗对话框    private void performSjfd() {        String pwd = PreferenceUtils.getString(this, Constants.SJFD_PwD);        if (TextUtils.isEmpty(pwd)) {            showInitPwdDialog();        } else {            showEnterPwdDialog();        }    }    //初始化，设置密码    private void showInitPwdDialog() {        //Toast.makeText(this, "设置密码", Toast.LENGTH_SHORT).show();        final AlertDialog.Builder builder = new AlertDialog.Builder(this);        View view = View.inflate(this, R.layout.dialog_init_pwd, null);        builder.setView(view);        final EditText initPwd = (EditText) view.findViewById(R.id.sjfd_init_pwd);        final EditText confirmPwd = (EditText) view.findViewById(R.id.sjfd_init_confirm);        Button ok = (Button) view.findViewById(R.id.sjfd_init_ok);        Button cancel = (Button) view.findViewById(R.id.sjfd_init_cancel);        final AlertDialog dialog = builder.show();        //确定按钮事件        ok.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                String pwd = initPwd.getText().toString().trim();                String pwd2 = confirmPwd.getText().toString().trim();                if (TextUtils.isEmpty(pwd)) {                    initPwd.requestFocus();                    return;                }                if (TextUtils.isEmpty(pwd2)) {                    confirmPwd.requestFocus();                    return;                }                if (!pwd.equals(pwd2)) {                    confirmPwd.setError("两次密码不一致");                    //Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();                    return;                }                PreferenceUtils.putString(HomeActivity.this, Constants.SJFD_PwD, pwd);                dialog.dismiss();                Intent intent = new Intent(HomeActivity.this, SjfdSetup1Activity.class);                startActivity(intent);                Log.i(TAG, "initPwd:" + initPwd.getText() + ",confirmPwd:" + confirmPwd.getText());            }        });        cancel.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                dialog.dismiss();            }        });    }    //输入进入密码    private void showEnterPwdDialog() {        AlertDialog.Builder builder = new AlertDialog.Builder(this);        View view = View.inflate(this, R.layout.dialog_enter_pwd, null);        final EditText enterPwd = (EditText) view.findViewById(R.id.sjfd_enter_pwd);        Button ok = (Button) view.findViewById(R.id.sjfd_enter_ok);        Button cancel = (Button) view.findViewById(R.id.sjfd_enter_cancel);        builder.setView(view);        final AlertDialog dialog = builder.show();        ok.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                String pass = PreferenceUtils.getString(HomeActivity.this, Constants.SJFD_PwD);                String enterPass = enterPwd.getText().toString();                if (TextUtils.isEmpty(enterPass)) {                    //Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();                    enterPwd.setError("密码不能为空");                    return;                }                if (!pass.equals(enterPass)) {                    enterPwd.setError("密码错误");                    //Toast.makeText(HomeActivity.this, "您输入的密码错误", Toast.LENGTH_SHORT).show();                } else {                    dialog.dismiss();                    Intent intent = new Intent(HomeActivity.this, SjfdActivity.class);                    startActivity(intent);                }            }        });        cancel.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View v) {                dialog.dismiss();            }        });    }}