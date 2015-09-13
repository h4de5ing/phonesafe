package com.code19.safe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/8/29.
 * 19:40
 * 通过inflate得到设置的条目项，绑定view_setting_item.xml
 */
public class SettingItemView extends RelativeLayout {
    private static final int BACKGROUDN_FIRST = 0;
    private static final int BACKGROUDN_MIDDLE = 1;
    private static final int BACKGROUDN_LAST = 2;
    private TextView mTvTitle;
    private ImageView mTvIcon;
    private boolean isOpen = true;//默认关闭

    //new 建对象时用，建对象时就将所有的控件带过去了
    public SettingItemView(Context context) {
        this(context, null);//创建对象时返回三个参数的构造方法
    }

    //xml布局使用   //三个参数的构造方法绑定xml
    public SettingItemView(Context context, AttributeSet set) {
        super(context, set);
        View.inflate(context, R.layout.view_setting_item, this);//加载xml布局，xml和当前类绑定
        mTvTitle = (TextView) findViewById(R.id.view_tv_title);  //布局选项中标题
        mTvIcon = (ImageView) findViewById(R.id.view_tv_icon);   //布局选项中的图标

        //读取自定义属性
        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.SettingItemView);

        String title = ta.getString(R.styleable.SettingItemView_sivTitle);
        int background = ta.getInt(R.styleable.SettingItemView_sivBackground, BACKGROUDN_FIRST);
        boolean toggle = ta.getBoolean(R.styleable.SettingItemView_sivToggle, true);

        ta.recycle();//循环利用

        //设置textView的值
        mTvTitle.setText(title);//找到的标题控件

        //选择背景
        switch (background) {
            case BACKGROUDN_FIRST:
                findViewById(R.id.setting_item_root).setBackgroundResource(R.drawable.setting_item_first_selector);
                break;
            case BACKGROUDN_MIDDLE:
                findViewById(R.id.setting_item_root).setBackgroundResource(R.drawable.setting_item_middle_selector);
                break;
            case BACKGROUDN_LAST:
                findViewById(R.id.setting_item_root).setBackgroundResource(R.drawable.setting_item_last_selector);
                break;
        }

        mTvIcon.setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    /**
     * 开关的方法，开的时候就关，关的时候就开
     */
    public void toggle() {
        mTvIcon.setImageResource(isOpen ? R.mipmap.off : R.mipmap.on); //设置图标
        isOpen = !isOpen; //重置状态
    }

    /**
     * 获得开关状态
     */
    public boolean getToggleState() {
        return isOpen;
    }

    /**
     * 设置开关的状态
     */
    public void setToggleState(boolean state) {
        this.isOpen = state;
        mTvIcon.setImageResource(isOpen ? R.mipmap.on : R.mipmap.off);
    }
}
