package edu.zjff.shzj.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;

public class Activity_welcome extends AppCompatActivity implements View.OnClickListener {

    private int recLen = 5;//跳过倒计时提示5秒
    private TextView tv;
    Timer timer = new Timer();  //定义一个计时器
    private Handler handler;
    private Runnable runnable;

    private int[] splash_sequence = {
            R.mipmap.hangzhou1,
            R.mipmap.huzhou1,
            R.mipmap.huzhou2,
            R.mipmap.jiaxing1,
            R.mipmap.nanxun,
            R.mipmap.ningbo,
            R.mipmap.shaoxing,
            R.mipmap.wenzhou,
            R.mipmap.xitang};

    @BindView(R.id.sp_bg)
    ImageView mSpBgImage;
    @BindView(R.id.sp_jump_btn)
    Button mSpJumpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        getWindow().setFlags(flag, flag);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        settingSplash();
        //设置沉浸式状态栏
        StatusBarUtil.setTransparentForImageView(Activity_welcome.this, null);
        timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
        /**
         * 正常情况下不点击跳过
         */
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                //从闪屏界面跳转到首界面
                Intent intent = new Intent(Activity_welcome.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);//延迟5S后发送handler信息

    }

    @OnClick({R.id.sp_bg, R.id.sp_jump_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sp_bg:
                Intent intent = new Intent(Activity_welcome.this, MainActivity.class);
                startActivity(intent);
                finish();
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                break;
            case R.id.sp_jump_btn:
                Intent intent2 = new Intent(Activity_welcome.this, MainActivity.class);
                startActivity(intent2);
                finish();
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                break;
        }
    }


    public void settingSplash() {
        //设置背景
        Random r = new Random();
        int radomNum = r.nextInt(splash_sequence.length);
        Glide.with(this)
                .load(splash_sequence[radomNum])
                .dontAnimate()
                .into(mSpBgImage);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() { // UI thread
                @Override
                public void run() {
                    recLen--;
                    mSpJumpBtn.setText("跳过 " + recLen);
                    if (recLen < 0) {
                        timer.cancel();
                        mSpJumpBtn.setVisibility(View.GONE);//倒计时到0隐藏字体
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View view) {

    }
}