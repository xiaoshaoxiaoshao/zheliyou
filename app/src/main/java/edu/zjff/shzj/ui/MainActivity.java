package edu.zjff.shzj.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.hhl.rebound.PopMenu;
import com.hhl.rebound.PopMenuItem;
import com.hhl.rebound.PopMenuItemListener;
import com.jaeger.library.StatusBarUtil;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.v3.MessageDialog;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;
import edu.zjff.shzj.R;
import edu.zjff.shzj.trinity.RecordActivity;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.PermissionUtils;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    ArrayList<Fragment> fragments = new ArrayList<>();
    PermissionUtils permissionUtils = new PermissionUtils(MainActivity.this);
    PopMenu popMenu;
    Fregment_index fregment_index=new Fregment_index();
    fragment_rank fragment_rank = new fragment_rank();
    fragment_my fragment_my;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashReport.initCrashReport(getApplicationContext());
        anyUtil.setStatusBarTransparent(this);

        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        initUtil();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        fragments.add(fregment_index);

        fragments.add(fragment_rank);
        //TODO 添加fregment
        //fragments.add();
        for (Fragment fragment:fragments)
            transaction.add(R.id.main_frag,fragment);
        transaction.commit();
        showIndex();
    }
    @OnClick(R.id.iv_rank)
    public void starimg(){
        setGone(1);
        show(fragments.get(1));
        /*
        MessageDialog.show(this,"提示","功能暂未开放");*/
        /*startActivity(new Intent(MainActivity.this,Activity_img.class));*/
    }

    private void initUtil() {

        ButterKnife.bind(this);
        DialogSettings.isUseBlur = (true);
        DialogSettings.style = (DialogSettings.STYLE.STYLE_MIUI);DialogSettings.autoShowInputKeyboard = (true);
        BToast.Config.getInstance()
            .apply(getApplication());// must call

        permissionUtils.request();

         popMenu = new PopMenu.Builder().attachToActivity(MainActivity.this)
                .addMenuItem(new PopMenuItem("发布游记", getResources().getDrawable(R.drawable.tabbar_compose_idea)))
                .addMenuItem(new PopMenuItem("发布短视频", getResources().getDrawable(R.drawable.tabbar_compose_photo)))
                .addMenuItem(new PopMenuItem("其他", getResources().getDrawable(R.drawable.tabbar_compose_more)))
                .setOnItemClickListener(new PopMenuItemListener() {
                    @Override
                    public void onItemClick(PopMenu popMenu, int position) {
                        if (UserUtil.isLogin(MainActivity.this))
                        switch (position)
                        {
                            case 0:
                                startActivity(new Intent(MainActivity.this,Activity_InsertNewArticle.class));
                                break;
                            case 1:
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, RecordActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                BToast.info(MainActivity.this).text("暂未开放").show();
                                break;
                        }
                    }
                })
                .build();

        ImmersionBar.with(this)
                //.statusBarColor(R.color.transparent)
                .statusBarDarkFont(true)
                .fitsSystemWindows(false)
                .keyboardEnable(true)
                // .navigationBarColor(R.color.virtual_buttons)
                .init();
        //加载短视频sdk的依赖库
        System.loadLibrary("trinity");
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionUtils.PERMISSION) {
            if (EasyPermissions.hasPermissions(MainActivity.this, permissionUtils.PERMS)) {
                BToast.success(MainActivity.this)
                        .text("权限请求成功")
                        .show();
            } else { BToast.warning(MainActivity.this)
                    .text("权限请求失敗")
                    .show();
            }
        }
    }
    @BindView(R.id.index)
    ImageView index;
    @BindView(R.id.rank)
    ImageView rank;
    @BindView(R.id.my)
    ImageView my;
    @BindView(R.id.tv_index)
    TextView tv_index;
    @BindView(R.id.tv_rank)
    TextView tv_rank;
    @BindView(R.id.tv_my)
    TextView tv_my;
    @OnClick(R.id.iv_index)

    public void showIndex() {
        show(fragments.get(0));
        setGone(0);
    }
   public void setGone(int i){
        switch (i){
            case 0:
                Glide.with(this).load(R.drawable.ic_home_flash).into(index);
                tv_index.setTextColor(Color.rgb(23,110,67));
                Glide.with(this).load(R.drawable.ic_rank).into(rank);
                tv_rank.setTextColor(Color.rgb(191,191,191));
                Glide.with(this).load(R.drawable.ic_my).into(my);
                tv_my.setTextColor(Color.rgb(191,191,191));
                break;
            case 1:
                Glide.with(this).load(R.drawable.ic_home).into(index);
                tv_index.setTextColor(Color.rgb(191,191,191));
                Glide.with(this).load(R.drawable.ic_rank_flash).into(rank);
                tv_rank.setTextColor(Color.rgb(23,110,67));
                Glide.with(this).load(R.drawable.ic_my).into(my);
                tv_my.setTextColor(Color.rgb(191,191,191));
                break;
            case 2:
                Glide.with(this).load(R.drawable.ic_home).into(index);
                tv_index.setTextColor(Color.rgb(191,191,191));
                Glide.with(this).load(R.drawable.ic_rank).into(rank);
                tv_rank.setTextColor(Color.rgb(191,191,191));
                Glide.with(this).load(R.drawable.ic_my_flash).into(my);
                tv_my.setTextColor(Color.rgb(23,110,67));
                break;
        }
    }
    @OnClick(R.id.iv_camera)
        public void showpop(){
        popMenu.show();
    }
    @OnClick(R.id.iv_video)
    public void showvideo() {

        startActivity(new Intent(MainActivity.this,Activity_video.class).putExtra("VideoId",1));
        /*MessageDialog.show(this,"提示","施工中...");*/
    }
    public void show(Fragment fragment) {//快问我为什么不用viewPager淦
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment frag : fragments)
            transaction.hide(frag);
        transaction.show(fragment);
        transaction.commit();
    }

    @OnClick(R.id.iv_my)
    public void toMyself(){
//        MessageDialog.show(this,"提示","功能暂未开放");/*
       if(UserUtil.isLogin_notshowDialog(this))
       {
           setGone(2);
           if (fragments.size()<3){
               FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
               fragment_my=new fragment_my();
               fragment_my.setOnBackPressLinsear(new fragment_my.OnBackPressLinsear() {

                   @Override
                   public void onBackPress() {
                       fragments.remove(fragment_my);
                       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                       transaction.remove(fragment_my);
                       transaction.commit();
                       showIndex();
                   }
               });
               fragments.add(fragment_my);
               transaction.add(R.id.main_frag,fragment_my);
               transaction.commit();
           }
           show(fragments.get(2));
       }else{
           Intent intent = new Intent();
           intent.setClass(MainActivity.this, Activity_login_welcome.class);
           startActivityForResult(intent,Constant.REQUEST_USER);
       }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_USER:
                    BToast.success(MainActivity.this).text("欢迎回来~").show();
                    break;
                default:
                    break;
            }
        }
    }

}