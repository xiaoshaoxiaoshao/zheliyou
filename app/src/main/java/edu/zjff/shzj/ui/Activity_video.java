package edu.zjff.shzj.ui;


import android.annotation.SuppressLint;

import android.app.Activity;

import android.content.ClipData;

import android.content.ClipboardManager;

import android.content.Context;

import android.content.Intent;

import android.content.res.Resources;

import android.os.Build;

import android.os.Bundle;

import android.os.CountDownTimer;

import android.util.DisplayMetrics;

import android.util.Log;

import android.view.View;

import android.view.ViewGroup;

import android.view.WindowInsets;

import android.widget.ImageView;

import android.widget.RelativeLayout;

import android.widget.TextView;

import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;

import androidx.core.view.ViewCompat;

import androidx.recyclerview.widget.DefaultItemAnimator;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.bravin.btoast.BToast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView;

import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;

import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;

import com.dueeeke.videoplayer.player.VideoView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.gson.Gson;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;

import com.google.gson.JsonParser;

import com.kongzue.dialog.interfaces.OnBackClickListener;

import com.kongzue.dialog.interfaces.OnMenuItemClickListener;

import com.kongzue.dialog.v3.BottomMenu;

import com.kongzue.dialog.v3.ShareDialog;

import com.kongzue.dialog.v3.WaitDialog;
import com.shzj.behavior.Demo1Activity;


import org.jetbrains.annotations.NotNull;


import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

import java.util.Random;

import java.util.concurrent.Executors;

import java.util.concurrent.ThreadPoolExecutor;


import butterknife.BindView;

import butterknife.ButterKnife;

import butterknife.OnClick;

import edu.zjff.shzj.R;

import edu.zjff.shzj.dialog.InputTextMsgDialog;

import edu.zjff.shzj.entity.Child;

import edu.zjff.shzj.entity.Comment;

import edu.zjff.shzj.entity.CommentRoot;

import edu.zjff.shzj.entity.Content;

import edu.zjff.shzj.entity.FirstLevelBean;

import edu.zjff.shzj.entity.SecondLevelBean;

import edu.zjff.shzj.entity.User;

import edu.zjff.shzj.entity.VideoMessage;

import edu.zjff.shzj.entity.VideoRoot;

import edu.zjff.shzj.util.CommentDialog;

import edu.zjff.shzj.util.CommentDialogSingleAdapter;

import edu.zjff.shzj.util.Constant;

import edu.zjff.shzj.util.HttpUtil;

import edu.zjff.shzj.util.OnVideoControllerListener;

import edu.zjff.shzj.util.RecyclerViewUtil;

import edu.zjff.shzj.util.RxBus;

import edu.zjff.shzj.util.UserUtil;

import edu.zjff.shzj.util.VideoAdapter;

import edu.zjff.shzj.util.VideoUtils;

import edu.zjff.shzj.util.anyUtil;

import edu.zjff.shzj.util.cache.PreloadManager;

import edu.zjff.shzj.view.ControllerView;

import edu.zjff.shzj.view.LikeView;

import edu.zjff.shzj.view.viewpagerlayoutmanager.OnViewPagerListener;

import edu.zjff.shzj.view.viewpagerlayoutmanager.ViewPagerLayoutManager;

import edu.zjff.shzj.widget.VerticalCommentLayout;

import edu.zjff.shzj.widget.render.TikTokRenderViewFactory;

import okhttp3.Call;

import okhttp3.Callback;

import okhttp3.Response;

import q.rorbin.qrefreshlayout.QRefreshLayout;

import rx.functions.Action1;


public class Activity_video extends AppCompatActivity implements VerticalCommentLayout.CommentItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {


    @BindView(R.id.recyclerview)

    RecyclerView recyclerView;

    private VideoAdapter adapter;

    private ViewPagerLayoutManager viewPagerLayoutManager;

    /**
     * 当前播放视频位置
     */

    public static int curPlayPos = 0;

    private int page = 0;

    boolean comeformMy=false;

    private boolean dataChanged = false;

    private VideoView mVideoView;

    @BindView(R.id.refreshlayout)

    SwipeRefreshLayout refreshLayout;

    private ImageView ivCurCover;

    ArrayList<VideoMessage>
    list ;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video);

        ButterKnife.bind(this);

        list=getIntent().getParcelableArrayListExtra("VideoList");


        init();


    }


    protected void setStatusBarTransparent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            View decorView = getWindow().getDecorView();

            decorView.setOnApplyWindowInsetsListener((v, insets) -> {

                WindowInsets defaultInsets = v.onApplyWindowInsets(insets);

                return defaultInsets.replaceSystemWindowInsets(

                        defaultInsets.getSystemWindowInsetLeft(),

                        0,

                        defaultInsets.getSystemWindowInsetRight(),

                        defaultInsets.getSystemWindowInsetBottom());

            });

            ViewCompat.requestApplyInsets(decorView);

            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));

        }

    }


    protected void init() {
        if (list==null)
            getList();

        else
            comeformMy=true;

        adapter = new VideoAdapter(Activity_video.this,list);

        recyclerView.setAdapter(adapter);

        anyUtil.setNavigation(Activity_video.this);

        mVideoView = new VideoView(this);

        mVideoView.setLooping(false);

        mVideoView.setRenderViewFactory(TikTokRenderViewFactory.create());

        mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());

        setViewPagerLayoutManager();

        setStatusBarTransparent();

        setRefreshEvent();

        //监听播放或暂停事件

        RxBus.getDefault().toObservable(PauseVideoEvent.class)

                .subscribe((Action1<PauseVideoEvent>) event -> {

                    if (event.isPlayOrPause()) {

                        mVideoView.start();

                    } else {

                        mVideoView.pause();

                    }

                });

    }


    @Override

    public void onPause() {

        super.onPause();

        mVideoView.pause();

    }


    @Override

    public void onStop() {

        super.onStop();

        mVideoView.release();

    }


    private void setViewPagerLayoutManager() {

        viewPagerLayoutManager = new ViewPagerLayoutManager(Activity_video.this);

        recyclerView.setLayoutManager(viewPagerLayoutManager);

        recyclerView.scrollToPosition(curPlayPos);


        viewPagerLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override

            public void onInitComplete() {

                playCurVideo(curPlayPos);

            }


            @Override

            public void onPageRelease(boolean isNext, int position) {

                if (ivCurCover != null) {

                    ivCurCover.setVisibility(View.VISIBLE);

                }

                mVideoView.release();

            }


            @Override

            public void onPageSelected(int position, boolean isBottom) {
                playCurVideo(position);
                if (position%10==9&&!comeformMy){
                    page++;
                    getList();
                }
            }

        });

    }


    private void setRefreshEvent() {

        refreshLayout.setColorSchemeResources(R.color.color_link);

        refreshLayout.setOnRefreshListener(() -> new CountDownTimer(1000, 1000) {

            @Override

            public void onTick(long millisUntilFinished) {


            }


            @Override

            public void onFinish() {

                refreshLayout.setRefreshing(false);

            }

        }.start());

    }


    ControllerView controllerView = null;


    @SuppressLint("ClickableViewAccessibility")

    private void playCurVideo(int position) {

        System.out.println("===init===playCurVideo");



        View itemView = viewPagerLayoutManager.findViewByPosition(position);

        if (itemView == null) {
            anyUtil.syso("itemView is null");
            return;

        }

        ViewGroup rootView = itemView.findViewById(R.id.rl_container);

        LikeView likeView = rootView.findViewById(R.id.likeview);

        controllerView = rootView.findViewById(R.id.controller);

        ImageView ivPlay = rootView.findViewById(R.id.iv_play);

        ImageView ivCover = rootView.findViewById(R.id.iv_cover);

        ivPlay.setAlpha(0.4f);


        //播放暂停事件

        likeView.setOnPlayPauseListener(() -> {

            if (mVideoView.isPlaying()) {

                mVideoView.pause();

                ivPlay.setVisibility(View.VISIBLE);

            } else {

                mVideoView.start();

                ivPlay.setVisibility(View.GONE);

            }

        });


        adapter.setHeartEvent(new VideoAdapter.HeartEvent() {

            @Override

            public void doubleClick() {

                //controllerView.like(true);


            }


            @Override

            public void singleClick() {

                if (mVideoView.isPlaying()) {

                    mVideoView.pause();

                    ivPlay.setVisibility(View.VISIBLE);

                } else {

                    mVideoView.start();

                    ivPlay.setVisibility(View.GONE);

                }

            }

        });


        //切换播放视频的作者主页数据

        //RxBus.getDefault().post(new CurUserBean(DataCreate.datas.get(position).getUser()));

        curPlayPos = position;


        initComment();

        bottomSheetDialog = null;

        //切换播放器位置

        dettachParentView(rootView);


        autoPlayVideo(position, ivCover);


        //注册所有事件

        likeShareEvent(controllerView,ivPlay);


    }


    /**
     * 移除videoview父view
     */

    private void dettachParentView(ViewGroup rootView) {

        //1.添加videoview到当前需要播放的item中,添加进item之前，保证ijkVideoView没有父view

        ViewGroup parent = (ViewGroup) mVideoView.getParent();

        if (parent != null) {

            parent.removeView(mVideoView);

        }

        rootView.addView(mVideoView, 0);

    }


    /**
     * 自动播放视频
     */

    private void autoPlayVideo(int position, ImageView ivCover) {


        System.out.println("开始播放视频");

        System.out.println("ind=" + position);

        VideoMessage item = list.get(position);

        //String bgVideoPath = "android.resource://" + getActivity().getPackageName() + "/" + DataCreate.datas.get(position).getVideoRes();

        VideoUtils.removeViewFormParent(mVideoView);

        String playUrl = PreloadManager.getInstance(this).getPlayUrl(Constant.BASE + "videos/" + item.getUrl());

        mVideoView.setUrl(playUrl);

        mVideoView.start();


        //延迟取消封面，避免加载视频黑屏

        new CountDownTimer(200, 200) {

            @Override

            public void onTick(long millisUntilFinished) {

            }


            @Override

            public void onFinish() {

                ivCover.setVisibility(View.GONE);

                ivCurCover = ivCover;

            }

        }.start();

    }
    void getList(){
        HttpUtil.SendPostRequestAddSrings(Constant.VIDEOLIST
                , new String[]{"page", UserUtil.user.getToken() == null ? "" : "token"}
                , new String[]{page + "", UserUtil.user.getToken() == null ? "" : UserUtil.user.getToken()}
                , new Callback() {
                    @Override

                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        String s = response.body().string();

                        VideoRoot videoRoot = new Gson().fromJson(s, VideoRoot.class);
                        anyUtil.syso("asdasd");
                        if ("500".equals(videoRoot.getCode()))
                            comeformMy=true;//业务不对   逻辑对的    comeformMy禁用刷新
                        if (list==null)
                            list=new ArrayList<>();
                        list.addAll(videoRoot.getVideoMessages());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                adapter.notifyDataSetChanged();
                            }
                        });
                    }


                    @Override

                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        e.printStackTrace();

                        call.cancel();

                    }

                });
    }

    /**
     * 用户操作事件
     */

    private void likeShareEvent(ControllerView controllerView,ImageView view) {

        controllerView.setListener(new OnVideoControllerListener() {

            @Override

            public void onHeadClick(String CreatId) {

                //TODO 去头像

                mVideoView.release();

                startActivity(new Intent(Activity_video.this, Demo1Activity.class).putExtra(Constant.MESSAGE,CreatId));

            }


            @Override

            public void onLikeClick() {

                controllerView.like(false);

            }


            @Override

            public void onCommentClick() {

                if (mVideoView.isPlaying())
                {
                    mVideoView.pause();
                    view.setVisibility(View.VISIBLE);
                }


                show();

            }


            @Override

            public void onShareClick() {

                mVideoView.release();

                /*new ShareDialog().show(getChildFragmentManager(), "");*/

                ShareDialog.build(Activity_video.this).show();

            }

        });

    }


    public class PauseVideoEvent {

        private boolean playOrPause;


        public PauseVideoEvent(boolean playOrPause) {

            this.playOrPause = playOrPause;

        }


        public boolean isPlayOrPause() {

            return playOrPause;

        }

    }


    public class MainPageChangeEvent {

        private int page;


        public MainPageChangeEvent(int page) {

            this.page = page;

        }


        public int getPage() {

            return page;

        }


        public void setPage(int page) {

            this.page = page;

        }

    }


    private void startService() {

        HttpUtil.SendPostRequestAddSrings(Constant.VIDEOLIST, new String[]{"pager", UserUtil.user.getToken() == null ? "" : "token"}, new String[]{page + "", UserUtil.user.getToken() == null ? "" : UserUtil.user.getToken()}, new Callback() {

            @Override

            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                e.printStackTrace();

                call.cancel();

            }


            @Override

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                String s = response.body().string();

                Log.e("videoroot", s);

                VideoRoot videoRoot = new Gson().fromJson(s, VideoRoot.class);

                list= (ArrayList<VideoMessage>) videoRoot.getVideoMessages();

                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        adapter.notifyDataSetChanged();

                    }

                });

            }

        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case Constant.REQUEST_USER:

                    UserUtil.user = data.getParcelableExtra(Constant.MESSAGE);

                    BToast.success(Activity_video.this).text("欢迎回来~").show();

                    startService();

                    break;

                default:

                    break;

            }

        }

    }


    private List<FirstLevelBean> data = new ArrayList<>();

    private List<FirstLevelBean> Total = new ArrayList<>();

    private BottomSheetDialog bottomSheetDialog;

    private InputTextMsgDialog inputTextMsgDialog;

    private float slideOffset = 0;

    int totalCount = 30;

    String Current_click_name = "";

    private CommentDialogSingleAdapter bottomSheetAdapter;

    private RecyclerView rv_dialog_lists;

    private int offsetY;

    Boolean isNull = false;

    private RecyclerViewUtil mRecyclerViewUtil;

    private Context context = Activity_video.this;

    public String ID;


    @Override

    public void onLoadMoreRequested() {

        if (data.size() >= totalCount || data.size() >= Total.size()) {

            bottomSheetAdapter.loadMoreEnd(false);

            return;

        }

        for (int i = data.size(); i < 5 + i && i < Total.size(); i++) {

            data.add(Total.get(i));

        }

        sort();

        bottomSheetAdapter.loadMoreComplete();

    }


    @Override

    public void onMoreClick(View layout, int position) {

        return;

    }


    @Override

    public void onItemClick(View view, SecondLevelBean bean, int position) {

        if (UserUtil.isLogin((AppCompatActivity) context))

            initInputTextMsgDialog(view, true, bean.getHeadImg(), position);

    }


    @Override

    public void onItemLongClick(View view, SecondLevelBean bean, int position) {

        LongClickDialog(view);

    }


    public void LongClickDialog(View view) {

        BottomMenu.show((AppCompatActivity) context, new String[]{"复制文字", "删除"}, new OnMenuItemClickListener() {

            @Override

            public void onClick(String text, int index) {

                switch (index) {

                    case 0:

                        //获取剪贴板管理器：

                        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                        // 创建普通字符型

                        ClipData mClipData = ClipData.newPlainText("Label", ((TextView) view.findViewById(R.id.tv_content)).getText() + "");

                        // 将ClipData内容放到系统剪贴板里。

                        cm.setPrimaryClip(mClipData);

                        break;

                    case 1:/*

                        Log.e("11",((TextView)view.findViewById(R.id.commId)).getText()+"");

                        Log.e("11","commId"+view.toString());*/

                        if (UserUtil.isLogin((Activity) context)) {

                            WaitDialog.show((AppCompatActivity) context, "请稍候...").setOnBackClickListener(

                                    new OnBackClickListener() {

                                        @Override

                                        public boolean onBackClick() {

                                            WaitDialog.dismiss();

                                            return true;

                                        }

                                    }

                            );

                            String comm = ((TextView) view.findViewById(R.id.commId)).getText() + "";

                            Log.e("a", "a" + comm);

                            HttpUtil.SendPostRequestAddSrings(Constant.VIDEO_DELETECOMMENT, new String[]{"commentId", "token"}, new String[]{comm, UserUtil.user.getToken()}, new Callback() {

                                @Override

                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                    WaitDialog.dismiss();

                                }


                                @Override

                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                    String s = response.body().string();

                                    Log.e("s:", "s:" + s);

                                    int index = 0;

                                    int in = 0;

                                    boolean isSecondLevelBean = false;

                                    for (FirstLevelBean fr : data) {

                                        if (fr.getId().equals(comm))

                                            break;

                                        else if (fr.getSecondLevelBeans() != null) {

                                            in = 0;

                                            List<SecondLevelBean> list = fr.getSecondLevelBeans();

                                            for (SecondLevelBean se : list) {

                                                if (se.getId().equals(comm)) {

                                                    break;

                                                }

                                                in++;

                                            }

                                            if (list.size() != in) {

                                                isSecondLevelBean = true;

                                                break;

                                            }

                                        }

                                        index++;

                                    }

                                    if (isSecondLevelBean)

                                        data.get(index).getSecondLevelBeans().remove(in);

                                    else

                                        data.remove(index);

                                    sort();

                                    WaitDialog.dismiss();

                                    controllerView.settvComment(data.size() + "");

                                }

                            });

                        }

                        break;

                }

            }

        });

    }


    private void sort() {

        int size = data.size();

        for (int i = 0; i < size; i++) {

            FirstLevelBean firstLevelBean = data.get(i);

            firstLevelBean.setPosition(i);

            List<SecondLevelBean> secondLevelBeans = firstLevelBean.getSecondLevelBeans();

            if (secondLevelBeans == null || secondLevelBeans.isEmpty()) continue;

            int count = secondLevelBeans.size();

            for (int j = 0; j < count; j++) {

                SecondLevelBean secondLevelBean = secondLevelBeans.get(j);

                secondLevelBean.setPosition(i);

                secondLevelBean.setChildPosition(j);

            }

        }

        if (size == 0)

            runOnUiThread(new Runnable() {

                @Override

                public void run() {

                    dialog_bottomsheet_tv_nocomment.setVisibility(View.VISIBLE);

                }

            });

        runOnUiThread(new Runnable() {

            @Override

            public void run() {

                bottomSheetAdapter.notifyDataSetChanged();

            }

        });

    }


    View dialog_bottomsheet_tv_nocomment = null;


    private void showSheetDialog() {

        if (bottomSheetDialog != null) {

            bottomSheetDialog.show();

            return;

        }


        View view = View.inflate(context, R.layout.dialog_bottomsheet, null);

        ImageView iv_dialog_close = (ImageView) view.findViewById(R.id.dialog_bottomsheet_iv_close);

        rv_dialog_lists = (RecyclerView) view.findViewById(R.id.dialog_bottomsheet_rv_lists);

        RelativeLayout rl_comment = view.findViewById(R.id.rl_comment);

        dialog_bottomsheet_tv_nocomment = view.findViewById(R.id.dialog_bottomsheet_tv_nocomment);

        if (isNull)

            dialog_bottomsheet_tv_nocomment.setVisibility(View.VISIBLE);


        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());


        rl_comment.setOnClickListener(v -> {

            if (UserUtil.isLogin((Activity) context)) {

                //showSheetDialog();

                initInputTextMsgDialog(null, false, UserUtil.user.getAvatar(), -1);

            }

        });


        bottomSheetAdapter = new CommentDialogSingleAdapter(this);

        bottomSheetAdapter.setNewData(data);

        rv_dialog_lists.setHasFixedSize(true);

        rv_dialog_lists.setLayoutManager(new LinearLayoutManager(context));

        rv_dialog_lists.setItemAnimator(new DefaultItemAnimator());

        bottomSheetAdapter.setLoadMoreView(new SimpleLoadMoreView());

        bottomSheetAdapter.setOnLoadMoreListener(this, rv_dialog_lists);

        rv_dialog_lists.setAdapter(bottomSheetAdapter);


        initListener();


        bottomSheetDialog = new BottomSheetDialog(context, R.style.dialog);

        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.setCanceledOnTouchOutside(true);

        final BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());

        mDialogBehavior.setPeekHeight(getWindowHeight());

        mDialogBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override

            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

//                    bottomSheetDialog.dismiss();

                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {

                    if (slideOffset <= -0.28) {

                        bottomSheetDialog.dismiss();

                    }

                }

            }


            @Override

            public void onSlide(@NonNull View bottomSheet, float slideOff) {

                slideOffset = slideOff;

            }

        });

        if (!bottomSheetDialog.isShowing())

            bottomSheetDialog.show();

    }


    private int getWindowHeight() {

        Resources res = context.getResources();

        DisplayMetrics displayMetrics = res.getDisplayMetrics();

        return displayMetrics.heightPixels;

    }


    private void initListener() {

        bottomSheetAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {

            @Override

            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

                LongClickDialog(view);

                return true;

            }

        });

        bottomSheetAdapter.setOnItemChildClickListener((adapter, view1, position) -> {

            FirstLevelBean firstLevelBean = bottomSheetAdapter.getData().get(position);

            if (firstLevelBean == null) return;

            if (view1.getId() == R.id.rl_group) {

                //TODO （到此为止一级评论被点击）     添加二级评论

                if (UserUtil.isLogin((AppCompatActivity) context)) {

                    /*showSheetDialog();*/

                    initInputTextMsgDialog(view1, false, UserUtil.user.getAvatar(), position);

                }

            }

        });

        //滚动事件

        if (mRecyclerViewUtil != null) mRecyclerViewUtil.initScrollListener(rv_dialog_lists);

    }


    private void dismissInputDialog() {

        if (inputTextMsgDialog != null) {

            if (inputTextMsgDialog.isShowing()) {

                inputTextMsgDialog.dismiss();

                if (!mVideoView.isPlaying())

                    mVideoView.start();

            }

            inputTextMsgDialog.cancel();

            inputTextMsgDialog = null;

        }

    }


    // item滑动到原位

        public void scrollLocation(int offsetY) {

        try {

            rv_dialog_lists.smoothScrollBy(0, offsetY);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    public void show() {

        slideOffset = 0;

        showSheetDialog();

    }


    private void initInputTextMsgDialog(View view, final boolean isReply, final String headImg, final int position) {

        dismissInputDialog();

        if (view != null) {

            offsetY = view.getTop();

            scrollLocation(offsetY);

            scrollLocation(offsetY);

            Current_click_name = ((TextView) view.findViewById(R.id.tv_user_name)).getText().toString();

        }

        if (inputTextMsgDialog == null) {

            inputTextMsgDialog = new InputTextMsgDialog(context, R.style.dialog_center);

            inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {

                @Override

                public void onTextSend(String msg) {//isReplay 是代表当前消息是否是回复某人的    点击二级评论中的信息为回复


                    WaitDialog.show((AppCompatActivity) context, "请稍候...").setOnBackClickListener(

                            new OnBackClickListener() {

                                @Override

                                public boolean onBackClick() {

                                    WaitDialog.dismiss();

                                    return true;

                                }

                            }

                    );

                    if (UserUtil.isLogin((Activity) context)) {

                        String[] parName = {"content", "videoId", "parentId", "token"};
                        String[] par = {msg, list.get(curPlayPos).getVideoId() + "", position == -1 ? "0" : data.get(position).getId(), UserUtil.user.getToken()};

                        HttpUtil.SendPostRequestAddSrings(Constant.VIDEO_INSERTNewComment, parName, par, new Callback() {

                            @Override

                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                ((AppCompatActivity) context).runOnUiThread(new Runnable() {

                                    @Override

                                    public void run() {

                                        WaitDialog.dismiss();

                                    }

                                });

                            }


                            @Override

                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                String s = response.body().string();

                                Log.e("s", "s:" + s);

                                if (s.contains("-110"))

                                    BToast.success(context)

                                            .text("評論失敗")

                                            .show();

                                else {

                                    JsonParser parser = new JsonParser();

                                    JsonElement element = parser.parse(s);

                                    JsonObject object = element.getAsJsonObject();  // 转化为对象

                                    try {

                                        String comm = object.get("commentId").getAsString();

                                        addComment(isReply, headImg, position, msg, comm);

                                    } catch (Exception e) {

                                    }

                                }

                                runOnUiThread(new Runnable() {

                                    @Override

                                    public void run() {

                                        if (dialog_bottomsheet_tv_nocomment.getVisibility() == View.VISIBLE)

                                            dialog_bottomsheet_tv_nocomment.setVisibility(View.INVISIBLE);

                                        controllerView.settvComment(data.size() + "");

                                        WaitDialog.dismiss();

                                    }

                                });

                            }

                        });

                    }


                }


                @Override

                public void dismiss() {

                    scrollLocation(-offsetY);

                }

            });

        }

        showInputTextMsgDialog();

    }


    private void showInputTextMsgDialog() {

        inputTextMsgDialog.show();

    }


    private void addComment(boolean isReply, String headImg, final int position, String msg, @Nullable String id) {


        if (position >= 0) {

            //添加二级评论

            SecondLevelBean secondLevelBean = new SecondLevelBean();

            FirstLevelBean firstLevelBean = data.get(position);

            secondLevelBean.setReplyUserName(Current_click_name);

            if (Current_click_name.contains(UserUtil.user.getUsername()))

                isReply = false;

            secondLevelBean.setIsReply(isReply ? 1 : 0);

            secondLevelBean.setContent(msg);

            secondLevelBean.setHeadImg(headImg);

            secondLevelBean.setCreateTime(System.currentTimeMillis());

            secondLevelBean.setIsLike(0);

            secondLevelBean.setUserName(UserUtil.user.getUsername());

            secondLevelBean.setId(id);

            firstLevelBean.getSecondLevelBeans().add(secondLevelBean);

            data.set(firstLevelBean.getPosition(), firstLevelBean);

            bottomSheetAdapter.notifyDataSetChanged();

            rv_dialog_lists.postDelayed(new Runnable() {

                @Override

                public void run() {

                    ((LinearLayoutManager) rv_dialog_lists.getLayoutManager())

                            .scrollToPositionWithOffset(position == data.size() - 1 ? position

                                    : position + 1, position == data.size() - 1 ? Integer.MIN_VALUE : rv_dialog_lists.getHeight() / 2);

                }

            }, 100);

        } else {

            //添加一级评论

            FirstLevelBean firstLevelBean = new FirstLevelBean();

            firstLevelBean.setUserName(UserUtil.user.getUsername());

            firstLevelBean.setId(id);

            firstLevelBean.setHeadImg(headImg);

            firstLevelBean.setCreateTime(System.currentTimeMillis());

            firstLevelBean.setContent(msg);

            firstLevelBean.setLikeCount(0);

            firstLevelBean.setSecondLevelBeans(new ArrayList<SecondLevelBean>());

            data.add(0, firstLevelBean);

            sort();

            rv_dialog_lists.scrollToPosition(0);

        }

        if (dialog_bottomsheet_tv_nocomment != null && dialog_bottomsheet_tv_nocomment.getVisibility() == View.VISIBLE)

            runOnUiThread(new Runnable() {

                @Override

                public void run() {

                    dialog_bottomsheet_tv_nocomment.setVisibility(View.INVISIBLE);

                }

            });

    }


    public void initComment() {

        HttpUtil.SendPostRequestAddSrings(Constant.video_Comment_URL, new String[]{"videoId"}, new String[]{list.get(curPlayPos).getVideoId() + ""}, new Callback() {

            @Override

            public void onFailure(@NotNull Call call, @NotNull IOException e) {


            }


            @Override

            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                isNull = false;

                String str = response.body().string();

                //str=str.replaceFirst(",\"code\":\"200\",\"Data\":",",\"code\":\"200\",\"Comment\":");

                Total.clear();

                data.clear();

                Log.e("success", str);

                CommentRoot commentRoot = new Gson().fromJson(str, CommentRoot.class);

                Log.e("success", commentRoot.toString());

                if ("200".equals(commentRoot.getCode())) {

                    for (Comment c : commentRoot.getComment()) {

                        FirstLevelBean firstLevelBean = new FirstLevelBean();

                        firstLevelBean.setContent(c.getContent());

                        firstLevelBean.setHeadImg(Constant.GETHENDIMG_URL + c.getAvatar());

                        firstLevelBean.setUserName(c.getNickname());

                        firstLevelBean.setPosition(data.size());

                        firstLevelBean.setCreateTime(c.getDate());

                        firstLevelBean.setId(c.getCommentId() + "");

                        List<SecondLevelBean> beanList = new ArrayList<>();

                        if (c.getChild() != null) {

                            for (Child cld : c.getChild()) {

                                SecondLevelBean secondLevelBean = new SecondLevelBean();

                                secondLevelBean.setContent(cld.getContent());

                                secondLevelBean.setHeadImg(Constant.GETHENDIMG_URL + cld.getAvatar());

                                secondLevelBean.setUserName(cld.getNickname());

                                //TODO 时间戳转换？

                                secondLevelBean.setCreateTime(c.getDate());

                                secondLevelBean.setId(cld.getCommentId() + "");

                                beanList.add(secondLevelBean);

                            }

                        }

                        firstLevelBean.setSecondLevelBeans(beanList);

                        Total.add(firstLevelBean);

                    }

                    for (int i = 0; i < 5 && i < Total.size(); i++)

                        data.add(Total.get(i));

                } else {

                    isNull = true;

                }

                runOnUiThread(new Runnable() {

                    @Override

                    public void run() {

                        controllerView.settvComment(Total.size() + "");

                    }

                });

            }

        });

    }

    private void showToast(String content) {

        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        curPlayPos=0;
        finish();
        super.onBackPressed();
    }
}
