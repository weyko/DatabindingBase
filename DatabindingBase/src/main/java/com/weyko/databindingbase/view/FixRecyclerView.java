package com.weyko.databindingbase.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 *  修复RecyclerView在fragment在onDetachedFromWindow时崩溃bug
 * Created by zhong on 2017/11/30.
 */

public class FixRecyclerView extends RecyclerView {
    public FixRecyclerView(Context context) {
        super(context);
    }

    public FixRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FixRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void stopScroll() {
        try {
            super.stopScroll();
        }catch (NullPointerException  e){
//            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        try {
            super.onDetachedFromWindow();
        }catch (NullPointerException  e){
//            e.printStackTrace();
        }
    }
}
