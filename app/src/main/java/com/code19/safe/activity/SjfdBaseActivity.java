package com.code19.safe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/8/31.
 * 11:12
 * 做一个向导页面共同部分的模板
 * 基类做成抽象类，方法全部做成抽象方法，让子类必须去实现这些方法
 */
public abstract class SjfdBaseActivity extends Activity {
    private static final String TAG = "SjfdBaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup1);
    }

    /***
     * 由于5个步骤中的功能重复，所以可以抽取5个界面中的共同方法
     * 每个页面点击按钮需要三步，1.打开新界面，2.切换界面的动画 3.结束自己
     * 打开新的界面并不确定，定义打开新界面的抽象方法，子类自己去实现，
     * 切换界面动画和结束自己，有父类实现，子类继承就可以直接调用
     */


    public void onClickPre(View v) {
        if (!doPre()) {
            return;
        }
        overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
        finish();
    }

    public void onClickNext(View v) {
        if (!doNext()) {
            return;
        }
        //doNext();//如果后面的setup3.4.5没有问题，就删掉这句。TODO:
        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
        finish();
    }

    protected abstract boolean doPre();

    protected abstract boolean doNext();
}
