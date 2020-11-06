package edu.zjff.shzj.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.StackView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bravin.btoast.BToast;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnItemClickListener;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.pictureselector.adapter.GridImageAdapter;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.FileUtils;
import edu.zjff.shzj.util.GlideEngine;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import edu.zjff.shzj.widget.FullyGridLayoutManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Activity_InsertNewArticle extends AppCompatActivity {
    String QiniuToken="";private UploadManager uploadManager;
    GridImageAdapter mAdapter;
    ExecutorService cachedThreadPool = Executors.newFixedThreadPool(3);
    private String keyname,categoryName="";
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.stackLabelView)
    StackLabel stackLabelView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.content)
    TextView content;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertarticle);
        anyUtil.setNavigation(this);
        ButterKnife.bind(this);
        setView();
    }
    private void setView(){
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this,
                4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(this, 8), false));
        mAdapter = new  GridImageAdapter(Activity_InsertNewArticle.this,onAddPicClickListener);
        mRecyclerView.setAdapter(mAdapter);


        HttpUtil.SENDPOSTREQ(Constant.GETAllCATEGORY, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                Log.e("s:","s:"+s);
                if(!s.contains("200"))
                    return;
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(s);
                JsonObject object = element.getAsJsonObject();  // 转化为对象
                JsonArray comm = object.get("data").getAsJsonArray();
                ArrayList labels = new ArrayList<>();
                for (JsonElement js:comm){
                    JsonObject jo = js.getAsJsonObject();
                    labels.add(jo.get("categoryName").getAsString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stackLabelView.setLabels(labels);
                        stackLabelView.setOnLabelClickListener(new OnLabelClickListener() {
                            @Override
                            public void onClick(int index, View v, String s) {
                                categoryName=s;

                                //BToast.info(Activity_InsertNewArticle.this).text(s).show();
                            }
                        });
                    }
                });
            }
        });
    }


    @BindView(R.id.submit_insertarticle)
    TextView submit;
    @OnClick(R.id.iv_back)
    public void back(){
        onBackPressed();
    }
    @OnClick(R.id.submit_insertarticle)
    public void submit(){
        submit.setClickable(false);
        Log.e("submit()","selectlist:"+mAdapter.getData().size());
        if(UserUtil.isLogin(Activity_InsertNewArticle.this))
            if(mAdapter.getData()!=null&&mAdapter.getData().size()!=0)
                    upload();
            else{
                sendwenzhang(UserUtil.user.getToken(),title.getText()+"",content.getText()+"",categoryName,null);
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_USER:
                    UserUtil.user =data.getParcelableExtra(Constant.MESSAGE);
                    BToast.success(Activity_InsertNewArticle.this).text("欢迎回来~").show();
                    break;
                default:
                    break;
            }
        }
    }
    private void sendwenzhang(String token,String title,String content,String categoryname,@Nullable String pictures){
        HttpUtil.SendPostRequestAddSrings(Constant.INSERTNewArticle, new String[]{"token","title","content","categoryName","pictures"}, new String[]{token,title,content,categoryname,pictures==null?"":pictures}, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                BToast.success(Activity_InsertNewArticle.this).text("发布失败，请稍后再试").show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        anyUtil.hideSoftKeyboard(Activity_InsertNewArticle.this);
                        onBackPressed();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("发文章","文章:"+response.body().string());
                BToast.success(Activity_InsertNewArticle.this).text("发布游记成功，请等待管理员审核").show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        anyUtil.hideSoftKeyboard(Activity_InsertNewArticle.this);
                        onBackPressed();
                    }
                });
            }
        });


    }

        String pictures="";
    public void upload() {
        uploadcount=0;
        HttpUtil.SENDGETREQ(Constant.GETQINIUTOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("七牛Token", "七牛Token:我Token你妈服务器都没了");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                QiniuToken = s.substring(s.indexOf(",") + 10, s.lastIndexOf("\""));
                Log.e("七牛Token", "七牛Token:" + QiniuToken);

                if(cachedThreadPool.isShutdown())
                    cachedThreadPool = Executors.newFixedThreadPool(3);

            for(LocalMedia lm:mAdapter.getData()){
                Log.e("开始上传","开始上传");
                    cachedThreadPool.execute(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!uploads(QiniuToken,lm.getPath()))
                                cachedThreadPool.shutdownNow();
                        }
                    }));
                if(cachedThreadPool.isShutdown())
                    break;
            }
            }
        });
    }
    boolean flag;int uploadcount=0;
    private boolean uploads(String uploadToken,String uploadFilePath) {
        if (uploadFilePath.contains("content://")) {
            Uri uri = Uri.parse(uploadFilePath);
            uploadFilePath = FileUtils.getFilePathByUri_BELOWAPI11(uri, this);
        }
        flag=true;

        //config配置上传参数
        Configuration configuration = new Configuration.Builder()
                .connectTimeout(10)
                .responseTimeout(60).build();

        if (this.uploadManager == null) {
            //this.uploadManager = new UploadManager(fileRecorder);
            this.uploadManager = new UploadManager(configuration, 3);
        }

        UploadOptions opt = new UploadOptions(null, null, true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
            }
        }, null);

        keyname = UUID.randomUUID()+uploadFilePath.substring(uploadFilePath.lastIndexOf("."));
        pictures+=keyname+"##";
        File uploadFile = new File(uploadFilePath);
        long time = new Date().getTime();
        if (keyname.equals(""))
            keyname = "test_" + time;

        this.uploadManager.put(uploadFile, "images/"+keyname, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo,
                                         JSONObject jsonData) {
                        if (respInfo.isOK()) {
                            Log.e("msg", "上传成功");
                            Log.e("zw", jsonData.toString() + respInfo.toString());
                            uploadcount++;
                            if (uploadcount==mAdapter.getData().size())
                                sendwenzhang(UserUtil.user.getToken(),title.getText()+"",content.getText()+"",categoryName,pictures.substring(0,pictures.length()-2));

                        }else{
                            flag=false;
                            Log.e("Log",respInfo.toString());
                            if(jsonData!=null)
                                Log.e("Log",jsonData.toString());

                        }
                    }
                }, opt);
        return flag;
    }
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            PictureSelector.create(Activity_InsertNewArticle.this)
                    .openGallery(PictureMimeType.ofImage())
                    .isPreviewImage(true)
                    .selectionMedia(mAdapter.getData())// 是否传入已选图片
                    .maxSelectNum(9)// 最大图片选择数量
                    .isCompress(true)
                    .loadImageEngine(GlideEngine.createGlideEngine()) // Please refer to the Demo GlideEngine.java
                    .forResult(new OnResultCallbackListener() {
                        @Override
                        public void onResult(List result) {
                            mAdapter.setList(result);
                            mAdapter.notifyDataSetChanged();

                            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    List<LocalMedia> selectList = mAdapter.getData();
                                    if (selectList.size() > 0) {
                                        LocalMedia media = selectList.get(position);
                                        String pictureType = media.getMimeType();
                                        int mediaType = PictureMimeType.getMimeType(pictureType);
                                        switch (mediaType) {
                                            case 1:
                                                // 预览图片 可自定长按保存路径
                                                //PictureSelector.create(Activity_InsertNewArticle.this).externalPicturePreview(position, "/custom_file", selectList);
                                                PictureSelector.create(Activity_InsertNewArticle.this).externalPicturePreview(position, selectList,0);
                                                break;
                                        }
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancel() {

                        }
                    });
        }

    };
}
