package edu.zjff.shzj.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import edu.zjff.shzj.ui.MainActivity;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PermissionUtils{
    Activity activity;
    String PERMISSION_STORAGE_MSG = "请授予权限，否则影响部分使用功能";
    public int PERMISSION = 10001;
    public String[] PERMS = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission. READ_PHONE_STATE

    };
    public PermissionUtils( Activity activity){
        this.activity=activity;
    }

    public void request(){

        if (!EasyPermissions.hasPermissions(activity, PERMS)) {
            // 没有申请过权限，现在去申请
            /**
             *@param host Context对象
             *@param rationale  权限弹窗上的提示语。
             *@param requestCode 请求权限的唯一标识码
             *@param perms 一系列权限
             */
            EasyPermissions.requestPermissions(activity, PERMISSION_STORAGE_MSG, PERMISSION, PERMS);
        }
    }

}
