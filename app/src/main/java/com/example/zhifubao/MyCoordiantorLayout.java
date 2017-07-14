package com.example.zhifubao;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hupihuai on 2017/7/11.
 */

public class MyCoordiantorLayout extends CoordinatorLayout {

    public MyCoordiantorLayout(Context context) {
        super(context);
    }

    public MyCoordiantorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCoordiantorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
