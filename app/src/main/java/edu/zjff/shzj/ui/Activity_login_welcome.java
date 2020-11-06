package edu.zjff.shzj.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bravin.btoast.BToast;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.FullScreenDialog;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mars.xlog.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.service.LoginService;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.LogUtil;
import edu.zjff.shzj.util.MyReceiver;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Activity_login_welcome extends AppCompatActivity {

    CustomDialog login,reg;
    Intent service;Intent service1;int where=-1;
    MyReceiver receiver;
    private IntentFilter intentFilter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_welcome);
        ButterKnife.bind(this);
        anyUtil.setNavigation(this);
        registerReceiver();
    }

    private void registerReceiver() {
        intentFilter=new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_LOGIN_WRONG);
        intentFilter.addAction(Constant.BROADCAST_LOGIN_SUCCESS);
        /*
        intentFilter.addAction(Constant.BROADCAST_YZM_WRONG);
        intentFilter.addAction(Constant.BROADCAST_YZM_SUCCESS);*/
        receiver=new MyReceiver(Activity_login_welcome.this);
        registerReceiver(receiver,intentFilter);
    }
    @OnClick(R.id.imageView3)
    public void back(){
        onBackPressed();
    }
    private void startService(User user){
        service=new Intent(this, LoginService.class);
        service.putExtra(Constant.USER,user);
        startService(service);
    }
    @Override
    protected void onDestroy(){
        //窗口销毁时,启动的service需要停止
        if(service!=null){
            stopService(service);
        }if(service1!=null){
            stopService(service1);
        }
        unregisterReceiver(receiver);//动态注册的广播接收器需取消
        super.onDestroy();
    }


    @OnClick(R.id.button2)
    public void tomyself(){
        login= CustomDialog.build(Activity_login_welcome.this,R.layout.item_login,new CustomDialog.OnBindView() {
                @Override
                public void onBind(CustomDialog dialog, View v) {
                    View layoutView=v;
                    //隐藏当前dialog
                    v.findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!anyUtil.isFastDoubleClick(500)){
                                login.doDismiss();
                            }
                        }
                    });
                    v.findViewById(R.id.submit_log).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            User user = new User();
                            user.setUsername(((TextView)layoutView.findViewById(R.id.et_count)).getText()+"");
                            user.setPassword(((TextView)layoutView.findViewById(R.id.et_pwd)).getText()+"");
                            startService(user);
                        }
                    });
                    v.findViewById(R.id.ll_email).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutView.findViewById(R.id.et_count).requestFocus();
                        }
                    });
                    v.findViewById(R.id.ll_pwd).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutView.findViewById(R.id.et_pwd).requestFocus();
                        }
                    });


                    //去另一个dialog
                    v.findViewById(R.id.tv_toreg).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          reg=  CustomDialog.build(Activity_login_welcome.this, R.layout.item_reg, new CustomDialog.OnBindView() {
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
                                                reg.doDismiss();
                                            }
                                        }
                                    });
                                    //dismiss 当前dialog
                                    v.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            reg.doDismiss();
                                        }
                                    });


                                    v.findViewById(R.id.tv_reg).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            User user1 = new User();
                                            user1.setUsername(((TextView)layoutView2.findViewById(R.id.et_name)).getText()+"");

                                            user1.setPassword(((TextView)layoutView2.findViewById(R.id.et_pwd)).getText()+"");

                                            user1.setEmail(((TextView)layoutView2.findViewById(R.id.et_count)).getText()+"");


                                            anyUtil.syso(user1.toString());
                                            HttpUtil.sendRequestToRegist(Constant.REGIST_URL, user1, new Callback() {
                                                @Override
                                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                                    onBackPressed();
                                                    BToast.success(Activity_login_welcome.this).text("服务器炸了  待会再来吧");
                                                }

                                                @Override
                                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                                    String str = response.body().string();
                                                    anyUtil.syso(str);
                                                    if (str!=null)
                                                    if (str.contains("200")){
                                                        BToast.success(Activity_login_welcome.this).text("注册成功~").show();
                                                        reg.doDismiss();
                                                    }else if(str.contains("-92")){
                                                        BToast.warning(Activity_login_welcome.this).text("用户名已被占用~").show();
                                                    }else if(str.contains("-94")){
                                                        BToast.warning(Activity_login_welcome.this).text("邮箱已被占用~").show();
                                                    }else if(str.contains("-95")){
                                                        BToast.warning(Activity_login_welcome.this).text("无效的邮箱格式~").show();
                                                    }else{
                                                        BToast.warning(Activity_login_welcome.this).text("发生了意料之外的错误").show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            }).setCancelable(true).setFullScreen(true).setAlign(CustomDialog.ALIGN.BOTTOM);
                          reg.show();
                        }
                    });

                }
            }).setCancelable(true).setFullScreen(true).setAlign(CustomDialog.ALIGN.BOTTOM);
        login.show();

    }

}
