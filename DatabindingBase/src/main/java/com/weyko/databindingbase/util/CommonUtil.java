package com.weyko.databindingbase.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.weyko.databindingbase.base.BaseApplication;
import com.weyko.databindingbase.configure.Constant;

import static android.widget.Toast.makeText;

/**
 * Created by zhong on 2018/8/6.
 */

public class CommonUtil {
    /**
     * 屏幕枚举 ScreenEnum
     *
     * @author weyko 2015年3月19日上午10:33:21 包含属性WIDTH（宽）,HEIGHT（高）,DENSITY（密度）
     */
    public enum ScreenEnum {
        WIDTH, HEIGHT, DENSITY
    }
    /**
     * 获取屏幕大小信息
     *
     * @param context
     * @param screenEnum
     *            获取所需类型数据.WIDTH:宽；HEIGHT：高；DENSITY：密度
     * @return
     */
    public static int getScreenSize(Context context, ScreenEnum screenEnum) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        switch (screenEnum) {
            case WIDTH:
                return metrics.widthPixels;
            case HEIGHT:
                return metrics.heightPixels;
            case DENSITY:
                return metrics.densityDpi;
        }
        return 0;
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * @Title: showSoftWindow
     * @param:
     * @Description: 显示软键盘
     * @return void
     */
    public static void hideSoftWindow(Context context, View view) {
        if(context==null||view==null)
            return;
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
    private static Toast setToast(Toast toast){
        toast.setGravity(Gravity.TOP,0,BaseApplication.getInstance().getScreenHeight()/4);
        toast.show();
        return toast;
    }
    /**
     * 弹出提示框
     * @param msgResId
     *            提示内容的资源id
     */
    public static void showToast(int msgResId) {
        String msg="";
        BaseApplication context = BaseApplication.getInstance();
        try{
            msg=context.getResources().getString(msgResId);
        }catch(Resources.NotFoundException e){
            e.printStackTrace();
        }
        setToast(makeText(context, msg, Toast.LENGTH_SHORT));
    }
    /**
     * 弹出提示框
     * @param msg
     *            提示内容
     */
    public static void showToast(String msg) {
        setToast(makeText(BaseApplication.getInstance(), msg, Toast.LENGTH_SHORT));
    }
    public static void d(String msg) {
        if (Constant.isDebug)
            Log.d("weyko", msg);
    }
}
