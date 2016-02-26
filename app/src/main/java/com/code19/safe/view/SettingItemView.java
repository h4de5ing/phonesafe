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
 */
public class SettingItemView extends RelativeLayout {
    private static final int BACKGROUDN_FIRST = 0;
    private static final int BACKGROUDN_MIDDLE = 1;
    private static final int BACKGROUDN_LAST = 2;
    private ImageView mTvIcon;
    private boolean isOpen = true;//默认关闭

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet set) {
        super(context, set);
        View.inflate(context, R.layout.view_setting_item, this);
        TextView mTvTitle = (TextView) findViewById(R.id.view_tv_title);
        mTvIcon = (ImageView) findViewById(R.id.view_tv_icon);
        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.SettingItemView);
        String title = ta.getString(R.styleable.SettingItemView_sivTitle);
        int background = ta.getInt(R.styleable.SettingItemView_sivBackground, BACKGROUDN_FIRST);
        boolean toggle = ta.getBoolean(R.styleable.SettingItemView_sivToggle, true);
        ta.recycle();
        mTvTitle.setText(title);
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
        mTvIcon.setImageResource(isOpen ? R.mipmap.off : R.mipmap.on);
        isOpen = !isOpen;
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
