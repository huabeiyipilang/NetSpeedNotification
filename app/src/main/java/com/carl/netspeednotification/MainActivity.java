package com.carl.netspeednotification;

import android.os.Bundle;
import com.carl.netspeednotification.base.BaseActivity;


public class MainActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFragment(MainFragment.class, null);
    }
}
