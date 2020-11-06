package com.shzj.behavior;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.holderview.HolderView;
import com.shzj.behavior.demo1.behavior.AppBarLayoutOverScrollViewBehavior;
import com.shzj.behavior.demo1.widget.NoScrollViewPager;
import com.shzj.behavior.demo1.widget.RoundProgressBar;
import com.shzj.behavior.fragment.ItemFragment1;
import com.shzj.behavior.fragment.MyFragmentPagerAdapter;
import com.shzj.behavior.fragment.dummy.DummyContent;
import com.shzj.behavior.fragment.dummy.TabEntity;
import com.shzj.behavior.widget.CircleImageView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.shinichi.library.ImagePreview;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.entity.VideoMessage;
import edu.zjff.shzj.entity.VideoRoot;
import edu.zjff.shzj.entity.userMsgRoot;
import edu.zjff.shzj.ui.Activity_login_welcome;
import edu.zjff.shzj.ui.activity_setting;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Demo1Activity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.frag_uc_follow_tv)
    TextView frag_uc_follow_tv;
    @BindView(R.id.uc_zoomiv)
    ImageView mZoomIv;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.title_layout)
    ViewGroup titleContainer;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.title_center_layout)
    ViewGroup titleCenterLayout;
    @BindView(R.id.uc_progressbar)
    RoundProgressBar progressBar;
    @BindView(R.id.uc_setting_iv)
    ImageView mSettingIv;
    @BindView(R.id.uc_avater)
    CircleImageView mAvater;
    @BindView(R.id.uc_tablayout)
    CommonTabLayout mTablayout;
    @BindView(R.id.uc_viewpager)
    NoScrollViewPager mViewPager;
    @BindView(R.id.holdView)
    HolderView holderView;
    @BindView(R.id.frag_uc_nickname_tv)
    TextView name;
    @BindView(R.id.frag_uc_interest_tv)
    TextView description;
    @BindView(R.id.title_uc_title)
    TextView title_name;
    @BindView(R.id.title_uc_avater)
    ImageView title_avater;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private List<Fragment> fragments;
    private int lastState = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        ButterKnife.bind(this);
        /* holderView.showEmpty();*/

        initdata();
        initListener();
        getFragmentsData();
        initStatus();

    }

    @OnClick(R.id.frag_uc_follow_tv)
    void exit() {
        UserUtil.exit(this);
        startActivity(new Intent(Demo1Activity.this, Activity_login_welcome.class));
        finish();
        //TODO 去登陆/注册页面
    }

    userMsgRoot MsgRoot = new userMsgRoot();

    private void initdata() {
        MsgRoot.setUsername(getIntent().getStringExtra(Constant.MESSAGE));
        if (MsgRoot.getUsername() == null) {
            //我的信息
            MsgRoot.setUsername(UserUtil.user.getUsername());
            MsgRoot.setDescription(UserUtil.user.getDescription());
            MsgRoot.setAvatar(UserUtil.user.getAvatar());
            MsgRoot.setBackground(UserUtil.user.getBackground());
            setView();
        } else {
            //别人的信息
            HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + "android/userInfo", new String[]{"username"}, new String[]{MsgRoot.getUsername()}, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String s = response.body().string();
                    MsgRoot = new Gson().fromJson(s, userMsgRoot.class);
                    anyUtil.syso(MsgRoot.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setView();
                        }
                    });
                }
            });
        }
    }
    private void setView() {
        mSettingIv.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        frag_uc_follow_tv.setVisibility(View.GONE);
        title_name.setText(MsgRoot.getUsername());
        name.setText(MsgRoot.getUsername());
        description.setText(MsgRoot.getDescription());


        Glide.with(this).load(Constant.BASE + "images/background/" + MsgRoot.getBackground())
                .into(mZoomIv);


        Glide.with(this).load(Constant.BASE + "images/avatar/" + MsgRoot.getAvatar())
                .into(mAvater);
        mAvater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImagePreview
                        .getInstance()
                        // 上下文，必须是activity，不需要担心内存泄漏，本框架已经处理好；
                        .setContext(Demo1Activity.this)
                        // 设置从第几张开始看（索引从0开始）
                        .setImage(Constant.BASE + "images/avatar/" + MsgRoot.getAvatar())
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
            }
        });
        Glide.with(this).load(Constant.BASE + "images/avatar/" + MsgRoot.getAvatar())
                .into(title_avater);
    }

    /**
     * 初始化tab
     */
    private void initTab() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragments = getFragments();
                MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, getNames());
                mTablayout.setTabData(mTabEntities);
                mViewPager.setAdapter(myFragmentPagerAdapter);
            }
        });
    }

    ArrayList<Data> articelitems = new ArrayList<>();
    ArrayList<VideoMessage> videoitems = new ArrayList<>();

    private void getFragmentsData() {
        HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + VIDEO, new String[]{"username"}, new String[]{MsgRoot.getUsername()}, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        initTab();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String s = response.body().string();
                        VideoRoot videoRoot = new Gson().fromJson(s, VideoRoot.class);
                        anyUtil.syso(s);
                        if (videoRoot.getVideoMessages()!=null)
                            videoitems = (ArrayList<VideoMessage>) videoRoot.getVideoMessages();

                        HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + ARTICLE, new String[]{"username"}, new String[]{MsgRoot.getUsername()}, new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        initTab();
                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        String s = response.body().string();
                                        DataRoot dataRoot = new Gson().fromJson(s, DataRoot.class);
                                        anyUtil.syso(dataRoot.toString());
                                        if (dataRoot.getData() != null)
                                            articelitems=(ArrayList<Data>)dataRoot.getData();
                                        initTab();
                                    }
                                }
                        );
                    }
                }
        );

    }
    /**
     * 绑定事件
     */
    private void initListener() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float percent = Float.valueOf(Math.abs(verticalOffset)) / Float.valueOf(appBarLayout.getTotalScrollRange());
                if (titleCenterLayout != null && mAvater != null && mSettingIv != null) {
                    titleCenterLayout.setAlpha(percent);
                    StatusBarUtil.setTranslucentForImageView(Demo1Activity.this, (int) (255f * percent), null);
                    if (percent == 0) {
                        groupChange(1f, 1);
                    } else if (percent == 1) {
                        if (mAvater.getVisibility() != View.GONE) {
                            mAvater.setVisibility(View.GONE);
                        }
                        groupChange(1f, 2);
                    } else {
                        if (mAvater.getVisibility() != View.VISIBLE) {
                            mAvater.setVisibility(View.VISIBLE);
                        }
                        groupChange(percent, 0);
                    }

                }
            }
        });
        AppBarLayoutOverScrollViewBehavior myAppBarLayoutBehavoir = (AppBarLayoutOverScrollViewBehavior)
                ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
        myAppBarLayoutBehavoir.setOnProgressChangeListener(new AppBarLayoutOverScrollViewBehavior.onProgressChangeListener() {
            @Override
            public void onProgressChange(float progress, boolean isRelease) {
                progressBar.setProgress((int) (progress * 360));
                if (progress == 1 && !progressBar.isSpinning && isRelease) {
                    // 刷新viewpager里的fragment
                }
            }
        });
        mTablayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

                mViewPager.setCurrentItem(position);
                if (((ItemFragment1) fragments.get(mViewPager.getCurrentItem())).items.size() == 0)
                    holderView.showEmpty();
                else
                    holderView.showSuccess();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTablayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化状态栏位置
     */
    private void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
            //沉浸式状态栏
            StatusBarUtil.setTransparentForImageView(Demo1Activity.this, null);
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = getStatusBarHeight(Demo1Activity.this);
            CollapsingToolbarLayout.LayoutParams lp1 = (CollapsingToolbarLayout.LayoutParams) titleContainer.getLayoutParams();
            lp1.topMargin = statusBarHeight;
            titleContainer.setLayoutParams(lp1);
            CollapsingToolbarLayout.LayoutParams lp2 = (CollapsingToolbarLayout.LayoutParams) mToolBar.getLayoutParams();
            lp2.topMargin = statusBarHeight;
            mToolBar.setLayoutParams(lp2);
        }
    }

    /**
     * @param alpha
     * @param state 0-正在变化 1展开 2 关闭
     */
    public void groupChange(float alpha, int state) {
        lastState = state;

        mSettingIv.setAlpha(alpha);

        switch (state) {
            case 1://完全展开 显示白色
                mSettingIv.setImageResource(R.drawable.icon_setting);
                mViewPager.setNoScroll(false);
                back.setImageResource(R.drawable.drawable_back_white);
                break;
            case 2://完全关闭 显示黑色
                mSettingIv.setImageResource(R.drawable.icon_setting_black);
                mViewPager.setNoScroll(false);

                back.setImageResource(R.drawable.shape_back);
                break;
            case 0://介于两种临界值之间 显示黑色
                if (lastState != 0) {
                    mSettingIv.setImageResource(R.drawable.icon_setting_black);
                }
                //为什么禁止滑动？在介于开关状态之间，不允许滑动，开启可能会导致不好的体验
                mViewPager.setNoScroll(true);
                break;
        }
    }


    /**
     * 获取状态栏高度
     * ！！这个方法来自StatusBarUtil,因为作者将之设为private，所以直接copy出来
     *
     * @param context context
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
@OnClick(R.id.back)
    public void back(){
        finish();
}
    /**
     * 假数据
     *
     * @return
     */
    public String[] getNames() {
        String[] mNames = new String[]{"TA的游记", "TA的视频"};
        mTabEntities.add(new TabEntity(articelitems.size()+"", mNames[0]));
        mTabEntities.add(new TabEntity(videoitems.size()+"", mNames[1]));
        return mNames;
    }

    String ARTICLE = "android/queryArticleOfUser";
    String VIDEO = "android/queryVideoOfUser";

    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ItemFragment1(articelitems));
        fragments.add(new ItemFragment1(videoitems));
        return fragments;
    }
}
