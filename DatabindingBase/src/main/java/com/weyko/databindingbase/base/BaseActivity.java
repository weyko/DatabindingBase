package com.weyko.databindingbase.base;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.weyko.databindingbase.R;
import com.weyko.databindingbase.databinding.ActivityBaseBinding;
import com.weyko.databindingbase.listener.PerfectClickListener;
import com.weyko.databindingbase.util.CommonUtil;
import com.weyko.databindingbase.util.PermissionManager;
import com.weyko.databindingbase.util.ShowLoadManager;
import com.weyko.databindingbase.view.FixRecyclerView;
import com.weyko.databindingbase.view.RecyclerViewDivider;
import com.weyko.databindingbase.view.swipebackhelper.SwipeBackHelper;
import com.weyko.databindingbase.view.swipebackhelper.SwipeListener;

import org.greenrobot.eventbus.EventBus;

/**
 * Description:
 * Created  by: weyko on 2017/5/27.
 */

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    public T binding;
    public ActivityBaseBinding baseBinding;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ShowLoadManager showLoadManager;
    private PermissionManager permissionManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base);
        binding = DataBindingUtil.inflate(getLayoutInflater(), setContent(), baseBinding.viewLoadPageActivity.container, true);
        showTitle(headerTitle());
        swipeRefreshLayout=baseBinding.viewLoadPageActivity.srPageLoad;
        baseBinding.viewLoadPageActivity.container.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                CommonUtil.hideSoftWindow(v.getContext(), v);
            }
        });
        showLoadManager=new ShowLoadManager(baseBinding.viewLoadPageActivity,baseBinding.viewLoadPageActivity.container);
        setRefreshable(false);
        initData();
        showBack(true);
        if (isFilterActivity()) {
            initSwipBackHelper();
        }
        permissionManager=new PermissionManager();
        BaseApplication.getInstance().addActivity(this);
    }
    public void setRefreshing(boolean isRefreshing){
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }
    public void setRefreshable(boolean isRefreshable){
        swipeRefreshLayout.setEnabled(isRefreshable);
    }
    public boolean isRereshable(){
        return swipeRefreshLayout.isEnabled();
    }
    public void setRecyclerView(FixRecyclerView recyclerView){
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new RecyclerViewDivider(recyclerView.getContext(),manager.getOrientation(), R.mipmap.list_divider_holo_light));
    }
    protected boolean isShowAnimation = true;

    //右滑关闭
    private void initSwipBackHelper() {
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)//获取当前页面
                .setSwipeBackEnable(true)//设置是否可滑动
                .setSwipeEdgePercent(0.2f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
                .setSwipeSensitivity(0.5f)//对横向滑动手势的敏感程度。0为迟钝 1为敏感
//                .setScrimColor(Color.GRAY)//底层阴影颜色
                .setClosePercent(0.6f)//触发关闭Activity百分比
                .setSwipeRelateEnable(true)//是否与下一级activity联动(微信效果)。默认关
                .setSwipeRelateOffset(500)//activity联动时的偏移量。默认500px。
                .addListener(new SwipeListener() {
                    @Override
                    public void onScroll(float percent, int px) {

                    }

                    @Override
                    public void onEdgeTouch() {

                    }

                    @Override
                    public void onScrollToClose() {
                        isShowAnimation = false;
                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (isFilterActivity()) {
            SwipeBackHelper.onPostCreate(this);
        }
    }

    public void startActivityByIntent(Intent intent) {
        startActivity(intent);
        overridePendingTransition();
    }

    public void startActivityByIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition();
    }

    private boolean isFilterActivity() {
        return false;
//        return !(this instanceof MainActivity)
//                && !(this instanceof LoginActivity);
    }

    @Override
    public void onBackPressed() {
        if (onBackIntercept()) return;
        super.onBackPressed();
    }

    abstract protected int setContent();

    abstract protected void initData();

    abstract protected boolean onBackIntercept();

    abstract protected String headerTitle();

    public void overridePendingTransition() {
        overridePendingTransition(R.anim.in_from_right, 0);
    }

    private boolean isShowingBack = false;

    public void showBack(boolean show) {
        isShowingBack = show;
        baseBinding.titleActivityBase.ivBack.setVisibility(show?View.VISIBLE:View.GONE);
        baseBinding.titleActivityBase.ivBack.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    public boolean isShowingBack() {
        return isShowingBack;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().removeActivity(this);
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
        if (isFilterActivity()) {
            SwipeBackHelper.onDestroy(this);
        }
    }

    private View showView;

    public void setShowView(View showView) {
        this.showView = showView;
    }

    public boolean isWindowShow() {
        return showView != null && showView.getVisibility() == View.VISIBLE;
    }

    public void hideWindow() {
        if (showView != null) showView.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (showView != null) {
                if (isWindowShow()) {
                    if (isFilterRect(ev, showView)) {
                        return super.dispatchTouchEvent(ev);
                    }
                    hideWindow();
                    return true;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否为过滤控件
     *
     * @return
     */
    private boolean isFilterRect(MotionEvent ev, View view) {
        if (view == null) return false;
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect.contains((int) ev.getX(), (int) ev.getY());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.out_from_right);
    }

    private Snackbar snackbar;

    public void snackShow(String toast) {
        if (snackbar == null)
            snackbar = Snackbar.make(baseBinding.coord, toast, Snackbar.LENGTH_LONG);
        else snackbar.setText(toast);
        snackbar.show();
    }
    public void hideToolBar() {
        if (null != getSupportActionBar()) {
            getSupportActionBar().hide();
        }else {
            baseBinding.titleActivityBase.getRoot().setVisibility(View.GONE);
        }
    }
    /**
     * 加载完成的状态
     */
    protected void showContentView() {
        showLoadManager.showContentView();
        swipeRefreshLayout.setEnabled(true);
    }
    /**
     * 显示加载中状态
     */
    protected void showLoading() {
        showLoadManager.showLoading();
    }
    /**
     * 加载失败点击重新加载的状态
     */
    protected void showError() {
        showLoadManager.showError();
    }
    /**
     * 加载失败点击重新加载的状态
     */
    protected void showEmpty(String emptyHint) {
        showLoadManager.showEmpty(emptyHint);
    }
    /**
     * 加载失败点击重新加载的状态
     */
    protected void showEmpty(String emptyHint, int iconResource, PerfectClickListener onClickListener) {
        showLoadManager.showEmpty(emptyHint,iconResource,onClickListener);
    }

    /**
     * 设置点击事件
     * @param onClickListener
     */
    protected void setErrorOnClick(final PerfectClickListener onClickListener){
        showLoadManager.setOnClickListener(onClickListener);
    }

    public void showTitle(String title) {
        if (title == null) {
            baseBinding.titleActivityBase.getRoot().setVisibility(View.GONE);
            return;
        }
        baseBinding.titleActivityBase.tvTitle.setVisibility(TextUtils.isEmpty(title)?View.GONE:View.VISIBLE);
        baseBinding.titleActivityBase.tvTitle.setText(title);
        if(isShowingBack){

        }
    }

    public void showMenu(int menuIcon, PerfectClickListener onClickListener) {
        baseBinding.titleActivityBase.ivRightLayoutTitle.setVisibility(menuIcon == 0 ? View.GONE : View.VISIBLE);
        baseBinding.titleActivityBase.ivRightLayoutTitle.setImageResource(menuIcon);
        baseBinding.titleActivityBase.ivRightLayoutTitle.setOnClickListener(onClickListener);
        baseBinding.titleActivityBase.tvMenuLayoutTitle.setVisibility(View.GONE);
    }
    public void showMenu(String menuTxt, PerfectClickListener onClickListener) {
        boolean empty = TextUtils.isEmpty(menuTxt);
        baseBinding.titleActivityBase.tvMenuLayoutTitle.setVisibility(empty ? View.GONE : View.VISIBLE);
        baseBinding.titleActivityBase.tvMenuLayoutTitle.setText(menuTxt);
        baseBinding.titleActivityBase.tvMenuLayoutTitle.setOnClickListener(onClickListener);
    }

    public void showLeft(String leftTxt, PerfectClickListener onClickListener) {
        baseBinding.titleActivityBase.tvBackLayoutTitle.setVisibility(TextUtils.isEmpty(leftTxt) ? View.GONE : View.VISIBLE);
        baseBinding.titleActivityBase.tvBackLayoutTitle.setText(leftTxt);
        baseBinding.titleActivityBase.tvBackLayoutTitle.setOnClickListener(onClickListener);
    }
    public void setEditable(boolean isEditable){
        baseBinding.titleActivityBase.tvMenuLayoutTitle.setEnabled(isEditable);
    }
    public void hideAll(){
        baseBinding.titleActivityBase.tvTitle.setVisibility(View.GONE);
        baseBinding.titleActivityBase.tvMenuLayoutTitle.setVisibility(View.GONE);
        baseBinding.titleActivityBase.ivRightLayoutTitle.setVisibility(View.GONE);
        baseBinding.titleActivityBase.tvBackLayoutTitle.setVisibility(View.GONE);
        baseBinding.titleActivityBase.ivBack.setVisibility(View.GONE);
    }
    public void invisibeView(View view){
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(permissionManager!=null){
            permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
