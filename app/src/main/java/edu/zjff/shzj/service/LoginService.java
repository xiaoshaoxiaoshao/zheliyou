package edu.zjff.shzj.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import edu.zjff.shzj.entity.Message;
import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.SharedPreferencesUtils;
import edu.zjff.shzj.util.UserUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginService extends Service {
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
        Login(user);
        return super.onStartCommand(intent, flags, startId);
    }

    private void Login(User user)  {
        HttpUtil.sendRequestToLogin(Constant.LOGIN_URL,user, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                //网络异常
                Intent intent = new Intent(Constant.BROADCAST_LOGIN_WRONG);
                sendBroadcast(intent);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {//该方法运行在子线程
                //发送网络请求成功
                String strData = response.body().string();
                Gson gson = new Gson();
                Message message = gson.fromJson(strData,Message.class);
                Intent intent = new Intent(Constant.BROADCAST_LOGIN_SUCCESS);
                if("200".equals(message.getCode())){
                    UserUtil.user=message.getUser();
                    message.getUser().setDate(getBaiduDatetime());
                    intent.putExtra(Constant.MESSAGE,message);
                }
                sendBroadcast(intent);
            }
        });

    }
    private Long getBaiduDatetime(){
        try {
            URL url = new URL("http://www.baidu.com");// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            return ld;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
