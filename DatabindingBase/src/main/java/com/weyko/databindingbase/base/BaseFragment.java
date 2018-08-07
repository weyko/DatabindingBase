package com.weyko.databindingbase.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.weyko.databindingbase.R;
import com.weyko.databindingbase.databinding.FragmentBaseBinding;
import com.weyko.databindingbase.listener.PerfectClickListener;
import com.weyko.databindingbase.util.CommonUtil;
import com.weyko.databindingbase.util.ListManager;
import com.weyko.databindingbase.util.ShowLoadManager;
import com.weyko.databindingbase.view.DispatchSwipeRefreshLayout;
import com.weyko.databindingbase.view.FixRecyclerView;
import com.weyko.databindingbase.view.RecyclerViewDivider;

/**
 * 是没有title的Fragment
 */
public abstract class BaseFragment<SV extends ViewDataBinding> extends Fragment {

    // 布局view
    protected SV binding;
    // fragment是否显示了
    protected boolean mIsVisible = false;
    // 刷新布局
    protected DispatchSwipeRefreshLayout swipeRefreshLayout;
//    private CompositeSubscription mCompositeSubscription;
    private ShowLoadManager showLoadManager;
    private ListManager listManager;
    public FragmentBaseBinding baseBinding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View ll = inflater.inflate(R.layout.fragment_base, null);
        baseBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_base, container, true);
        swipeRefreshLayout=baseBinding.viewContainFragment.srPageLoad;
        swipeRefreshLayout.setEnabled(false);
        binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), setContent(), null, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity= Gravity.CENTER;
        binding.getRoot().setLayoutParams(params);
        baseBinding.viewContainFragment.container.addView(binding.getRoot());
        return baseBinding.getRoot();
    }
    /**
     * 隐藏软键盘
     * @param viewMain
     */
    public void hideSoftWindow(View viewMain){
        if(viewMain==null)return;
        CommonUtil.hideSoftWindow(viewMain.getContext(),viewMain);
    }
    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            mIsVisible = true;
            onVisible();
        } else {
            mIsVisible = false;
            onInvisible();
        }
    }
    public void setRecyclerView(FixRecyclerView recyclerView){
        LinearLayoutManager manager=new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerViewDivider(recyclerView.getContext(),manager.getOrientation(),R.mipmap.list_divider_holo_light));
    }
    protected void onInvisible() {
    }

    /**
     * 显示时加载数据,需要这样的使用
     * 注意声明 isPrepared，先初始化
     * 生命周期会先执行 setUserVisibleHint 再执行onActivityCreated
     * 在 onActivityCreated 之后第一次显示加载数据，只加载一次
     */
    protected void loadData(int startIndex) {
    }

    protected void onVisible() {
        loadData(0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showLoadManager=new ShowLoadManager(baseBinding.viewContainFragment,binding.getRoot());
        // 点击加载失败布局
        showLoadManager.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoading();
                onRefresh();
            }
        });
        initData();
    }
    /**
     * 布局
     */
    public abstract int setContent();
    /**
     * 初始化数据
     */
    public abstract void initData();
    /**
     * 加载失败后点击后的操作
     */
    protected void onRefresh() {

    }

    /**
     * 显示加载中状态
     */
    protected void showLoading() {
         showLoadManager.showLoading();
        swipeRefreshLayout.setEnabled(false);
    }

    /**
     * 加载完成的状态
     */
    protected void showContentView() {
        showLoadManager.showContentView();
        swipeRefreshLayout.setEnabled(true);
    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected void showError() {
        showLoadManager.showError();
        swipeRefreshLayout.setEnabled(false);
    }
    /**
     * 加载失败点击重新加载的状态
     */
    protected void showEmpty(String emptyHint) {
        showLoadManager.showEmpty(emptyHint);
        swipeRefreshLayout.setEnabled(true);
    }
    /**
     * 加载失败点击重新加载的状态
     */
    protected void showEmpty(String emptyHint,int icon) {
        showLoadManager.showEmpty(emptyHint,icon,null);
        swipeRefreshLayout.setEnabled(true);
    }
    public DispatchSwipeRefreshLayout getSwipRefreshLayout(){
        return swipeRefreshLayout;
    }
    public void setRefresh(boolean isRefeshable){
        if(swipeRefreshLayout!=null){
            swipeRefreshLayout.setEnabled(isRefeshable);
            swipeRefreshLayout.setColorSchemeColors(BaseApplication.getInstance().getResources().getColor(R.color.colorPrimary));
        }
    }
    public void setSwipeRefreshLayout(ListManager.OnListDataLoader onListDataLoader){
        if(listManager==null){
            listManager=new ListManager(swipeRefreshLayout,onListDataLoader);
        }
    }
    public void loadFinish(){
        if(listManager!=null){
            listManager.updateRefreshing(false);
        }
        if(swipeRefreshLayout!=null){
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void startActivity(Class clzz){
        Intent intent=new Intent(getActivity(),clzz);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right,0);
    }
    public void startActivity(Class clzz,Bundle bundle){
        Intent intent=new Intent(getActivity(),clzz);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right,0);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
