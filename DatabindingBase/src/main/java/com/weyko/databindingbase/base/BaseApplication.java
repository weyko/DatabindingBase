package com.weyko.databindingbase.base;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;

import com.weyko.databindingbase.cache.UserData;
import com.weyko.databindingbase.util.CommonUtil;
import java.util.ArrayList;

/**
 * Description:
 * Created  by: weyko on 2017/5/27.
 */

public class BaseApplication extends MultiDexApplication {
    private static BaseApplication instance;
    private ArrayList<FragmentActivity> activities;
    private int screenWith;
    private int screenHeight;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        activities=new ArrayList<>();
    }
    public static BaseApplication getInstance(){
        return instance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public void addActivity(BaseActivity activity) {
        activities.add(0,activity);
    }

    public void removeActivity(BaseActivity activity) {
        activities.remove(activity);
    }
    public FragmentActivity getCurrentActivity() {
        if(activities==null||activities.size()==0)return null;
        return activities.get(0);
    }

    public void removeAll() {
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
                activity.overridePendingTransition(0, 0);
            }
        }
    }
    public int getScreenWith(){
      if(screenWith==0)screenWith= CommonUtil.getScreenSize(this, CommonUtil.ScreenEnum.WIDTH);
        return screenWith;
    }
    public int getScreenHeight(){
        if(screenHeight==0)screenHeight=CommonUtil.getScreenSize(this, CommonUtil.ScreenEnum.HEIGHT);
       return  screenHeight;
    }

    private int mLanguage=1;
    public int getmLanguage() {
        return mLanguage;
    }

    public String getSSOUserId() {
        return String.valueOf(UserData.getInstance().getUserId());
    }
}
