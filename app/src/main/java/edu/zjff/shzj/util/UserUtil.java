package edu.zjff.shzj.util;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;

import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.ui.Activity_login_welcome;

public class UserUtil {
    public static User user =new User();
    public static boolean isLogin(Activity activity){
        if (user.getDate()==0){
            user.setUsername(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "username", "error").toString());
            user.setAvatar(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "avatar", "error").toString());
            user.setBackground(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "background", "error").toString());
            user.setDescription(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "description", "error").toString());
            user.setToken(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "token", "error").toString());
            user.setDate((Long)SharedPreferencesUtils.getParam(activity.getApplicationContext(), "date",new Long(0)));
        }
        if ((System.currentTimeMillis()-user.getDate())/1000<86400) {
            return true;
        } else {
            showLoginDiaLog((AppCompatActivity)activity);
            return false;
        }
    }

    private static void showLoginDiaLog(AppCompatActivity activity) {
        MessageDialog.show(activity, "提示", "你还没登录，请先登录。")
                .setOkButton("确定", new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        //TODO 登录页
                        activity.startActivityForResult(new Intent(activity.getApplicationContext(), Activity_login_welcome.class).putExtra("where",1), Constant.REQUEST_USER);
                        return false;
                    }
                })
                .setCancelButton("取消", new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        return false;
                    }
                });
    }

    public static boolean isLogin_notshowDialog(Activity activity){

        if (user.getDate()==0){
            user.setUsername(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "username", "error").toString());
            user.setAvatar(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "avatar", "error").toString());
            user.setBackground(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "background", "error").toString());
            user.setDescription(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "description", "error").toString());
            user.setToken(SharedPreferencesUtils.getParam(activity.getApplicationContext(), "token", "error").toString());
            user.setDate((Long)SharedPreferencesUtils.getParam(activity.getApplicationContext(), "date",new Long(0)));
        }
        if ((System.currentTimeMillis()-user.getDate())/1000<86400) {
            return true;
        } else {
            return false;
        }
    }

    public static void exit(Activity activity){
        user=new User();
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "username", "");
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "avatar","");
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "background", "");
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "description", "");
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "token", "");
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "date",new Long(0));
    }
    public static void save(Activity activity){

        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "username", user.getUsername());
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "avatar",user.getAvatar());
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "background", user.getBackground());
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "description", user.getDescription());
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "token", user.getToken());
        SharedPreferencesUtils.setParam(activity.getApplicationContext(), "date",user.getDate());
    }
}
