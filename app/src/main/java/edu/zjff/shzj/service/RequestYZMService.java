package edu.zjff.shzj.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RequestYZMService extends Service {
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
        regist();
        return super.onStartCommand(intent, flags, startId);
    }

    private void regist()  {
        HttpUtil.sendRequestToGetYZM(Constant.YZM_URL, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //网络异常
                Intent intent = new Intent(Constant.BROADCAST_YZM_WRONG);
                sendBroadcast(intent);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //该方法运行在子线程
                //发送网络请求成功
                InputStream inputStream = response.body().byteStream();//得到图片的流
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Intent intent = new Intent(Constant.BROADCAST_YZM_SUCCESS);
                intent.putExtra(Constant.YZM,bitmap);
                sendBroadcast(intent);
            }
        });
    }
}
