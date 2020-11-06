package edu.zjff.shzj.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.VideoMessage;
import edu.zjff.shzj.util.AutoLinkHerfManager;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.OnVideoControllerListener;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import edu.zjff.shzj.util.autolinktextview.AutoLinkTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.animation.Animation.INFINITE;

/**
 * create by libo
 * create on 2020-05-20
 * description
 */
public class ControllerView extends RelativeLayout implements View.OnClickListener {
    @BindView(R.id.tv_content)
    AutoLinkTextView autoLinkTextView;
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.lottie_anim)
    LottieAnimationView animationView;
    @BindView(R.id.rl_like)
    RelativeLayout rlLike;
    @BindView(R.id.iv_comment)
    IconFontTextView ivComment;
    @BindView(R.id.iv_share)
    IconFontTextView ivShare;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.rl_record)
    RelativeLayout rlRecord;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.iv_head_anim)
    CircleImageView ivHeadAnim;
    @BindView(R.id.iv_like)
    IconFontTextView ivLike;
    @BindView(R.id.tv_likecount)
    TextView tvLikecount;
    @BindView(R.id.tv_commentcount)
    TextView tvCommentcount;
    @BindView(R.id.tv_sharecount)
    TextView tvSharecount;
    @BindView(R.id.iv_focus)
    ImageView ivFocus;
    private OnVideoControllerListener listener;
    private VideoMessage videoData = new VideoMessage();
        public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fregment_video, this);
        ButterKnife.bind(this, rootView);

        ivHead.setOnClickListener(this);
        ivComment.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        rlLike.setOnClickListener(this);
        ivFocus.setOnClickListener(this);

        setRotateAnim();
    }
    public void settvComment(String s){
        ((AppCompatActivity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCommentcount.setText(s);
            }
        });
    }
    @SuppressLint("SetTextI18n")
    public void setVideoData(VideoMessage videoData) {
        this.videoData = videoData;

        //设置头像
        Glide.with(getContext())
                .load(Constant.GETHENDIMG_URL+videoData.getUser().getAvatar())
                .into(ivHead);
//        ivHead.setImageResource(videoData.getUser().getAvatar());

        //设置昵称
        tvNickname.setText("@" + videoData.getUser().getUsername());

        //设置自动超链接格式的简介
        AutoLinkHerfManager.setContent(videoData.getProfile(), autoLinkTextView);

        //设置旋转头像
        Glide.with(getContext())
                .load(Constant.GETHENDIMG_URL+videoData.getUser().getAvatar())
                .into(ivHeadAnim);
        //设置点赞数量
        tvLikecount.setText(videoData.getNice()+"");
        //tvCommentcount.setText(NumUtils.numberFilter(videoData.getCommentCount()));
        //tvSharecount.setText(NumUtils.numberFilter(videoData.getShareCount()));

        //设置点赞动画
        animationView.setAnimation("like.json");

        //点赞状态
        if (videoData.getLiked()) {
            ivLike.setTextColor(getResources().getColor(R.color.color_FF0041));
        } else {
            ivLike.setTextColor(getResources().getColor(R.color.white));
        }

        //关注状态
        if (videoData.getFocused()) {
            ivFocus.setVisibility(GONE);
        } else {
            ivFocus.setVisibility(VISIBLE);
        }
    }

    public void setListener(OnVideoControllerListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_head:
                listener.onHeadClick(videoData.getUser().getUsername());
                break;
            case R.id.rl_like:
                listener.onLikeClick();
                break;
            case R.id.iv_comment:
                listener.onCommentClick();
                break;
            case R.id.iv_share:
                listener.onShareClick();
                break;
            case R.id.iv_focus:
                if (!videoData.getFocused()) {
                    videoData.setLiked(true);
                    ivFocus.setVisibility(GONE);
                }
                break;
        }
    }

    /**
     * 点赞动作
     */

    public void like(@Nullable boolean isLikeClick) {


        if(UserUtil.isLogin((Activity) getContext()))
                if (!isLikeClick||(!videoData.getLiked()&&isLikeClick))
            HttpUtil.SendPostRequestAddSrings(Constant.VIDEO_LIKE,
                    new String[]{"token", "videoId", "status"},
                    new String[]{UserUtil.user.getToken(), videoData.getVideoId()+"", videoData.getLiked()?"0":"1"},
                    new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("like(11)","error");
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            BToast.info(getContext()).text(response.body().string()).show();

                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (videoData.getLiked()) {
                                        //点赞
                                        animationView.setVisibility(VISIBLE);
                                        animationView.playAnimation();
                                        ivLike.setTextColor(getResources().getColor(R.color.color_FF0041));
                                        tvLikecount.setText(Integer.parseInt(tvLikecount.getText()+"")+1+"");
                                    } else {
                                        animationView.setVisibility(INVISIBLE);
                                        ivLike.setTextColor(getResources().getColor(R.color.white));
                                        tvLikecount.setText(Integer.parseInt(tvLikecount.getText()+"")-1+"");

                                    }
                                }
                            });

                            videoData.setLiked(!videoData.getLiked());

                        }
                    });
    }

    /**
     * 循环旋转动画
     */
    private void setRotateAnim() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(INFINITE);
        rotateAnimation.setDuration(8000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rlRecord.startAnimation(rotateAnimation);
    }

}
