package com.weyko.databindingbase.util;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.weyko.databindingbase.base.BaseFragment;

import java.util.HashMap;

/**
 * Description:权限管理类
 * Created  by: weyko on 2017/6/19.
 */

public class PermissionManager {

    private HashMap<Integer,OnPermissionListener>allowMaps;
    private RequestPermisson requestPermission;
    public static class RequestPermisson{
        /**
         * 获取位置权限
         */
        public final static int PERMISSION_LOCATION=0;
        /**
         * 写入SD卡权限
         */
        public final static int PERMISSION_SDCARD_WRITE=PERMISSION_LOCATION+1;
        /**
         * 读取联系人权限
         */
        public final static int PERMISSION_CONTACTS=PERMISSION_SDCARD_WRITE+1;
        /**
         * 拨打电话权限
         */
        public final static int PERMISSION_PHONE=PERMISSION_CONTACTS+1;
        /**
         * 发送短信权限
         */
        public final static int PERMISSION_SMS=PERMISSION_PHONE+1;
        /**
         * 读取短信权限
         */
        public final static int PERMISSION_SMS_READ=PERMISSION_SMS+1;
        /**
         * 读取手机状态
         */
        public final static int READ_PHONE_STATE=PERMISSION_SMS_READ+1;
        /**
         * 读取语音播放权限
         */
        public final static int RECORD_AUDIO=READ_PHONE_STATE+1;
        /**
         * 读取拍照权限
         */
        public final static int RECORD_CAMERA=RECORD_AUDIO+1;
        /**
         * 权限内容
         */
        private String permissonMsg="";
        public String getPermission(int requestCode){
            switch (requestCode){
                case PERMISSION_LOCATION:
                    permissonMsg="获取位置权限";
                    return Manifest.permission.ACCESS_COARSE_LOCATION;
                case PERMISSION_SDCARD_WRITE:
                    permissonMsg="写入SD卡权限";
                    return Manifest.permission.WRITE_EXTERNAL_STORAGE;
                case PERMISSION_CONTACTS:
                    permissonMsg="读取联系人权限";
                    return Manifest.permission.WRITE_CONTACTS;
                case PERMISSION_PHONE:
                    permissonMsg="拨打电话权限";
                    return Manifest.permission.CALL_PHONE;
                case PERMISSION_SMS:
                    permissonMsg="发送短信权限";
                    return Manifest.permission.SEND_SMS;
                case PERMISSION_SMS_READ:
                    permissonMsg="读取短信权限";
                    return Manifest.permission.READ_SMS;
                case READ_PHONE_STATE:
                    permissonMsg="读取手机状态";
                    return Manifest.permission.READ_PHONE_STATE;
                case RECORD_AUDIO:
                    permissonMsg="语音播放权限";
                    return Manifest.permission.RECORD_AUDIO;
                case RECORD_CAMERA:
                    permissonMsg="拍照权限";
                    return Manifest.permission.CAMERA;
            }
            return null;
        }

        /**
         * 获取权限内容
         * @return
         */
        public String getPermissionMsg(){
            return permissonMsg;
        }
    }
    public PermissionManager() {
        allowMaps=new HashMap<>();
        requestPermission=new RequestPermisson();
    }

    /**
     * 检查权限
     * @param requestCode 权限请求码，调用RequestPermisson类内部对应请求码
     * @param onPermissionListener 权限允许之后的操作
     */
    public void checkPermisson(BaseFragment baseFragment, int requestCode, OnPermissionListener onPermissionListener){
        if(onPermissionListener==null)return;
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            String permission=requestPermission.getPermission(requestCode);
            if(permission==null)return;
            int checkPermission  = ContextCompat.checkSelfPermission(baseFragment.getActivity(), permission);
            if(checkPermission!= PackageManager.PERMISSION_GRANTED){
                allowMaps.put(requestCode,onPermissionListener);
                baseFragment.requestPermissions(new String[]{permission},requestCode);
            }else{
                onPermissionListener.onPermissionCheckResult(true,requestCode);
            }
//        }else{
//            onPermissionListener.onPermissionCheckResult(true);
//        }
    }
    /**
     * 检查权限
     * @param requestCode 权限请求码，调用RequestPermisson类内部对应请求码
     * @param onPermissionListener 权限允许之后的操作
     */
    public void checkPermisson(final FragmentActivity baseActivity, final int requestCode, OnPermissionListener onPermissionListener){
        if(onPermissionListener==null)return;
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
           final   String permission=requestPermission.getPermission(requestCode);
            if(permission==null)return;
            int checkPermission  = ContextCompat.checkSelfPermission(baseActivity, permission);
           if(checkPermission!= PackageManager.PERMISSION_GRANTED){
               allowMaps.put(requestCode,onPermissionListener);
               ActivityCompat.requestPermissions(baseActivity,new String[]{permission},requestCode);
            }else{
                onPermissionListener.onPermissionCheckResult(true,requestCode);
            }
//        }else{
//            onPermissionListener.onPermissionCheckResult(true);
//        }
    }

    /**
     * 用户选择不再提示时，弹出系统框
     * @param baseActivity
     * @param requestCode
     * @param permission
     * @return
     */
    private void checkIsNeedShowSystemDialog(final FragmentActivity baseActivity, final int requestCode, final String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(baseActivity, permission)) {
            showMessageOKCancel(baseActivity,"You need to allow access to Contacts",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(baseActivity,new String[]{permission},requestCode);
                        }
                    });
        }else{
            ActivityCompat.requestPermissions(baseActivity,new String[]{permission},requestCode);
        }
    }

    /**
     * 权限检查结果处理方法，在检查的类中重写调用此方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length==0)return;
        OnPermissionListener allowRun = allowMaps.get(requestCode);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(allowRun!=null)
            allowRun.onPermissionCheckResult(true,requestCode);
        } else {
            if(allowRun!=null)allowRun.onPermissionCheckResult(false,requestCode);////PERMISSION_DENIED
            CommonUtil.showToast("拒绝"+requestPermission.getPermissionMsg());
        }
    }
    public interface OnPermissionListener{
        public void onPermissionCheckResult(boolean isAllow, int requestCode);
    }
    public void onDestory() {
        allowMaps.clear();
        allowMaps=null;
        requestPermission=null;
    }
    private void showMessageOKCancel(FragmentActivity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
