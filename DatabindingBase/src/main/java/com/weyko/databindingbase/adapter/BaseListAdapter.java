package com.weyko.databindingbase.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weyko.databindingbase.R;
import com.weyko.databindingbase.databinding.FooterItemBookBinding;
import com.weyko.databindingbase.databinding.HeaderItemBookBinding;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<T,F extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private int status = LOAD_PULL_TO;
    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;
    private static final int TYPE_TOP = -1;

    private static final int TYPE_FOOTER_BOOK = -2;
    private static final int TYPE_HEADER_BOOK = -3;
    private static final int TYPE_CONTENT_BOOK = -4;
    private List<T> list;
    private int itemResouce;
    private BaseViewHolder.OnBindItemListener onBindItemListener;
    public BaseListAdapter(int itemResouce, BaseViewHolder.OnBindItemListener<T,F> onBindItemListener) {
        list = new ArrayList<>();
        this.itemResouce=itemResouce;
        this.onBindItemListener=onBindItemListener;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER_BOOK;
        } else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER_BOOK;
        } else {
            return TYPE_CONTENT_BOOK ;
        }
    }
    public abstract View headViewLayout();
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER_BOOK:
                HeaderItemBookBinding mBindHeader = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.header_item_book, parent, false);
                View headerView=headViewLayout()==null?mBindHeader.getRoot():headViewLayout();
                return new HeaderViewHolder(headerView);
            case TYPE_FOOTER_BOOK:
                FooterItemBookBinding mBindFooter = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.footer_item_book, parent, false);
                return new FooterViewHolder(mBindFooter.getRoot());
            default:
                ViewDataBinding itemTaskBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), itemResouce, parent, false);
                return new BaseViewHolder<T,F>(itemTaskBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case TYPE_HEADER_BOOK:
                if(holder instanceof BaseListAdapter.HeaderViewHolder) {
                    HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                    headerViewHolder.bindItem();
                }
                break;
            case TYPE_FOOTER_BOOK:
                if(holder instanceof BaseListAdapter.FooterViewHolder) {
                    FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                    footerViewHolder.bindItem();
                    footerViewHolder.setIsRecyclable(true);
                }
                break;
            default:
                if(holder instanceof BaseViewHolder) {
                    BaseViewHolder bookViewHolder = (BaseViewHolder) holder;
                    if (list != null && list.size() > 0) {
                        // 内容从"1"开始
                        bookViewHolder.bindItem(list.get(position - 1), position - 1, onBindItemListener);
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 2;
    }

    /**
     * 处理 GridLayoutManager 添加头尾布局占满屏幕宽的情况
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeader(position) || isFooter(position)) ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 处理 StaggeredGridLayoutManager 添加头尾布局占满屏幕宽的情况
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    /**
     * 这里规定 position = 0 时
     * 就为头布局，设置为占满整屏幕宽
     */
    private boolean isHeader(int position) {
        return position >= 0 && position < 1;
    }

    /**
     * 这里规定 position =  getItemCount() - 1时
     * 就为尾布局，设置为占满整屏幕宽
     * getItemCount() 改了 ，这里就不用改
     */
    private boolean isFooter(int position) {
        return position < getItemCount() && position >= getItemCount() - 1;
    }

    /**
     * footer view
     */
    private class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterItemBookBinding mBindFooter;

        FooterViewHolder(View itemView) {
            super(itemView);
            mBindFooter = DataBindingUtil.getBinding(itemView);
            mBindFooter.rlMore.setGravity(Gravity.CENTER);
//            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dipToPx(context, 40));
//            itemView.setLayoutParams(params);
        }

        private void bindItem() {
            switch (status) {
                case LOAD_MORE:
                    mBindFooter.progress.setVisibility(View.VISIBLE);
                    mBindFooter.tvLoadPrompt.setText(R.string.load_loading);
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_PULL_TO:
                    mBindFooter.progress.setVisibility(View.GONE);
                    mBindFooter.tvLoadPrompt.setText(R.string.load_more);
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_NONE:
                    System.out.println("LOAD_NONE----");
                    mBindFooter.progress.setVisibility(View.GONE);
                    mBindFooter.tvLoadPrompt.setText(R.string.load_nomore);
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder {

//        HeaderItemBookBinding mBindTask;

        HeaderViewHolder(View view) {
            super(view);
//            mBindTask = DataBindingUtil.getBinding(view);
        }

        private void bindItem() {
//            mBindTask.executePendingBindings();
        }
    }
    public boolean isEnd(){
        return this.status==LOAD_END;
    }
    public boolean isDone(){
        return this.status==LOAD_NONE;
    }
    public void updateLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }
    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void addAll(List<T> list) {
        this.list.addAll(list);
    }

}
