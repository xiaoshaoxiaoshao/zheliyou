package edu.zjff.shzj.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.ui.Activity_Content;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class anyUtil {
    public static SpannableString matcherSearchTitle(int color, String text, String keyword) {
        SpannableString s = new SpannableString(text);
        keyword = escapeExprSpecialWord(keyword);
        text = escapeExprSpecialWord(text);
        if (text.contains(keyword) && !TextUtils.isEmpty(keyword)) {
            try {
                Pattern p = Pattern.compile(keyword);
                Matcher m = p.matcher(s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } catch (Exception e) {
            }
        }
        return s;
    }

    public static String escapeExprSpecialWord(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void setNavigation(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 实现透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 判断是否是快速点击
     */
    private static long lastClickTime = 0;

    public static boolean isFastDoubleClick(int intervaltime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < intervaltime) {
            return true;
        }
        lastClickTime = time;
        return false;

    }

    public static void syso(String s) {
        System.out.println("----------------------阿巴阿巴阿巴阿巴---------------------------");
        System.out.println("-阿巴-" + s);
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    private static UploadManager uploadManager;

    public static void upload(String path, String key, mCallBack callback) {
        HttpUtil.SENDGETREQ(Constant.GETQINIUTOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("七牛Token", "七牛Token:我Token你妈服务器都没了");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                String QiniuToken = s.substring(s.indexOf(",") + 10, s.lastIndexOf("\""));
                Log.e("七牛Token", "七牛Token:" + QiniuToken);
                uploads(QiniuToken, path, key, callback);
            }
        });
    }

    public static void uploadImg(String path, String key,Context context,mCallBack callback) {
        if (path.contains("content://")) {
            Uri uri = Uri.parse(path);
            path = FileUtils.getFilePathByUri_BELOWAPI11(uri, context);
        }
        String finalPath = path;
        HttpUtil.SENDGETREQ(Constant.GETQINIUTOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("七牛Token", "七牛Token:我Token你妈服务器都没了");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                String QiniuToken = s.substring(s.indexOf(",") + 10, s.lastIndexOf("\""));
                Log.e("七牛Token", "七牛Token:" + QiniuToken);
                uploads(QiniuToken, finalPath, key, callback);
            }
        });
    }
    private static void uploads(String uploadToken, String uploadFilePath,String key,mCallBack callback) {
        //config配置上传参数
        Configuration configuration = new Configuration.Builder()
                .connectTimeout(10)
                .responseTimeout(60).build();
        if (uploadManager == null) {
            //this.uploadManager = new UploadManager(fileRecorder);
            uploadManager = new UploadManager(configuration, 3);
        }

        UploadOptions opt = new UploadOptions(null, null, true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
            }
        }, null);String keyname;
        try{
            keyname = UUID.randomUUID()+uploadFilePath.substring(uploadFilePath.lastIndexOf("."));
        }catch (Exception e){
            if (key.equals("videos/"))
                keyname = UUID.randomUUID()+".mp4";
            else
                keyname = UUID.randomUUID()+".jpg";

        }

        File uploadFile = new File(uploadFilePath);
        if (keyname.equals(""))
            keyname = UUID.randomUUID()+uploadFilePath.substring(uploadFilePath.lastIndexOf("."));

        String finalKeyname = keyname;
        uploadManager.put(uploadFile, key+keyname, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo,
                                         JSONObject jsonData) {
                        if (respInfo.isOK()) {
                            Log.e("msg", "上传成功");
                            callback.success(finalKeyname);
                        }else{
                            Log.e("Log",respInfo.toString());
                            if(jsonData!=null)
                                Log.e("Log",jsonData.toString());
                        }
                    }
                }, opt);
    }

    public interface mCallBack{
        void success(String fileName);
    }
    public static void setStatusBarTransparent(Activity activity){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = activity.getWindow();

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.TRANSPARENT);

        } else {

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

    }

    public static String countProcess(String count){
        String s = count;
        if (count.length()>=5){
            return s.substring(0,s.length()-4)+"."+s.substring(1,s.length()-3)+"w+";
        }else if(count.length()>=4){
            return s.substring(0,s.length()-3)+"."+s.substring(1,s.length()-2)+"k";
        }else
            return s;
    }public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
    public static String stampToDate(String str){
        long time=Long.parseLong(str);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time_Date = sdf.format(new Date(time));
        return time_Date;

    }

}
