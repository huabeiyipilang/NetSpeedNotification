package com.carl.netspeednotification.base;

import android.content.Context;
import android.widget.RelativeLayout;

abstract public class BaseItemView extends RelativeLayout {

    public BaseItemView(Context context) {
        super(context);
        inflate(context, getLayoutRes(), this);
        initViews();
    }

    abstract protected int getLayoutRes();

    abstract protected void initViews();

    abstract public void bindData(Object data);
}
