package edu.zjff.shzj.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.kongzue.holderview.HolderView;
import com.shzj.behavior.Demo1Activity;
import com.shzj.behavior.demo1.behavior.AppBarLayoutOverScrollViewBehavior;
import com.shzj.behavior.demo1.widget.NoScrollViewPager;
import com.shzj.behavior.demo1.widget.RoundProgressBar;
import com.shzj.behavior.fragment.ItemFragment1;
import com.shzj.behavior.fragment.MyFragmentPagerAdapter;
import com.shzj.behavior.fragment.dummy.DummyContent;
import com.shzj.behavior.fragment.dummy.TabEntity;
import com.shzj.behavior.widget.CircleImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.entity.VideoMessage;
import edu.zjff.shzj.entity.VideoRoot;
import edu.zjff.shzj.entity.userMsgRoot;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class fragment_my extends Fragment {
    public interface OnBackPressLinsear{
        void onBackPress();
    }
    OnBackPressLinsear onBackPressLinsear;

    public OnBackPressLinsear getOnBackPressLinsear() {
        return onBackPressLinsear;
    }

    public void setOnBackPressLinsear(OnBackPressLinsear onBackPressLinsear) {
        this.onBackPressLinsear = onBackPressLinsear;
    }
    @OnClick(R.id.uc_setting_iv)
    public void setting(){
        startActivity(new Intent(getContext(),activity_setting.class));
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        anyUtil.syso("onCreateView(");
        View view=null;
        if(UserUtil.isLogin_notshowDialog(getActivity())){
            view = inflater.inflate(R.layout.frag_my,null);
            ButterKnife.bind(this,view);
            /* holderView.showEmpty();*/
            initdata();
            initListener();
            getFragmentsData();
            initStatus();
        }
        return view;
    }

    @Override
    public void onStart() {
        if (!UserUtil.isLogin_notshowDialog(getActivity()))
            exit();
        /*initdata();*/
        setView();
        super.onStart();
    }

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


    @OnClick(R.id.frag_uc_follow_tv)
    void exit() {
        UserUtil.exit(getActivity());
        onBackPressLinsear.onBackPress();
        startActivity(new Intent(getContext(), Activity_login_welcome.class));
        //TODO 去登陆/注册页面
    }

    userMsgRoot MsgRoot = new userMsgRoot();

    private void initdata() {

        MsgRoot.setUsername(getActivity().getIntent().getStringExtra(Constant.MESSAGE));
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
                    getActivity().runOnUiThread(new Runnable() {
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
        title_name.setText(MsgRoot.getUsername());
        name.setText(MsgRoot.getUsername());
        description.setText(MsgRoot.getDescription());
        Glide.with(this).load(Constant.BASE + "images/background/" + MsgRoot.getBackground())
                .into(mZoomIv);
        Glide.with(this).load(Constant.BASE + "images/avatar/" + MsgRoot.getAvatar())
                .into(mAvater);
        Glide.with(this).load(Constant.BASE + "images/avatar/" + MsgRoot.getAvatar())
                .into(title_avater);
    }

    private void setViewOnStatic() {
        title_name.setText(MsgRoot.getUsername());
        name.setText(MsgRoot.getUsername());
        description.setText(MsgRoot.getDescription());
        Glide.with(this).load(Constant.BASE + "images/background/" + MsgRoot.getBackground())
                .into(mZoomIv);
        Glide.with(this).load(Constant.BASE + "images/avatar/" + MsgRoot.getAvatar())
                .into(mAvater);
        Glide.with(this).load(Constant.BASE + "images/avatar/" + MsgRoot.getAvatar())
                .into(title_avater);
    }
    /**
     * 初始化tab
     */
    private void initTab() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fragments = getFragments();
                MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter( getActivity().getSupportFragmentManager(), fragments, getNames());
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
                    StatusBarUtil.setTranslucentForImageView(getActivity(), (int) (255f * percent), null);
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
                    if (anyUtil.isFastDoubleClick(10000)){

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initdata();
                            }
                        });
                    }
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
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = getStatusBarHeight(getContext());
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
                break;
            case 2://完全关闭 显示黑色
                mSettingIv.setImageResource(R.drawable.icon_setting_black);
                mViewPager.setNoScroll(false);
                anyUtil.setNavigation((AppCompatActivity) getActivity());
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
