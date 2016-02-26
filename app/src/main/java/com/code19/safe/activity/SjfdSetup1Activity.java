package com.code19.safe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.code19.safe.R;

/**
 * Created by Gh0st on 2015/8/31.
 * 2:47
 */
public class SjfdSetup1Activity extends SjfdBaseActivity {
    private String TAG = "SjfdSetup1Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sjfd_setup1);
    }

    @Override
    protected boolean doPre() {
        return false;
    }

    @Override
    protected boolean doNext() {
        Intent intent = new Intent(SjfdSetup1Activity.this, SjfdSetup2Activity.class);
        startActivity(intent);
        return true;
    }

}
