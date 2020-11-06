package edu.zjff.shzj.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.FullScreenDialog;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.shinichi.library.ImagePreview;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.GlideEngine;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class activity_setting extends AppCompatActivity {
    @OnClick(R.id.back)
    public void back(){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        UserUtil.save(activity_setting.this);
        finish();
        super.onBackPressed();
    }

    @BindView(R.id.iv_tou)
    ImageView iv_tou;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.qianming)
    TextView qianming;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setView();
    }
    CustomDialog customDialog;
    String old;String newpwd;
    @OnClick(R.id.upd_bg)
    public void updbg(){
        PictureSelector.create(activity_setting.this)
                .openGallery(PictureMimeType.ofAll())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .selectionMode(PictureConfig.SINGLE)
                .enableCrop(true)
                .isAndroidQTransform(true)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        // onResult Callback
                        if (result.get(0).getPath()!=null){
                            System.out.println("AndroidQ路径：" + result.get(0).getAndroidQToPath());
                            System.out.println("路径：" + result.get(0).getPath());
                            //TODO 这里要判断是否是Android10
                            anyUtil.uploadImg(result.get(0).getPath(),"images/background/",getApplicationContext(), new anyUtil.mCallBack() {
                                @Override
                                public void success(String fileName) {
                                    UserUtil.user.setBackground(fileName);
                                    HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + "android/uploadBackground",
                                            new String[]{"token","background"},
                                            new String[]{UserUtil.user.getToken(),fileName}, new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                                    String s = response.body().string();
                                                    anyUtil.syso(s);
                                                    DataRoot dataRoot = new Gson().fromJson(s,DataRoot.class);
                                                    if ("200".equals(dataRoot.getCode()))
                                                        BToast.success(activity_setting.this).text(dataRoot.getMessage()).show();
                                                    else
                                                        BToast.info(activity_setting.this).text(dataRoot.getMessage()).show();

                                                }
                                            });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }
    @OnClick(R.id.upd_pwd)
    public void upd(){
        old="";newpwd="";
        customDialog=CustomDialog.build(activity_setting.this, R.layout.layout_full_updpwd, new CustomDialog.OnBindView() {
            @Override
            public void onBind(CustomDialog dialog, View v) {
                View layoutView2 = v;
                v.findViewById(R.id.ll_email).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutView2.findViewById(R.id.et_count).requestFocus();
                    }
                });
                v.findViewById(R.id.ll_pwd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutView2.findViewById(R.id.et_pwd).requestFocus();
                    }
                });

                v.findViewById(R.id.ll_name).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutView2.findViewById(R.id.et_name).requestFocus();
                    }
                });

                //dismiss 当前dialog
                v.findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!anyUtil.isFastDoubleClick(500)){
                            customDialog.doDismiss();
                        }
                    }
                });
                //dismiss 当前dialog
                v.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.doDismiss();
                    }
                });


                v.findViewById(R.id.tv_reg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TextView textView=layoutView2.findViewById(R.id.et_count);
                        TextView textView2=layoutView2.findViewById(R.id.et_pwd);
                        if (textView.getText()!=null&&textView2.getText()!=null&&((TextView) layoutView2.findViewById(R.id.et_name)).getText()!=null){

                            if ((textView.getText()+"").equals(textView2.getText()+"")){
                                newpwd=textView.getText()+"";
                                old=((TextView)layoutView2.findViewById(R.id.et_name)).getText()+"";
                                String[] par={"oldPassword","newPassword","token"};
                                String[] key={old,newpwd,UserUtil.user.getToken()};
                                HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + "android/updatePassword", par, key, new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            String s = response.body().string();
                                            anyUtil.syso(s);
                                        DataRoot dataRoot = new Gson().fromJson(s,DataRoot.class);
                                        if ("200".equals(dataRoot.getCode())){
                                            BToast.success(activity_setting.this).text(dataRoot.getMessage()).show();
                                            exit();
                                        }
                                        else
                                            BToast.info(activity_setting.this).text(dataRoot.getMessage()).show();
                                    }
                                });
                            }else
                                BToast.warning(activity_setting.this).text("两次密码不同").show();
                        }else{
                            BToast.warning(activity_setting.this).text("输入不能为空").show();
                        }

                    }
                });

            }
        }).setCancelable(true).setFullScreen(true).setAlign(CustomDialog.ALIGN.BOTTOM);
        customDialog.show();
    }
    @OnClick(R.id.iv_tou)
    public void selectPic(){
        BottomMenu.show(activity_setting.this, new String[]{"查看大图", "修改头像"}, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index)
                {
                    case 0:
                        //TODO 看大图
                        ImagePreview
                                .getInstance()
                                // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
                                .setContext(activity_setting.this)
                                // 设置从第几张开始看（索引从0开始）
                                .setImage(Constant.BASE+"images/avatar/"+UserUtil.user.getAvatar())
                                //=================================================================================================
                                // 有三种设置数据集合的方式，根据自己的需求进行三选一：
                                // 1：第一步生成的imageInfo List

                                // 2：直接传url List
                                //.setImageList(List<String> imageList)

                                // 3：只有一张图片的情况，可以直接传入这张图片的url
                                //.setImage(String image)
                                //=================================================================================================

                                // 开启预览
                                .start();

                        break;
                    case 1:
                        //TODO 改头像

                        PictureSelector.create(activity_setting.this)
                                .openGallery(PictureMimeType.ofAll())
                                .loadImageEngine(GlideEngine.createGlideEngine())
                                .selectionMode(PictureConfig.SINGLE)
                                .enableCrop(true)
                                .forResult(new OnResultCallbackListener<LocalMedia>() {
                                    @Override
                                    public void onResult(List<LocalMedia> result) {
                                        // onResult Callback
                                    if (result.get(0).getPath()!=null){

                                        anyUtil.uploadImg(result.get(0).getPath(),"images/avatar/",getApplicationContext(), new anyUtil.mCallBack() {
                                            @Override
                                            public void success(String fileName) {
                                                Glide.with(activity_setting.this).load(result.get(0).getPath())
                                                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                                                        .override(200, 200)
                                                        .into(iv_tou);

                                                UserUtil.user.setAvatar(fileName);

                                                HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + "android/uploadAvatar",
                                                        new String[]{"token","avatar"},
                                                        new String[]{UserUtil.user.getToken(),fileName}, new Callback() {
                                                    @Override
                                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                                    }

                                                    @Override
                                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                        String s = response.body().string();
                                                        anyUtil.syso(s);
                                                        DataRoot dataRoot = new Gson().fromJson(s,DataRoot.class);
                                                        if ("200".equals(dataRoot.getCode()))
                                                            BToast.success(activity_setting.this).text(dataRoot.getMessage()).show();
                                                        else
                                                            BToast.info(activity_setting.this).text(dataRoot.getMessage()).show();
                                                    }
                                                });
                                            }
                                        });

                                    }
                                    }

                                    @Override
                                    public void onCancel() {
                                        // onCancel Callback
                                    }
                                });
                        break;
                }
            }
        });
    }

    private void setView() {
        anyUtil.setNavigation(activity_setting.this);
        Glide.with(this).load(Constant.BASE+"images/avatar/"+UserUtil.user.getAvatar())
                .placeholder(R.mipmap.shzj_avatar_default)
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(50)))
                .override(200, 200) // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                .into(iv_tou);
        name.setText(UserUtil.user.getUsername());
        qianming.setText(UserUtil.user.getDescription()==null?"这个人很懒什么也没留下..":UserUtil.user.getDescription());

    }
    @OnClick(R.id.exit)
    public void exit(){
        UserUtil.exit(activity_setting.this);
        finish();
    }
    @OnClick(R.id.pan)
    public void qianmingxie(){
        InputDialog.show(activity_setting.this, "修改", "输入新的个性签名", "确定")
                .setOnOkButtonClickListener(new OnInputDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                        //inputStr 即当前输入的文本
                        HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + "android/updateDescription",
                                new String[]{"token","description"},
                                new String[]{UserUtil.user.getToken(),inputStr},
                                new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                String s= response.body().string();
                                                anyUtil.syso(s);
                                                UserUtil.user.setDescription(inputStr);
                                        DataRoot dataRoot = new Gson().fromJson(s,DataRoot.class);
                                        if ("200".equals(dataRoot.getCode())){
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    qianming.setText(inputStr+"");
                                                }
                                            });
                                            BToast.success(activity_setting.this).text(dataRoot.getMessage()).show();
                                        }
                                        else
                                            BToast.info(activity_setting.this).text(dataRoot.getMessage()).show();
                                    }
                                }
                        );
                        return false;
                    }
                });
    }
    @OnClick(R.id.banben)
     public void banben(){
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = this.getPackageManager ( ).getApplicationInfo ( getPackageName ( ) , PackageManager.GET_META_DATA );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Float msg = applicationInfo.metaData.getFloat("BUGLY_APP_VERSION");

        MessageDialog.show(activity_setting.this,"应用版本","v - "+msg);
    }
    @OnClick(R.id.help)
    public void help(){
        MessageDialog.show(activity_setting.this,"帮助反馈","本应用已接入Bugly自动上报Bug\n如情况紧急请带上时间手机型号联系作者");
    }
}
