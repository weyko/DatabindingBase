package com.weyko.databindingbase.util;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.weyko.databindingbase.R;
import com.weyko.databindingbase.adapter.BaseListAdapter;
import com.weyko.databindingbase.base.BaseApplication;
import com.weyko.databindingbase.configure.Constant;
import com.weyko.databindingbase.databinding.LayoutListBinding;
import com.weyko.databindingbase.view.RecyclerViewDivider;

import java.util.List;

/**
 * Description:列表管理类
 * Created  by: weyko on 2017/7/27.
 */

public class ListManager<T extends BaseListAdapter>{
    private final int TIME_LOAD_DELAY=800;//数据加载延迟时间
    private LayoutListBinding binding;
    private LinearLayoutManager manager;
    /**
     * 索引的起点位置
     */
    private final int fistIndex = 1;
    // 开始请求的角标
    private int mStart = fistIndex;
    // 一次请求的数量
    private int mCount =10;
    private T adapter;
    private OnListDataLoader onListDataLoader;
    private RecyclerView recyclerView;
    public ListManager(SwipeRefreshLayout swipeRefreshLayout, LayoutListBinding binding, T adapter, OnListDataLoader onListDataLoader) {
        this.swipeRefreshLayout=swipeRefreshLayout;
        this.binding = binding;
        this.adapter = adapter;
        this.onListDataLoader=onListDataLoader;
        manager= new LinearLayoutManager(BaseApplication.getInstance());
        recyclerView=binding.rvFragmentGet;
        recyclerView.setLayoutManager(manager);
        scrollRecycleView();
        initSwipeRefreshLayout(swipeRefreshLayout);
    }
    public ListManager(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView, T adapter, OnListDataLoader onListDataLoader) {
        this.onListDataLoader=onListDataLoader;
        this.adapter = adapter;
        this.swipeRefreshLayout=swipeRefreshLayout;
        this.recyclerView=recyclerView;
        manager= new LinearLayoutManager(BaseApplication.getInstance());
        recyclerView.setLayoutManager(manager);
        scrollRecycleView();
        initSwipeRefreshLayout(swipeRefreshLayout);
    }
    public ListManager(SwipeRefreshLayout swipeRefreshLayout, OnListDataLoader onListDataLoader) {
        this.onListDataLoader=onListDataLoader;
        this.swipeRefreshLayout=swipeRefreshLayout;
        initSwipeRefreshLayout(swipeRefreshLayout);
    }
    public void updateAdapter(List<Object>list){
        if(adapter==null){
            return;
        }
        if(mStart==fistIndex) {
            adapter.setList(list);
        }else{
            int showPosition=adapter.getItemCount()-2;
            manager.scrollToPositionWithOffset(showPosition,0);
            adapter.addAll(list);
        }
        if(recyclerView!=null)
          recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    public void refresh(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
    public void updateAdapter(List<Object>list,int startIndex,int pageCount,long totals){
        if(list!=null)
        updateAdapter(list);
        if(pageCount!=mCount){
            updateState(startIndex==fistIndex?BaseListAdapter.LOAD_END:BaseListAdapter.LOAD_NONE);
        }else{
            updateState(BaseListAdapter.LOAD_MORE);
        }
    }
    public void updateRefreshing(boolean isRefreshing){
        if(swipeRefreshLayout!=null)
         swipeRefreshLayout.setRefreshing(isRefreshing);
    }
    public void updateState(int  state){
        adapter.updateLoadStatus(state);
    }
    private SwipeRefreshLayout swipeRefreshLayout;
    public void initSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){
        if(swipeRefreshLayout==null)return;
        swipeRefreshLayout.setColorSchemeColors(BaseApplication.getInstance().getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRefreshing(true);
                mStart=fistIndex;
                loadList();
            }
        });
    }
    private void scrollRecycleView() {
        if(recyclerView==null)return;
        if(!Constant.TAG_NOLINE.equals(recyclerView.getTag()))
        recyclerView.addItemDecoration(new RecyclerViewDivider(recyclerView.getContext(),manager.getOrientation(),R.mipmap.list_divider_holo_light));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    lastVisibleItem = manager.findLastVisibleItemPosition();

                    /**StaggeredGridLayoutManager*/
//                    int[] into = new int[(manager).getSpanCount()];
//                    lastVisibleItem = findMax(manager.findLastVisibleItemPositions(into));
                    if(adapter!=null){
                        if(adapter.isEnd())return;
                    }
                    if (manager.getItemCount() == 0) {
                        if (adapter != null) {
                            adapter.updateLoadStatus(BaseListAdapter.LOAD_NONE);
                        }
                        return;

                    }
                    if(adapter!=null && adapter.isDone())return;
                    if (swipeRefreshLayout!=null&&!swipeRefreshLayout.isRefreshing()
                            && lastVisibleItem + 1 == manager.getItemCount()) {
                        if (adapter != null) {
                            adapter.updateLoadStatus(BaseListAdapter.LOAD_PULL_TO);
                            // isLoadMore = true;
//                            adapter.updateLoadStatus(AMapAdapter.LOAD_MORE);
                        }
                        //new Handler().postDelayed(() -> getBeforeNews(time), 1000);
                        mStart++;
                        loadList();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();

                /**StaggeredGridLayoutManager*/
//                int[] into = new int[(manager).getSpanCount()];
//                lastVisibleItem = findMax(manager.findLastVisibleItemPositions(into));
            }
        });
    }

    public void loadList() {
        if(swipeRefreshLayout!=null)
          swipeRefreshLayout.postDelayed(new ListRunnable(onListDataLoader,mStart), TIME_LOAD_DELAY);
    }
    private class ListRunnable implements Runnable{
        private OnListDataLoader onListDataLoader;
        private int start;
        public ListRunnable(OnListDataLoader onListDataLoader, int start) {
            this.onListDataLoader = onListDataLoader;
            this.start = start;
        }

        @Override
        public void run() {
            if(onListDataLoader!=null){
                CommonUtil.d("scrollRecycleView--->mStart="+start);
                onListDataLoader.loadList(start);
            }else{
                updateRefreshing(false);
            }
        }
    }
    /**
     * 设置开始索引
     * @param startIndex
     */
    public void setStartIndex(int startIndex){
        this.mStart=startIndex;
    }
    public void onDestory() {

    }
    public interface OnListDataLoader{
      public void loadList(int startIndex);
    }
}
