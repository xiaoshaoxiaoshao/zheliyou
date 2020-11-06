package edu.zjff.shzj.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;

import edu.zjff.shzj.entity.Message;
import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegistService extends Service {
    /**
     * 参考书上的用法,
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 每次启动服务该方法都会执行
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        User user = intent.getParcelableExtra(Constant.USER);
        regist(user);
        return super.onStartCommand(intent, flags, startId);
    }

    private void regist(User user)  {
        HttpUtil.sendRequestToRegist(Constant.REGIST_URL,user, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //网络异常
                Intent intent = new Intent(Constant.BROADCAST_REG_WRONG);
                sendBroadcast(intent);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {//该方法运行在子线程
                //发送网络请求成功
                String strData = response.body().string();
                Gson gson = new Gson();
                //一行代码将json数据转换成实体类
                Message message = gson.fromJson(strData,Message.class);

                Log.e("mes:",strData);
                Intent intent = new Intent(Constant.BROADCAST_REG_SUCCESS);
                intent.putExtra(Constant.MESSAGE,message);
                sendBroadcast(intent);
            }
        });
    }

}
