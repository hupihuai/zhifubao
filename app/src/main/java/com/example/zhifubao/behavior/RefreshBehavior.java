package com.example.zhifubao.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhifubao.R;
import com.example.zhifubao.helper.HeaderScrollingViewBehavior;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by hupihuai on 2017/7/11.
 */

public class RefreshBehavior extends HeaderScrollingViewBehavior {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private View mChildView;
    private float mLastMoveY;
    private int scrollRange;

    private boolean mIsIntercepted;

    public RefreshBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View findFirstDependency(List<View> views) {
        return null;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        if (mRecyclerView == null) {
            mRefreshLayout = (SwipeRefreshLayout) parent.findViewById(R.id.refreshLayout);
            mRecyclerView = (RecyclerView) parent.findViewById(R.id.recyclerView);
            mChildView = child;
            scrollRange = child.getHeight();
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMoveY = ev.getY();
                View childUnder = findTopChildUnder(parent, (int) ev.getX(), (int) ev.getY());
                final boolean isMe = childUnder.getId() == R.id.header;
                mIsIntercepted = isMe;
                break;
        }

        return mIsIntercepted;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mIsIntercepted) {
                    mRecyclerView.onTouchEvent(ev);
                }
                mLastMoveY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                float destY = y - mLastMoveY + child.getTranslationY();
                System.out.println("destY = " + destY);
                if (destY > 0) {//向下
                    int top = mChildView.getTop();
                    //child 没有完全出现 移动child
                    if (top < 0) {
                        child.setTranslationY(destY);
                    } else {//child 已经完全出现
                        mRecyclerView.onTouchEvent(ev);

                    }

                } else {//recyclerview 是否已经
                    boolean dragRefreshLayout = isDragRefreshLayout();
                    if (dragRefreshLayout) {
                        mRecyclerView.onTouchEvent(ev);
                    } else {//移动
                        mRecyclerView.scrollToPosition(0);
                        child.setTranslationY(destY);
                    }
                }
                mLastMoveY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsIntercepted) {
                    mRecyclerView.onTouchEvent(ev);
                }
                break;

        }

        return true;
    }

    private View findTopChildUnder(ViewGroup parentView, int x, int y) {
        final int childCount = parentView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = parentView.getChildAt(i);
            if (x >= child.getLeft() && x < child.getRight()
                    && y >= child.getTop() && y < child.getBottom()) {
                return child;
            }
        }
        return null;
    }

    private boolean isDragRefreshLayout() {
        boolean isRefresh = true;
        try {
            Field mCurrentTargetOffsetTop = mRefreshLayout.getClass().getDeclaredField("mCurrentTargetOffsetTop");
            mCurrentTargetOffsetTop.setAccessible(true);
            int top = mCurrentTargetOffsetTop.getInt(mRefreshLayout);
            if (top <= -120) {
                isRefresh = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isRefresh;
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        //init
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (dy > 0) {//handle up
            if (scrollRange + child.getTranslationY() > 0) {
                float scrollY = child.getTranslationY() - dy;
                if (scrollRange >= scrollY) {
                    child.setTranslationY(scrollY);
                    consumed[1] = dy;
                } else {
                    float newDy = scrollRange + child.getTranslationY();
                    scrollY = child.getTranslationY() - newDy;
                    child.setTranslationY(scrollY);
                    consumed[1] = (int) newDy;
                }
            }
        } else {//handle down
            boolean canScrollUp = ViewCompat.canScrollVertically(mRecyclerView, -1);
            if (!canScrollUp) {
                float scrollY = child.getTranslationY() - dy;
                if (scrollY <= 0) {
                    child.setTranslationY(scrollY);
                    consumed[1] = dy;
                } else {
                    scrollY = 0;
                    child.setTranslationY(scrollY);
                    consumed[1] = (int) -child.getTranslationY();
                }
            }
        }
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }
}
