package com.weyko.databindingbase.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Description:
 * Created  by: weyko on 2017/7/27.
 */

public class BaseViewHolder<T,F extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private F binding;
    public BaseViewHolder(View itemView) {
        super(itemView);
        binding= DataBindingUtil.getBinding(itemView);
    }
    public void bindItem(T bean, int position,OnBindItemListener onBindItemListener) {
       if(onBindItemListener!= null) onBindItemListener.onBindItem(bean,binding,position);
        binding.executePendingBindings();
    }
    public interface OnBindItemListener<T,F extends ViewDataBinding>{
        public void onBindItem(T bean, F binding, int position);
    }
}
