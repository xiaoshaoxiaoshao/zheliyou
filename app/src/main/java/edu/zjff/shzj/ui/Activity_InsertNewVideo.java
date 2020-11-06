package edu.zjff.shzj.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnMenuItemClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.v3.BottomMenu;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.kongzue.stacklabelview.StackLabel;
import com.kongzue.stacklabelview.interfaces.OnLabelClickListener;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.cityjd.JDCityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Content;
import edu.zjff.shzj.entity.Message;
import edu.zjff.shzj.entity.User;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Activity_InsertNewVideo extends AppCompatActivity {
    @BindView(R.id.rl_text)
    RelativeLayout rl_text;
    @BindView(R.id.place)
    StackLabel place;
@BindView(R.id.title)
    EditText title;
@BindView(R.id.content)
    EditText content;
@BindView(R.id.video_view)
    VideoView videoView;
@BindView(R.id.stackLabelView)
    StackLabel huati;
@BindView(R.id.test)
    ImageView t;
    public String cityName="位置",provinceName="",other="",other1="";
    ArrayList<String> labels = new ArrayList<>();
    ArrayList<String> huat = new ArrayList<>();
    CityPickerView mPicker=new CityPickerView();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertvideo);
        anyUtil.setNavigation(this);
        ButterKnife.bind(this);
        init();
    }

    File file = new File("/sdcard/export.mp4");
    void init(){

        DialogSettings.autoShowInputKeyboard = (true);

        File file = new File("/sdcard/export.mp4");

        if (file.exists())
        {
            Glide.with(this).load(file);
            videoView.setVideoPath("/sdcard/export.mp4");
        }else
            onBackPressed();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        if(!videoView.isPlaying())
            videoView.start();

        initCityPick();

        //videoView.setMinimumHeight((getWindowManager().getDefaultDisplay().getHeight())/4);
        //title.setMaxHeight((getWindowManager().getDefaultDisplay().getHeight())/6);
        title.setHeight((getWindowManager().getDefaultDisplay().getHeight())/6);
        initClickTag();

        initLocation();
        startLocation();

    }

    private void initClickTag() {
        place.setSelectBackground(0);
        place.setSelectMode(false);
        place.setOnLabelClickListener(new mListener());

        huat.add("编辑我的话题");
        huati.setSelectMode(false);
        huati.setSelectBackground(0);
        huati.setOnLabelClickListener(new huatiaListener());
    }

    private void initCityPick() {

        mPicker.init(this);

        //监听选择点击事件及返回结果
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {

            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                provinceName=province.getName();
                cityName=city.getName();
                setPlace();
                //省份province
                //城市city
                //地区district
            }

            @Override
            public void onCancel() {
            }
        });
    }

    @BindView(R.id.submit_insertarticle)
    TextView submit;
    @OnClick(R.id.submit_insertarticle)
    void insert(){
        submit.setClickable(false);

        WaitDialog.show(Activity_InsertNewVideo.this,"请稍后");
            //TODO  七牛提交视频
            if(UserUtil.isLogin(Activity_InsertNewVideo.this))
            {
                if((title.getText()+"")!=""&&cityName!=""&&(content.getText()+"")!="")
                {
                    if (UserUtil.isLogin(this))
                    anyUtil.uploadImg("/sdcard/export.mp4","videos/",getApplicationContext(), new anyUtil.mCallBack() {
                        @Override
                        public void success(String fileName) {
                            WaitDialog.dismiss();
                            String tag="##"+cityName+"##";
                            if (other!="")
                                tag+="##"+other+"##";
                            if (other1!="")
                                tag+="##"+other1+"##";
                            for (String s:huat){
                            if (s!=""&&!"编辑我的话题".equals(s))
                                tag+="##"+s+"##";
                            }

                            //TODO 向发送服务器
                            String[] par=new String[]{"token","title","profile","categoryName","tag","cover","url"};
                            String[] val=new String[]{UserUtil.user.getToken()
                                    ,title.getText()+""
                                    ,content.getText()+""
                                    ,"null"
                                    ,tag
                                    ,"null"
                                    , Constant.BASE+"videos/"+fileName

                            };
                            HttpUtil.SendPostRequestAddSrings(Constant.INSERTNewVideo,par ,val , new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    anyUtil.syso(response.body().string());

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            WaitDialog.dismiss();
                                            submit.setClickable(true);
                                            BToast.success(Activity_InsertNewVideo.this).text("发布成功~请等待管理员审核").show();
                                            finish();
                                        }
                                    });
                                }
                            });


                        }
                    });

                }
                else{
                    submit.setClickable(true);
                    WaitDialog.dismiss();
                    MessageDialog.show(this,"提示","信息不完全");
                }
            }

    }


    @OnClick({R.id.iv_back,R.id.video_view})
    public void back(){
        onBackPressed();
    }


    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                if(location.getCity()!=null&&location.getCity()!=""){
                    cityName=location.getCity();
                    provinceName=location.getProvince();
                    setPlace();
                    stopLocation();
                }
            }
        }
    };
    private void startLocation(){
        // 启动定位
        locationClient.startLocation();
    }
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    class mListener implements OnLabelClickListener{
        @Override
        public void onClick(int index, View v, String s) {
                if("+".equals(s)){
                    InputDialog.build(Activity_InsertNewVideo.this)
                            .setTheme(DialogSettings.THEME.DARK)
                            .setStyle(DialogSettings.STYLE.STYLE_IOS)
                            .setInputInfo(new InputInfo().setMAX_LENGTH(4))
                            .setTitle("新增地址")
                            .setMessage("填入您的地址(小于4个字)")
                            .setOkButton("确定", new OnInputDialogButtonClickListener() {
                                @Override
                                public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                                    if (inputStr!="")
                                        if(other!="")
                                            other1=inputStr;
                                        else
                                            other=inputStr;
                                    setPlace();
                                    return false;
                                }
                            })
                            .show();
                }else if (index>0){
                    if (DialogSettings.style != (DialogSettings.STYLE.STYLE_MIUI))
                    DialogSettings.style = (DialogSettings.STYLE.STYLE_MIUI);
                    BottomMenu.show(Activity_InsertNewVideo.this, "操作", new String[]{"修改", "删除"}, new OnMenuItemClickListener() {
                        @Override
                        public void onClick(String text, int ind) {
                            switch (ind){
                                case 0:
                                    InputDialog.build(Activity_InsertNewVideo.this)
                                            .setTheme(DialogSettings.THEME.DARK)
                                            .setStyle(DialogSettings.STYLE.STYLE_IOS)
                                            .setInputInfo(new InputInfo().setMAX_LENGTH(4))
                                            .setTitle("修改地址")
                                            .setMessage("填入新的地址(小于4个字)")
                                            .setOkButton("确定", new OnInputDialogButtonClickListener() {
                                                @Override
                                                public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                                                    if (inputStr!="")
                                                        if(index==2)
                                                            other1=inputStr;
                                                        else
                                                            other=inputStr;
                                                    setPlace();
                                                    return false;
                                                }
                                            })
                                            .show();
                                    break;
                                case 1:

                                    MessageDialog.build(Activity_InsertNewVideo.this).
                                            show(Activity_InsertNewVideo.this, "删除地址", "您确定删除此地址", "确定", "取消")
                                    .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                                        @Override
                                        public boolean onClick(BaseDialog baseDialog, View v) {
                                            if(index==2)
                                                other1="";
                                            else
                                                other="";
                                            setPlace();
                                            return false;
                                        }
                                    })
                                    ;
                                    break;

                            }
                        }
                    });
                }else{
                    CityConfig cityConfig = new CityConfig.Builder()
                            .province(provinceName==""?"浙江省":provinceName)
                            .city(cityName==""||"位置".equals(cityName)?"杭州市":cityName)
                            .setCustomItemTextViewId(R.id.item_city_name_tv)
                            .setCityWheelType(CityConfig.WheelType.PRO_CITY)
                            .setCustomItemLayout(R.layout.item_city)
                            .build();
                    mPicker.setConfig(cityConfig);
                    mPicker.showCityPicker();

                }
        }
    }

    class huatiaListener implements OnLabelClickListener{

        @Override
        public void onClick(int index, View v, String s) {
            if("+".equals(s)||"编辑我的话题".equals(s)){
                InputDialog.build(Activity_InsertNewVideo.this)
                        .setTheme(DialogSettings.THEME.DARK)
                        .setStyle(DialogSettings.STYLE.STYLE_IOS)
                        .setTitle("新增话题")
                        .setMessage("填入您想加入的话题")
                        .setOkButton("确定", new OnInputDialogButtonClickListener() {
                            @Override
                            public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                                if(inputStr!=""&&inputStr.replaceAll(" ","")!=""){
                                    huat.add(0,inputStr);
                                    if (huat.size()>3)
                                        huat.remove(3);
                                    huati.setLabels(huat);
                                }
                                return false;
                            }
                        })
                        .show();
            }else{
                if (DialogSettings.style != (DialogSettings.STYLE.STYLE_MIUI))
                    DialogSettings.style = (DialogSettings.STYLE.STYLE_MIUI);
                BottomMenu.show(Activity_InsertNewVideo.this, "操作", new String[]{"修改", "删除"}, new OnMenuItemClickListener() {
                    @Override
                    public void onClick(String text, int ind) {
                        switch (ind){
                            case 0:
                                InputDialog.build(Activity_InsertNewVideo.this)
                                        .setTheme(DialogSettings.THEME.DARK)
                                        .setStyle(DialogSettings.STYLE.STYLE_IOS)
                                        .setTitle("修改话题")
                                        .setMessage("填入新的话题")
                                        .setOkButton("确定", new OnInputDialogButtonClickListener() {
                                            @Override
                                            public boolean onClick(BaseDialog baseDialog, View v, String inputStr) {
                                                if (inputStr!="")
                                                    huat.set(index,inputStr);
                                                    huati.setLabels(huat);
                                                return false;
                                            }
                                        })
                                        .show();
                                break;
                            case 1:
                                MessageDialog.build(Activity_InsertNewVideo.this).
                                        show(Activity_InsertNewVideo.this, "删除话题", "您确定删除此话题", "确定", "取消")
                                        .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                                            @Override
                                            public boolean onClick(BaseDialog baseDialog, View v) {
                                                huat.remove(index);
                                                if (huat.size()==2)
                                                    huat.add("+");
                                                huati.setLabels(huat);
                                                return false;
                                            }
                                        })
                                ;
                                break;

                        }
                    }
                });
            }
        }
    }

    public void setPlace(){
        labels = new ArrayList<>();
        labels.add(cityName);
        if (other!="")
            labels.add(other);
        if (other1!="")
            labels.add(other1);
        if (labels.size()<3){
            labels.add("+");
        }
        place.setLabels(labels);
    }



}
