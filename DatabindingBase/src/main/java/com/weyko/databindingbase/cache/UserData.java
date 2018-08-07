package com.weyko.databindingbase.cache;

import com.weyko.databindingbase.base.BaseApplication;

/**
 * Created by zhong on 2017/11/22.
 */

public class UserData {
    private static UserData instance;
    private SharedPref sharedPref;
    public static UserData getInstance(){
        if(instance==null){
            synchronized (UserData.class){
                instance=new UserData();
            }
        }
        return instance;
    }

    public UserData() {
        sharedPref=new SharedPref(BaseApplication.getInstance(),"user_info");
    }
    public void setUserId(long userId){
        sharedPref.putLong("userId",userId);
    }
    public void setToken(String token){
        sharedPref.putString("token",token);
    }
    public void setAccount(String account){
        sharedPref.putString("account",account);
    }
    public void setAddress(String address){
        sharedPref.putString(getUserId()+"address",address);
    }
    public void setCountryCode(String countryCode){
        sharedPref.putString("countryCode",countryCode);
    }
    public void setCityCode(int cityCode){
        sharedPref.putInt("cityCode",cityCode);
    }
    public void setCountryName(String countryName){
        sharedPref.putString("countryName",countryName);
    }
    public void setLongitude(String longitude){
        sharedPref.putString("longitude",longitude);
    }
    public void setLatitude(String latitude){
        sharedPref.put("latitude",latitude);
    }

    public String getAddress(){
        return sharedPref.getString(getUserId()+"address","");
    }
    public String getLongitude(){
        return sharedPref.getString("longitude","0");
    }
    public String getLatitude(){
        return sharedPref.getString("latitude","0");
    }
    public long getUserId(){
        return sharedPref.getLong("userId",0);
    }
    public String getToken(){
        return sharedPref.getString("token","");
    }
    public String getAccount(){
        return sharedPref.getString("account","");
    }
    public String getBalance(String address){
        return sharedPref.getString(address+"balance","");
    }
    public void setPayData(String payData){
        sharedPref.putString("payData",payData);
    }
    public String getPayData(){
        return sharedPref.getString("payData","");
    }
    public void setNickName(String nickName){
        sharedPref.putString("nickName",nickName);
    }
    public String getNickName(){
        return sharedPref.getString("nickName","");
    }
}
