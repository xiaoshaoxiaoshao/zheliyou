package edu.zjff.shzj.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.zjff.shzj.entity.User;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2020/5/19.
 * 发送http请求的工具类
 *
 */

public class HttpUtil<callback> {
    /**
     * get请求获取服务端返回的输入流
     * @param path 天气请求路径
     * @return 输入流
     * @throws Exception
     */

    static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    Log.d("reg->cookie:","cookie to put:"+url.host()+","+cookies.toString());
                    cookieStore.put(url.host(),cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();
    public static InputStream getInputStream(String path){
        //1.创建Url对象
        URL url= null;
        InputStream is=null;
        HttpURLConnection conn=null;
        try {
            url = new URL(path);
            //2.通过Url获取HttpUrlConnection
             conn= (HttpURLConnection) url.openConnection();
            //3.设置请求方式
            conn.setRequestMethod("GET");
            //4.设置连接超时，读取超时的毫秒值
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            //5.发送请求获取服务端返回的数据
             is=conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * 将服务端的输入流，转换成字符串
     * @param is
     * @return
     * @throws Exception
     */
    public static String isToString(InputStream is) {
        StringBuilder sb=null;
        BufferedReader br=null;
        try {
             br=new BufferedReader(new InputStreamReader(is));//BufferedReader 缓冲字符输入流,一次可以读取一行数据, InputStreamReader字符转换流,它可以将字节流转换成字符流
            String line=null;
             sb=new StringBuilder();//
            while((line=br.readLine())!=null){
                System.out.println(line);
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();//json数据
    }


    public static void sendRequestToRegist(String url, User user, Callback callback) {
        RequestBody requestBody =  new FormBody.Builder()
                .add("username",user.getUsername())
                .add("password",user.getPassword())
                .add("email", user.getEmail())/*
                .add("phone", user.getPhone())
                .add("captcha", user.getCaptcha())*/
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
    public static void sendRequestToGetYZM(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendRequestToLogin(String url, User user, Callback callback) {

        RequestBody requestBody =  new FormBody.Builder()
                .add("username",user.getUsername())
                .add("password",user.getPassword())
                .build();
        anyUtil.syso(requestBody.toString());
        anyUtil.syso("username"+user.getUsername());
        anyUtil.syso("password"+user.getPassword());
        /*
        OkHttpClient client1 = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        client1.newCall(request).enqueue(callback);*/

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendRequestToGetArticleList(String url,int page, Callback callback) {

        RequestBody requestBody =  new FormBody.Builder()
                .add("page",page+"")
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody).build();
        client.newCall(request).enqueue(callback);

    }

    public static void SENDPOSTREQ(String url, Callback callback) {
        RequestBody requestBody =  new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    public static void SENDGETREQ(String url, Callback callback) {

        Request request = new Request.Builder()
                .url(url).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendPostRequestAddString(String url ,String parameterName, String parameter , Callback callback){

        RequestBody requestBody =  new FormBody.Builder()
                .add(parameterName,parameter)
                .build();
        Request request = new Request.Builder()
                .url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
    public static void SendPostRequestAddSrings(String url ,String[] parameterName, String[] parameter , Callback callback){
        RequestBody requestBody;
        FormBody.Builder builder =  new FormBody.Builder();
        for (int i=0 ; i<parameter.length;i++){
            if(parameter[i]==""||parameterName[i]=="")
                continue;
                builder.add(parameterName[i],parameter[i]);
        }
        requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
}
