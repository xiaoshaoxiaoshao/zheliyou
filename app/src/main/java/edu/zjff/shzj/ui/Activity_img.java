package edu.zjff.shzj.ui;
//package edu.zjff.shzj.ui;
//
//import android.os.Bundle;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.bravin.btoast.BToast;
//import com.kongzue.dialog.v3.WaitDialog;
//import com.tencent.mars.xlog.Log;
//
//import java.util.ArrayList;
//
//import edu.zjff.shzj.entity.FirstLevelBean;
//import edu.zjff.shzj.util.CommentDialog;
//
//public class Activity_img extends AppCompatActivity {
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FirstLevelBean firstLevelBean = new FirstLevelBean();
//        firstLevelBean.setUserName("123");
//        firstLevelBean.setId("1");
//        firstLevelBean.setContent("阿巴阿巴阿巴阿巴阿巴阿巴");
//        ArrayList<FirstLevelBean> a = new ArrayList<>();
//        a.add(firstLevelBean);
//        CommentDialog commentDialog;
//        initLocation();
//        star();
//        mLocationClient.startLocation();
//        commentDialog = new CommentDialog(Activity_img.this,a,CommentDialog.PARMENTNAME_ARTICLE,"1");
///*
//        try {
//            new Thread().sleep(100);commentDialog.show();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }*/
//    }
//
//    //声明AMapLocationClient类对象
//    public AMapLocationClient mLocationClient = null;
//    //声明定位回调监听器
//    public AMapLocationListener mLocationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation aMapLocation) {
//            if (aMapLocation != null) {
//                if (aMapLocation.getErrorCode() == 0) {
////可在其中解析amapLocation获取相应内容。
//                    Log.e("city","c:"+aMapLocation.getCity());mLocationClient.stopLocation();
//                }else {
//                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
//                    Log.e("AmapError","location Error, ErrCode:"
//                            + aMapLocation.getErrorCode() + ", errInfo:"
//                            + aMapLocation.getErrorInfo());
//                }
//            }
//        }
//    };
//
//
//    //声明AMapLocationClientOption对象
//    public AMapLocationClientOption mLocationOption = null;
//    private void initLocation() {
////初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        mLocationOption = new AMapLocationClientOption();
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
////设置定位回调监听
//        mLocationOption.setNeedAddress(true);
//        mLocationClient.setLocationListener(mLocationListener);
//
//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//    }
// void star(){
////启动定位
//     mLocationClient.startLocation();
// }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
//    }
//}



import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.tencent.mars.xlog.Log;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;
import test.CheckPermissionsActivity;

/**
 * 高精度定位模式功能演示
 *
 * @创建时间： 2015年11月24日 下午5:22:42
 * @项目名称： AMapLocationDemo2.x
 * @author hongming.wang
 * @文件名称: Hight_Accuracy_Activity.java
 * @类型名称: Hight_Accuracy_Activity
 */
public class Activity_img extends CheckPermissionsActivity{
    private RadioGroup rgLocationMode;
    private EditText etInterval;
    private EditText etHttpTimeout;
    private CheckBox cbOnceLocation;
    private CheckBox cbAddress;
    private CheckBox cbGpsFirst;
    private CheckBox cbCacheAble;
    private CheckBox cbOnceLastest;
    private CheckBox cbSensorAble;
    private TextView tvResult;
    private Button btLocation;
    private RadioGroup rgGeoLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_index);
        ButterKnife.bind(this);
        /*//初始化定位
        initLocation();*/
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
        startLocation();
    }
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
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
                System.out.println(location.getCity());
            } else {
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
}

