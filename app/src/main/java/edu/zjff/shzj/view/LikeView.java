package edu.zjff.shzj.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import org.xmlpull.v1.XmlPullParser;

import butterknife.BindView;
import edu.zjff.shzj.util.AnimUtils;

import java.util.Random;

import edu.zjff.shzj.R;

/**
 * create by libo
 * create on 2020-05-20
 * description 点赞动画view
 */
public class LikeView extends RelativeLayout {
    private GestureDetector gestureDetector;
    /** 图片大小 */
    private int likeViewSize = 150;
    private int[] angles = new int[]{-30, 0, 30};
    /** 单击是否有点赞效果 */
    private boolean canSingleTabShow = false;
    private OnPlayPauseListener onPlayPauseListener;
    public OnLikeListener onLikeListener;




    public LikeView(Context context) {
        super(context);
        init();
    }

    public LikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                addLikeView(e);
                onLikeListener.onLikeListener();
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (onPlayPauseListener != null) {
                    onPlayPauseListener.onPlayOrPause();
                }
                return true;
            }
        });

        setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void addLikeView(MotionEvent e) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_END);
        imageView.setImageResource(R.mipmap.ic_like);
        addView(imageView);
        LayoutParams layoutParams = new LayoutParams(likeViewSize,likeViewSize+400);//likeViewSize>(int)e.getRawY()?(int)e.getRawY():likeViewSize
        layoutParams.leftMargin = (int) e.getX() - likeViewSize / 2;
        //layoutParams.topMargin = (int) e.getY()-likeViewSize;
        layoutParams.topMargin = (int)e.getRawY()-400;
        imageView.setLayoutParams(layoutParams);
//        playAnim(imageView);

    }
    private void playAnim(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        int degrees = angles[new Random().nextInt(3)];
        animationSet.addAnimation(AnimUtils.rotateAnim(0, 0, degrees));
        animationSet.addAnimation(AnimUtils.scaleAnim(100, 2f, 1f, 0));
        animationSet.addAnimation(AnimUtils.alphaAnim(0, 1, 100, 0));
        animationSet.addAnimation(AnimUtils.scaleAnim(400, 1f, 1.8f, 400));
        animationSet.addAnimation(AnimUtils.alphaAnim(1f, 0, 500, 400));
        animationSet.addAnimation(AnimUtils.translationAnim(400, 0, 0, 0, -400, 400));
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(() -> removeView(view));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animationSet);
    }

    public interface OnPlayPauseListener {
        void onPlayOrPause();
    }

    /**
     * 设置单机播放暂停事件
     * @param onPlayPauseListener
     */
    public void setOnPlayPauseListener(OnPlayPauseListener onPlayPauseListener) {
        this.onPlayPauseListener = onPlayPauseListener;
    }

    public interface OnLikeListener {
        void onLikeListener();
    }

    /**
     * 设置双击点赞事件
     * @param onLikeListener
     */
    public void setOnLikeListener(OnLikeListener onLikeListener) {
        this.onLikeListener = onLikeListener;
    }
    /**
     * 重设 view 的宽高
     */
    public static void setViewLayoutParams(View view, int nWidth, int nHeight) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.height != nHeight || lp.width != nWidth) {
            lp.width = nWidth;
            lp.height = nHeight;
            view.setLayoutParams(lp);
        }
    }
}
