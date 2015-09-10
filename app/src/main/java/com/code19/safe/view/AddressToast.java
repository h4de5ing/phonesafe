package com.code19.safe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/9/6.
 * 23:06
 */
public class AddressToast extends Dialog {
    public AddressToast(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //去掉dialog的title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //获得window的layoutparam
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(params);
        setContentView(R.layout.dialog_number_address_style);  //TODO style没有做效果
    }
}
