package edu.zjff.shzj.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.tencent.mars.xlog.Log;

import java.io.IOException;

import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.ui.Fregment_index;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetArticleListService extends Service {
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
    int pager;
    String categoryName;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null&&intent.getIntExtra("where",-1)==-1){
            pager = Fregment_index.page;
            categoryName=Fregment_index.categoryName;
        }
        if (categoryName==null)
        GetArticleList();
        else
        GetArticleListofCategory();
        return super.onStartCommand(intent, flags, startId);
    }
    private void GetArticleListofCategory()  {
        HttpUtil.SendPostRequestAddSrings(Constant.BASE1+"android/getArticleOfCategory",
                new String[]{"categoryName","page",UserUtil.user.getToken()==null?"":"token"},
                new String[]{categoryName,pager+"",UserUtil.user.getToken()==null?"":UserUtil.user.getToken()}, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //网络异常
                Intent intent = new Intent(Constant.BROADCAST_GETARTICLE_WRONG);
                sendBroadcast(intent);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strData = response.body().string();
                anyUtil.syso(strData);
                Gson gson = new Gson();
                if (!"{\"error\":\"Document not found\"}".equals(strData)){
                    try{
                        DataRoot data = gson.fromJson(strData, DataRoot.class);
                        Intent intent = new Intent(Constant.BROADCAST_GETARTICLE_SUCCESS);
                        intent.putExtra(Constant.MESSAGE,data);
                        sendBroadcast(intent);
                    }catch (Exception e){}
                }else{
                    Intent intent = new Intent(Constant.BROADCAST_GETARTICLE_WRONG);
                    sendBroadcast(intent);
                }
            }
        });


    }
      private void GetArticleList()  {
        HttpUtil.SendPostRequestAddSrings(Constant.GETARTICLELIST_URL,new String[]{"pager",UserUtil.user.getToken()==null?"":"token"}, new String[]{pager+"", UserUtil.user.getToken()==null?"":UserUtil.user.getToken()}, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //网络异常
                Intent intent = new Intent(Constant.BROADCAST_GETARTICLE_WRONG);
                sendBroadcast(intent);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strData = response.body().string();
                anyUtil.syso(strData);
                Gson gson = new Gson();
                if (!"{\"error\":\"Document not found\"}".equals(strData)){
                    try{
                        DataRoot data = gson.fromJson(strData, DataRoot.class);

                        Intent intent = new Intent(Constant.BROADCAST_GETARTICLE_SUCCESS);
                        Log.e("data.getData().size()",">  >"+data.getData().size());
                        intent.putExtra(Constant.MESSAGE,data);
                        sendBroadcast(intent);
                    }catch (Exception e){}
                }else{
                    Intent intent = new Intent(Constant.BROADCAST_GETARTICLE_WRONG);
                    sendBroadcast(intent);
                }
            }
        });


    }
}
