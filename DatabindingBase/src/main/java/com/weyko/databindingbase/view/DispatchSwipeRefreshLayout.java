package com.weyko.databindingbase.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zhong on 2018/7/5.
 */

public class DispatchSwipeRefreshLayout extends SwipeRefreshLayout {
    public View canScrollView;
    public DispatchSwipeRefreshLayout(Context context) {
        super(context);
    }

    public DispatchSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScrollView(View canScrollView) {
        this.canScrollView = canScrollView;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(canScrollView!=null){
            Rect rect=new Rect();
            canScrollView.getGlobalVisibleRect(rect);
            if(rect.contains((int)event.getRawX(),(int)event.getRawY())){
                canScrollView.onTouchEvent(event);
                return false;
            }
        }
        return super.onInterceptTouchEvent(event);
    }
}
