package com.code19.safe.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Gh0st on 2015/8/29.
 * 19:12
 * 自定义控件，设置控件的跑马灯效果,如果在大屏上，屏幕足够宽，就不会有跑马灯效果
 */

public class FocusedTextView extends TextView {

    public FocusedTextView(Context context) {
        this(context, null);
    }

    //设置自定义控件的属性
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleLine();//设置单行
        setEllipsize(TextUtils.TruncateAt.MARQUEE);//椭圆
        setFocusable(true);
        setFocusableInTouchMode(true);//
    }

    //欺骗window获得了焦点
    @Override
    public boolean isFocused() {
        return true;
    }

    //不会被其他控件抢夺焦点
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    //窗口改变也不会抢夺焦点
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
        }
    }
}
