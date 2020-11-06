package edu.zjff.shzj.util;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bravin.btoast.BToast;
import com.kongzue.dialog.v3.MessageDialog;
import com.shzj.behavior.Demo1Activity;
import com.tencent.mars.xlog.Log;

import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Message;
import edu.zjff.shzj.service.RequestYZMService;

public class MyReceiver extends BroadcastReceiver {
    private Activity activity;

    public MyReceiver(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        Message message;
        switch (intent.getAction()) {
            case Constant.BROADCAST_REG_SUCCESS:
                message = intent.getParcelableExtra(Constant.MESSAGE);
                //ToastUtils.showToast(activity, message.getMessage(), 0);
                if (!"200".equals(message)) {
                    Intent service = new Intent(activity, RequestYZMService.class);
                    activity.startService(service);
                }
                break;
            case Constant.BROADCAST_LOGIN_SUCCESS:
                message=intent.getParcelableExtra(Constant.MESSAGE);
                if (message!=null){
                    SharedPreferencesUtils.setParam(activity, "background", message.getUser().getBackground());
                    SharedPreferencesUtils.setParam(activity, "description", message.getUser().getDescription());
                    SharedPreferencesUtils.setParam(activity,"token", message.getUser().getToken());
                    SharedPreferencesUtils.setParam(activity,"username", message.getUser().getUsername());
                    SharedPreferencesUtils.setParam(activity,"avatar", message.getUser().getAvatar());
                    SharedPreferencesUtils.setParam(activity,"date",message.getUser().getDate());
                    activity.getIntent().putExtra(Constant.MESSAGE,message.getUser());
                    activity.setResult(Activity.RESULT_OK,activity.getIntent());
                    activity.finish();
                }
                else{
                    MessageDialog.show((AppCompatActivity) activity,"提示","账号或密码错误");
                    activity.setResult(Activity.RESULT_CANCELED, activity.getIntent());
                }
                break;
            case Constant.BROADCAST_REG_WRONG:
            case Constant.BROADCAST_YZM_WRONG:
            case Constant.BROADCAST_LOGIN_WRONG:
                BToast.success(context)
                        .text("服務器炸了")
                        .show();
                break;
        }
    }

}