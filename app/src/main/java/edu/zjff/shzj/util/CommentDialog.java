package edu.zjff.shzj.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bravin.btoast.BToast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.SimpleLoadMoreView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kongzue.dialog.interfaces.OnBackClickListener;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.WaitDialog;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import edu.zjff.shzj.R;
import edu.zjff.shzj.dialog.InputTextMsgDialog;
import edu.zjff.shzj.entity.Child;
import edu.zjff.shzj.entity.Comment;
import edu.zjff.shzj.entity.CommentRoot;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.entity.FirstLevelBean;
import edu.zjff.shzj.entity.SecondLevelBean;
import edu.zjff.shzj.ui.Activity_Content;
import edu.zjff.shzj.widget.VerticalCommentLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CommentDialog implements VerticalCommentLayout.CommentItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    private List<FirstLevelBean> data = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private InputTextMsgDialog inputTextMsgDialog;
    private float slideOffset = 0;
    String Current_click_name="";
    private CommentDialogSingleAdapter bottomSheetAdapter;
    private RecyclerView rv_dialog_lists;
    private int offsetY;
    Boolean isNull = false;
    private RecyclerViewUtil mRecyclerViewUtil;
    private Context context;
    public static final String PARMENTNAME_ARTICLE="articleId";
    public static final String PARMENTNAME_VIDEO="videoId";
    public final String parmentName;
    public String ID;
    public CommentDialog(Context context,List<FirstLevelBean> totalData,String parmentname,String id){
        ID=id;
        parmentName=parmentname;


        data=totalData;
        this.context= context;
    }

    @Override
    public void onLoadMoreRequested() {

    }

    @Override
    public void onMoreClick(View layout, int position) {

    }

    @Override
    public void onItemClick(View view, SecondLevelBean bean, int position) {
        if(UserUtil.isLogin((AppCompatActivity)context))
            initInputTextMsgDialog(view, true, bean.getHeadImg(), position);
    }

    @Override
    public void onItemLongClick(View view, SecondLevelBean bean, int position) {
         LongClickDialog(view);
    }

    public void LongClickDialog(View view){
        BottomMenu.show((AppCompatActivity) context, new String[]{"复制文字", "删除"}, new OnMenuItemClickListener() {
            @Override
            public void onClick(String text, int index) {
                switch (index){
                    case 0:
                        //获取剪贴板管理器：
                        ClipboardManager cm=(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型
                        ClipData mClipData= ClipData.newPlainText("Label",((TextView)view.findViewById(R.id.tv_content)).getText()+"");
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        break;
                    case 1:/*
                        Log.e("11",((TextView)view.findViewById(R.id.commId)).getText()+"");
                        Log.e("11","commId"+view.toString());*/
                        if(UserUtil.isLogin((Activity) context))
                        {
                            WaitDialog.show((AppCompatActivity) context, "请稍候...").setOnBackClickListener(
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
                                    BToast.success(context)
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
        bottomSheetAdapter.notifyDataSetChanged();
    }

    private void showSheetDialog() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.show();
            return;
        }
        View view = View.inflate(context, R.layout.dialog_bottomsheet,null);
        ImageView iv_dialog_close = (ImageView) view.findViewById(R.id.dialog_bottomsheet_iv_close);
        rv_dialog_lists = (RecyclerView) view.findViewById(R.id.dialog_bottomsheet_rv_lists);
        RelativeLayout rl_comment = view.findViewById(R.id.rl_comment);
        if (!isNull) {
            view.findViewById(R.id.dialog_bottomsheet_tv_nocomment).setVisibility(View.VISIBLE);
        }

        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());

        rl_comment.setOnClickListener(v -> {
            if (UserUtil.isLogin((Activity) context)){
                showSheetDialog();
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
                if (UserUtil.isLogin((AppCompatActivity)context)){
                    showSheetDialog();
                    Log.e("initListener()","position:"+position);
                    initInputTextMsgDialog(view1, false, UserUtil.user.getAvatar(), position);
                }
            }
        });
        //滚动事件
        if (mRecyclerViewUtil != null) mRecyclerViewUtil.initScrollListener(rv_dialog_lists);
    }
    private void dismissInputDialog(){
        if (inputTextMsgDialog != null) {
            if (inputTextMsgDialog.isShowing()) inputTextMsgDialog.dismiss();
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
    public void show(){
        showSheetDialog();
        slideOffset = 0;
        initInputTextMsgDialog(null,false,null,-1);
    }

    private void initInputTextMsgDialog(View view, final boolean isReply, final String headImg, final int position) {
        dismissInputDialog();
        if (view != null) {
            offsetY = view.getTop();
            scrollLocation(offsetY);
            scrollLocation(offsetY);
            Current_click_name=((TextView)view.findViewById(R.id.tv_user_name)).getText().toString();
        }
        if (inputTextMsgDialog == null) {
            inputTextMsgDialog = new InputTextMsgDialog(context, R.style.dialog_center);
            inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
                @Override
                public void onTextSend(String msg) {//isReplay 是代表当前消息是否是回复某人的    点击二级评论中的信息为回复

                    WaitDialog.show((AppCompatActivity)context, "请稍候...").setOnBackClickListener(
                            new OnBackClickListener() {
                                @Override
                                public boolean onBackClick() {WaitDialog.dismiss();
                                    return true;
                                }
                            }
                    );
                    if(UserUtil.isLogin((Activity) context))
                    {
                        String[] parName={"content",parmentName,"parentId","token"};

                        String[] par={msg,ID,position==-1?"0":data.get(position).getId(),UserUtil.user.getToken()};

                        HttpUtil.SendPostRequestAddSrings(Constant.INSERTNewComment, parName, par, new Callback() {

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        WaitDialog.dismiss();
                                    }
                                });
                            }
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                String s = response.body().string();
                                if (s.contains("-110"))
                                    BToast.success(context)
                                            .text("評論失敗")
                                            .show();
                                else{
                                    JsonParser parser = new JsonParser();
                                    JsonElement element = parser.parse(s);
                                    JsonObject object = element.getAsJsonObject();  // 转化为对象
                                    String comm = object.get("commentId").getAsString();

                                    addComment(isReply, headImg, position, msg,comm);
                                    ((AppCompatActivity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
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

    private void showInputTextMsgDialog() {
        inputTextMsgDialog.show();
    }
    private void addComment(boolean isReply, String headImg, final int position, String msg,@Nullable String id) {

        if (position >= 0) {
            //添加二级评论
            SecondLevelBean secondLevelBean = new SecondLevelBean();
            FirstLevelBean firstLevelBean = bottomSheetAdapter.getData().get(position);
            secondLevelBean.setReplyUserName(Current_click_name);
            if(Current_click_name.contains(UserUtil.user.getUsername()))
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
        }
    }

}
