package com.weyko.databindingbase.view.swipebackhelper;

public interface SwipeListener {
        void onScroll(float percent, int px);
        void onEdgeTouch();
        /**
         * Invoke when scroll percent over the threshold for the first time
         */
        void onScrollToClose();
    }