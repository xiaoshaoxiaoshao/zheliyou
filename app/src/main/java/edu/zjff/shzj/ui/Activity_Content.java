package edu.zjff.shzj.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hymane.expandtextview.ExpandTextView;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.kongzue.holderview.HolderView;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.NineGridViewAdapter;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;
import edu.zjff.shzj.dialog.InputTextMsgDialog;
import edu.zjff.shzj.entity.Child;
import edu.zjff.shzj.entity.Comment;
import edu.zjff.shzj.entity.CommentRoot;
import edu.zjff.shzj.entity.Content;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.entity.FirstLevelBean;
import edu.zjff.shzj.entity.SecondLevelBean;
import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.util.CommentDialogSingleAdapter;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.MyNineAdapter;
import edu.zjff.shzj.util.RecyclerViewUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import edu.zjff.shzj.widget.VerticalCommentLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Activity_Content extends AppCompatActivity implements VerticalCommentLayout.CommentItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {
    static String count;
    Content constant;
    static String ArticleId = "";
    /*@BindView(R.id.iv_tou)
    ImageView iv_tou;


    @BindView(R.id.tv_name)
    TextView tv_name;
    */
    @BindView(R.id.holdView)
    HolderView holderView;
    @BindView(R.id.nineGrid)
    NineGridView nineGrid;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.etv)
    ExpandTextView etv;
    @BindView(R.id.iv_top)
    ImageView iv_top;
    /*@BindView(R.id.tv_content)
    TextView tv_content;*/
    @BindView(R.id.tv_zan)
    TextView tv_zan;
    @BindView(R.id.iv_content_line)
    ImageView iv_content_line;
    @BindView(R.id.ll_img)
    LinearLayout ll_img;
    @BindView(R.id.tv_liuyan)
    TextView tv_liuyan;
    @BindView(R.id.et_say)
    EditText et_say;/*
    @BindView(R.id.grid)
    GridView gridView;*/
    @BindView(R.id.iv_zan)
    ImageView iv_zan;
    @BindView(R.id.ll_zan)
    LinearLayout ll_zan;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra("ArticleId") == null)
            showdialog();
        else {
            getArticleContent();
        }
    }
    private void getArticleContent(){

        ArticleId = getIntent().getStringExtra("ArticleId");
        //游记详情
        HttpUtil.SendPostRequestAddSrings(Constant.GetArticle_URL, new String[]{"articleId",UserUtil.isLogin_notshowDialog(this)?"token":""}, new String[]{ArticleId,UserUtil.isLogin_notshowDialog(this)?UserUtil.user.getToken():""}
                , new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        showdialog();
                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String msg = response.body().string();
                        constant = new Gson().fromJson(msg, Content.class);
                        Log.e("getArticleLike()","like:"+constant.getArticleLike());
                        if(constant.getArticleLike()==null)
                        constant.setArticleLike(false);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setView();
                                initData();
                            }
                        });
                    }
                });
    }
    /**设置GirdView参数，绑定数据*/
   /* private void setGridView() {
        int size = 5;
        int length = 300;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(5); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

        GridViewAdapter adapter = new GridViewAdapter(getApplicationContext(),dataList);
        gridView.setAdapter(adapter);
    }
*/

    public void showdialog() {
        MessageDialog.show(Activity_Content.this, "提示", "网络出错了！")
                .setOkButton("确定", new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {

                        finish();
                        return false;
                    }
                });
    }

    void setView() {
        /*if (constant.getPictures().size()==1)
            setContentView(R.layout.activity_content_single);
        else*/
        setContentView(R.layout.activity_content);
        ButterKnife.bind(this);
        anyUtil.setNavigation(Activity_Content.this);
        BToast.Config.getInstance().apply(this.getApplication());
        NineGridView.setImageLoader(new GlideImageLoader());
        holderView.showEmpty();
        nineGrid.setMaxSize(6);/*
        nineGrid.setSingleImageSize(10000);*/
        etv.setContent(constant.getContent());

        /*tv_name.setText(constant.getCreator());*/
        tv_title.setText(constant.getTitle());
        /*tv_content.setText(constant.getContent());*/

        tv_zan.setText(constant.getNice());
        if (constant.getArticleLike()!=null&&constant.getArticleLike())
        Glide.with(this).load(R.drawable.ic_z_green_like_flash).dontAnimate().into(iv_zan);

        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
       if (!constant.getPictures().get(0).isEmpty()){
           for (String a :constant.getPictures()){
           ImageInfo info = new ImageInfo();
           info.bigImageUrl =Constant.GETIMG_URL+a;
           info.thumbnailUrl=Constant.GETIMG_URL+a;
           imageInfo.add(info);
       }
           Glide.with(this)
                   .load(Constant.BASE+"images/"+constant.getPictures().get(0))
                   .placeholder(R.drawable.g3)
                   .into(iv_top);
           imageInfo.remove(0);
           ll_img.setVisibility(View.VISIBLE);
           holderView.setVisibility(View.GONE);
           nineGrid.setAdapter(new MyNineAdapter(Activity_Content.this,imageInfo));


       }
    }
    private class GlideImageLoader implements NineGridView.ImageLoader {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)
                    .placeholder(R.drawable.ic_default_image)
                    .dontAnimate()
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(imageView);
        }
        @Override

        public Bitmap getCacheImage(String url) {
            return null;
        }
    }
    @OnClick(R.id.ll_msg)
    public void show() {
    /*
    bottomSheetDialog.show();*/
    if(UserUtil.isLogin(this)){
        slideOffset = 0;
        showSheetDialog();
    }
    }

    @OnClick(R.id.et_say)
    public void saysomething() {
        if (isLogin()){
            showSheetDialog();
            initInputTextMsgDialog(null, false, UserUtil.user.getAvatar(), -1);
        }
    }
    @OnClick(R.id.iv_back)
    public void back() {
        onBackPressed();
    }
    static boolean like;
    @Override
    public void onBackPressed() {
        count=tv_zan.getText()+"";
            like=constant.getArticleLike();
        sendBroadcast(new Intent(Constant.BROADCAST_GETARTICLE_WRONG));
        finish();
        super.onBackPressed();
    }

    @OnClick(R.id.ll_zan)
        public void zan(){
        ll_zan.setClickable(false);
        if (UserUtil.isLogin(this))
            HttpUtil.SendPostRequestAddSrings(Constant.ZAN, new String[]{"token", "articleId", "status"}, new String[]{UserUtil.user.getToken(), ArticleId, constant.getArticleLike() ? "0" : "1"}, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String s = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (s.contains("成功"))
                                if(s.contains("取消")){
                                    int a= Integer.parseInt(tv_zan.getText()+"")-1;
                                    tv_zan.setText(a+"");
                                    Glide.with(Activity_Content.this).load(R.drawable.ic_z_green_like).dontAnimate().into(iv_zan);
                                    constant.setArticleLike(false);
                                }
                                else{
                                    int a= Integer.parseInt(tv_zan.getText()+"")+1;
                                    tv_zan.setText(a+"");
                                    Glide.with(Activity_Content.this).load(R.drawable.ic_z_green_like_flash).into(iv_zan);
                                    constant.setArticleLike(true);
                                }
                            else
                                BToast.info(Activity_Content.this).text("出错了").show();

                            ll_zan.setClickable(true);
                        }
                    });
                    Log.e("s:",""+s);
                }
            });
    }

    public boolean isLogin(){
        return UserUtil.isLogin(this);
        }

    public void LongClickDialog(View view){
        BottomMenu.show(Activity_Content.this, new String[]{"复制文字", "删除"}, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index){
                    case 0:
                        //获取剪贴板管理器：
                        ClipboardManager cm=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型
                        ClipData mClipData= ClipData.newPlainText("Label",((TextView)view.findViewById(R.id.tv_content)).getText()+"");
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        break;
                    case 1:/*
                        Log.e("11",((TextView)view.findViewById(R.id.commId)).getText()+"");
                        Log.e("11","commId"+view.toString());*/
                        if(isLogin())
                        {
                            WaitDialog.show(Activity_Content.this, "请稍候...").setOnBackClickListener(
                                    new OnBackClickListener() {
                                        @Override
                                        public boolean onBackClick() {WaitDialog.dismiss();
                                            return true;
                                        }
                                    }
                            );
                            String comm = ((TextView)view.findViewById(R.id.commId)).getText()+"";
                            HttpUtil.SendPostRequestAddSrings(Constant.DELETECOMMENT,new String[]{"commentId","token"},new String[]{comm,UserUtil.user.getToken()}, new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    WaitDialog.dismiss();
                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String s = response.body().string();
                                    BToast.success(Activity_Content.this)
                                            .text(s.split(",")[1])
                                            .show();
                                    WaitDialog.dismiss();
                                    int index = 0;int in=0;boolean isSecondLevelBean = false;
                                    for (FirstLevelBean fr :data){
                                       if(fr.getId().equals(comm))
                                           break;
                                        else if(fr.getSecondLevelBeans()!=null)
                                       {
                                           in = 0;List<SecondLevelBean> list = fr.getSecondLevelBeans();
                                           for (SecondLevelBean se :list){
                                               if(se.getId().equals(comm))
                                               { break;}
                                               in++;
                                           }
                                           if(list.size()!=in){
                                               isSecondLevelBean=true;
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
                                }
                            });
                        }
                        break;
                }
            }
        });
    }

    private List<FirstLevelBean> data = new ArrayList<>();
    private List<FirstLevelBean> TotalData = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private InputTextMsgDialog inputTextMsgDialog;
    private float slideOffset = 0;
    String content = "";

    View dialog_bottomsheet_tv_nocomment;
    private CommentDialogSingleAdapter bottomSheetAdapter;
    private RecyclerView rv_dialog_lists;
    private long totalCount = 30;//总条数不得超过它
    private int offsetY;
    Boolean isNull = false;
    private RecyclerViewUtil mRecyclerViewUtil;
    List<Data> dataList;

    //初始化数据 在项目中是从服务器获取数据
    private void initData() {
        //评论详情
        HttpUtil.sendPostRequestAddString(Constant.Comment_URL, "articleId", ArticleId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("error", "Activity_Content()-->initData()");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = response.body().string();
                //str=str.replaceFirst(",\"code\":\"200\",\"Data\":",",\"code\":\"200\",\"Comment\":");

                CommentRoot commentRoot = new Gson().fromJson(str, CommentRoot.class);
                if ("200".equals(commentRoot.getCode())) {
                    for (Comment c : commentRoot.getComment()) {
                        FirstLevelBean firstLevelBean = new FirstLevelBean();
                        firstLevelBean.setContent(c.getContent());
                        firstLevelBean.setHeadImg(Constant.GETHENDIMG_URL + c.getAvatar());
                        firstLevelBean.setUserName(c.getNickname());
                        firstLevelBean.setPosition(data.size());
                        firstLevelBean.setCreateTime(c.getDate());
                        firstLevelBean.setId(c.getCommentId()+"");
                        List<SecondLevelBean> beanList = new ArrayList<>();
                        if (c.getChild() != null) {
                            for (Child cld : c.getChild()) {
                                SecondLevelBean secondLevelBean = new SecondLevelBean();
                                secondLevelBean.setContent(cld.getContent());
                                secondLevelBean.setHeadImg(Constant.GETHENDIMG_URL + cld.getAvatar());
                                secondLevelBean.setUserName(cld.getNickname());
                                //TODO 时间戳转换？
                                secondLevelBean.setCreateTime(c.getDate());
                                secondLevelBean.setId(cld.getCommentId()+"");
                                beanList.add(secondLevelBean);
                            }
                        }
                        firstLevelBean.setSecondLevelBeans(beanList);
                        data.add(firstLevelBean);
                        Log.e("success", data.toString());
                    }
                } else {
                    isNull = true;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_liuyan.setText(data.size()+"");
                    }
                });
            }
        });


/*

        //卡片布局
        HttpUtil.sendRequestToGetArticleList(Constant.GETARTICLELIST_URL,(int)Math.random()*5, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                gridView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strData = response.body().string();
                Gson gson = new Gson();
                DataRoot data=null;
                data = gson.fromJson(strData, DataRoot.class);
                Log.e("GetArService.class/mes:",data.toString());

                if (!"{\"error\":\"Document not found\"}".equals(strData)){
                    dataList=data.getData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setGridView();
                        }
                    });
                }else
                    gridView.setVisibility(View.INVISIBLE);
            }
        });
*/

    }

    /**
     * 重新排列数据
     * 未解决滑动卡顿问题
     */
    private void sort() {
        int size = data.size();
        for (int i = 0; i < size; i++) {
            FirstLevelBean firstLevelBean = data.get(i);
            firstLevelBean. setPosition(i);
            List<SecondLevelBean> secondLevelBeans = firstLevelBean.getSecondLevelBeans();
            if (secondLevelBeans == null || secondLevelBeans.isEmpty()) continue;
            int count = secondLevelBeans.size();
            for (int j = 0; j < count; j++) {
                SecondLevelBean secondLevelBean = secondLevelBeans.get(j);
                secondLevelBean.setPosition(i);
                secondLevelBean.setChildPosition(j);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(size==0)
                    if (rv_dialog_lists.getVisibility()==View.INVISIBLE)
                        rv_dialog_lists.setVisibility(View.VISIBLE);
                bottomSheetAdapter.notifyDataSetChanged();
            }
        });
    }
    private void showSheetDialog() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.show();
            return;
        }

        View view = View.inflate(this, R.layout.dialog_bottomsheet, null);
        ImageView iv_dialog_close = (ImageView) view.findViewById(R.id.dialog_bottomsheet_iv_close);
        rv_dialog_lists = (RecyclerView) view.findViewById(R.id.dialog_bottomsheet_rv_lists);
        RelativeLayout rl_comment = view.findViewById(R.id.rl_comment);

        dialog_bottomsheet_tv_nocomment=view.findViewById(R.id.dialog_bottomsheet_tv_nocomment);
        if (isNull) {
            dialog_bottomsheet_tv_nocomment.setVisibility(View.VISIBLE);
        }
        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());

        rl_comment.setOnClickListener(v -> {
            if (isLogin()){
                showSheetDialog();
                initInputTextMsgDialog(null, false,UserUtil. user.getAvatar(), -1);
            }
        });

        bottomSheetAdapter = new CommentDialogSingleAdapter(this);
        bottomSheetAdapter.setNewData(data);
        rv_dialog_lists.setHasFixedSize(true);
        rv_dialog_lists.setLayoutManager(new LinearLayoutManager(this));
        rv_dialog_lists.setItemAnimator(new DefaultItemAnimator());
        bottomSheetAdapter.setLoadMoreView(new SimpleLoadMoreView());
        bottomSheetAdapter.setOnLoadMoreListener(this, rv_dialog_lists);
        rv_dialog_lists.setAdapter(bottomSheetAdapter);

        initListener();
        bottomSheetDialog = new BottomSheetDialog(this, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        final BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Activity_Content.this.slideOffset = slideOffset;
            }
        });
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
            if (view1.getId() == R.id.ll_like && false) {
                // 小邵阉割法

                firstLevelBean.setLikeCount(firstLevelBean.getLikeCount() + (firstLevelBean.getIsLike() == 0 ? 1 : -1));
                firstLevelBean.setIsLike(firstLevelBean.getIsLike() == 0 ? 1 : 0);
                data.set(position, firstLevelBean);
                bottomSheetAdapter.notifyItemChanged(firstLevelBean.getPosition());
            } else if (view1.getId() == R.id.rl_group) {
                //TODO （到此为止一级评论被点击）     添加二级评论
                if (isLogin()){
                    showSheetDialog();
                    Log.e("initListener()","position:"+position);
                    initInputTextMsgDialog(view1, false, UserUtil.user.getAvatar(), position);
                }
            }
        });
        //滚动事件
        if (mRecyclerViewUtil != null) mRecyclerViewUtil.initScrollListener(rv_dialog_lists);
    }
    String Current_click_name="";
    private void initInputTextMsgDialog(View view, final boolean isReply, final String headImg, final int position) {
        dismissInputDialog();
        if (view != null) {
            offsetY = view.getTop();
            scrollLocation(offsetY);
            scrollLocation(offsetY);
            Current_click_name=((TextView)view.findViewById(R.id.tv_user_name)).getText().toString();
        }
        if (inputTextMsgDialog == null) {
            inputTextMsgDialog = new InputTextMsgDialog(this, R.style.dialog_center);
            inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
                @Override
                    public void onTextSend(String msg) {//isReplay 是代表当前消息是否是回复某人的    点击二级评论中的信息为回复


                    WaitDialog.show(Activity_Content.this, "请稍候...").setOnBackClickListener(
                            new OnBackClickListener() {
                                @Override
                                public boolean onBackClick() {
                                    WaitDialog.dismiss();
                                    return true;
                                }
                            }
                    );
                    if(isLogin())
                    {
                        String[] parName={"content","articleId","parentId","token"};
                        Log.e("position","p:"+position);
                        String[] par={msg,ArticleId,position==-1?"0":data.get(position).getId(),UserUtil.user.getToken()};//position==-1?"0":data.get(position).getId()==null?"0":data.get(position).getId()
                        Log.e("par"," "+par[0]+" "+par[1]+" "+par[2]+" "+par[3]);
                        HttpUtil.SendPostRequestAddSrings(Constant.INSERTNewComment, parName, par, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        WaitDialog.dismiss();
                                    }
                                });
                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String s = response.body().string();
                                anyUtil.syso(s);
                                if (s.contains("-110"))
                                    BToast.success(Activity_Content.this)
                                            .text("評論失敗")
                                            .show();
                                else{

                                    JsonParser parser = new JsonParser();
                                    JsonElement element = parser.parse(s);
                                    JsonObject object = element.getAsJsonObject();  // 转化为对象
                                    String comm = object.get("commentId").getAsString();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (dialog_bottomsheet_tv_nocomment.getVisibility()==View.VISIBLE)
                                                dialog_bottomsheet_tv_nocomment.setVisibility(View.INVISIBLE);
                                            addComment(isReply, headImg, position, msg,comm);
                                            WaitDialog.dismiss();
                                        }
                                    });
                                }
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

    private void addComment(boolean isReply, String headImg, final int position, String msg,@Nullable String id) {
        if (position >= 0) {
            //添加二级评论
            SecondLevelBean secondLevelBean = new SecondLevelBean();
            FirstLevelBean firstLevelBean = bottomSheetAdapter.getData().get(position);
            secondLevelBean.setReplyUserName(Current_click_name);
            if(UserUtil.user.getUsername().contains(Current_click_name))
                isReply=false;
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_liuyan.setText(data.size()+"");
                }
            });
        }
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_USER && resultCode == RESULT_OK) {
            getArticleContent();
        }
    }
    private void dismissInputDialog(){
        if (inputTextMsgDialog != null) {
            if (inputTextMsgDialog.isShowing()) inputTextMsgDialog.dismiss();
            inputTextMsgDialog.cancel();
            inputTextMsgDialog = null;
        }
    }
    private void showInputTextMsgDialog() {
        inputTextMsgDialog.show();
    }
    private int getWindowHeight() {
        Resources res = getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    //在项目中是从服务器获取数据，其实就是二级评论分页获取
    @Override
    public void onMoreClick(View layout, int position) {
    //TODO 从数据TotalData中更新Data
    }


    //添加二级评论（回复某人）
    @Override
    public void onItemClick(View layout, SecondLevelBean bean, int position) {
        if(isLogin())
        initInputTextMsgDialog(layout, true, bean.getHeadImg(), position);
    }

    @Override
    public void onItemLongClick(View view, SecondLevelBean bean, int position) {
        LongClickDialog(view);
    }

    //二级评论点赞 本地数据更新喜欢状态
    // 在项目中是还需要通知服务器成功才可以修改本地数据

    //在项目中是从服务器获取数据，其实就是一级评论分页获取
    @Override
    public void onLoadMoreRequested() {
        if (data.size() >= totalCount||data.size()>=TotalData.size()) {
            bottomSheetAdapter.loadMoreEnd(false);
            return;
        }
        for(int i = data.size();i<5+i||i<TotalData.size();i++){
            data.add(TotalData.get(i));
        }
        sort();
        bottomSheetAdapter.loadMoreComplete();
    }

    // item滑动到原位
    public void scrollLocation(int offsetY) {
        try {
            rv_dialog_lists.smoothScrollBy(0, offsetY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecyclerViewUtil != null) {
            mRecyclerViewUtil.destroy();
            mRecyclerViewUtil = null;
        }
        bottomSheetAdapter = null;
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public class GridViewAdapter extends BaseAdapter {
        Context context;
        List<Data> list;
        public GridViewAdapter(Context _context, List<Data> _list) {
            this.list = _list;
            this.context = _context;
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.item_grid, null);
            ImageView iv = convertView.findViewById(R.id.image);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView content = (TextView) convertView.findViewById(R.id.content);
            if(ArticleId.equals(list.get(position).getArticleId()))
                list.remove(position);
            Glide.with(context).load(Constant.GETIMG_URL+list.get(position).getThumbnail())
                    .placeholder(R.drawable.ic_default_image).dontAnimate().into(iv);
            title.setText(list.get(position).getTitle());
            content.setText(list.get(position).getContent());
            return convertView;
        }
    }
}
