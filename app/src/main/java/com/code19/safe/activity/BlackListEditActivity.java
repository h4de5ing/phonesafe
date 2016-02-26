package com.code19.safe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.code19.safe.R;
import com.code19.safe.bean.BlackBean;
import com.code19.safe.db.BlackDao;

/**
 * Created by Gh0st on 2015/9/4.
 * 12:36
 */
public class BlackListEditActivity extends Activity {

    private static final String TAG = "BlackListEditActivity";
    public static final String KEY_NUMBER = "key_number";
    public static final String KEY_TYPE = "key_type";
    public static final String ACTION_UPDATE = "update";
    public static final String KEY_POSITION = "position";
    private Button mAdd;
    private Button mCancel;
    private RadioGroup mType;
    private EditText mBlNumber;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        initView();
        initEvent();
    }

    //初始化界面，就需要根据请求码判断是做更新操作还是添加操作
    private void initView() {
        mAdd = (Button) findViewById(R.id.bl_btn_add);
        mCancel = (Button) findViewById(R.id.bl_btn_cancel);
        mType = (RadioGroup) findViewById(R.id.bl_rg_type);
        mBlNumber = (EditText) findViewById(R.id.bl_et_number);
        TextView title = (TextView) findViewById(R.id.blTvTitle);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ACTION_UPDATE.equals(action)) {
            Log.i(TAG, "他点过来的是更新，我要开始更新了。。。");
            //更新要做的操作时，界面1，设置标题为更新，2.将编辑框设置为不可编辑，3，添加按钮的文字设置为更新 数据，取出传过来的数据
            //1.UI操作
            title.setText("更新黑名单");
            mAdd.setText("更新");
            mBlNumber.setEnabled(true);//设置为不可编辑
            //2.数据操作
            mBlNumber.setText(intent.getStringExtra(BlackListEditActivity.KEY_NUMBER));
            int type = intent.getIntExtra(BlackListEditActivity.KEY_TYPE, -1);
            int id=-1;
            switch (type) {
                case BlackBean.TYEP_CALL:
                    id=R.id.bl_rb_call;
                    break;
                case BlackBean.TYEP_SMS:
                    id=R.id.bl_rb_sms;
                    break;
                case BlackBean.TYPE_ALL:
                    id=R.id.bl_rb_all;
                    break;
            }

        }
    }

    private void initEvent() {
        //添加按钮的点击事件
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mBlNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(BlackListEditActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int typeId = mType.getCheckedRadioButtonId();
                if (typeId == -1) {//等于-1表示没有选择任何radiobutton
                    Toast.makeText(BlackListEditActivity.this, "拦截类型不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "黑名单号码 ：" + number + "拦截类型：" + type);
                BlackDao dao = new BlackDao(BlackListEditActivity.this);
                boolean insert = dao.insert(number, type);
                if (!insert) {
                    Toast.makeText(BlackListEditActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "存到数据数据库的执行结果" + insert);
                Toast.makeText(BlackListEditActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                //返回添加成功的resultCode,让主界面刷新值,顺便把添加的值带过去
                Intent data = new Intent();
                data.putExtra(KEY_NUMBER, number);
                data.putExtra(KEY_TYPE, type);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);//返回取消的值
                finish();
            }
        });
        //按钮组的选择事件
        mType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //按钮组的改变事件
                switch (checkedId) {
                    case R.id.bl_rb_call:
                        type = 0;
                        break;
                    case R.id.bl_rb_sms:
                        type = 1;
                        break;
                    case R.id.bl_rb_all:
                        type = 2;
                        break;
                }
            }
        });
    }
}
