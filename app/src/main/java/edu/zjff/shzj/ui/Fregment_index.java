package edu.zjff.shzj.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.bravin.btoast.BToast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;
import com.shzj.behavior.fragment.dummy.TabEntity;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.service.GetArticleListService;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.UserUtil;
import edu.zjff.shzj.util.anyUtil;
import edu.zjff.shzj.util.locationUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;

public class Fregment_index extends Fragment {

    RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private StaggeredGridLayoutManager layoutManager;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.city)
    TextView city;
    @BindView(R.id.weather)
    TextView weather;
    Intent service;
    public static int page=0;
    int index=0;
    public static String categoryName;
    BReceiver receiver;private IntentFilter intentFilter;
    private List<Data> list=new ArrayList<>();//数据
    private List<Data> uselist=new ArrayList<>();//数据
    private  List<Integer> heightlist =new ArrayList<>();
    locationUtil locationUtil;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //线程池
        View view = inflater.inflate(R.layout.fragment_index, null);
        getLocation();
        ButterKnife.bind(this,view);
        registerReceiver();
        setBanner();//轮播
        startService();
        getCateoryList();
        setView(view);
        initdata();
        return view;
    }

    private void getLocation() {
        if (locationUtil==null)
            locationUtil= new locationUtil(getContext());
        locationUtil.setListener(new locationUtil.locationUtilListener() {
                    @Override
                    public void ongetLocationSuccess(AMapLocation location) {
                        locationUtil.stopLocation();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        city.setText(location.getCity());
                                    if (location.getCity().length()<1)
                                        weather.setText("");
                                        else
                                    locationUtil.getWeather(location.getCity());

                                }
                            });
                    }

            @Override
            public void ongetWeaherSuccess(LocalWeatherLiveResult localWeatherLiveResult) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LocalWeatherLive weatherlive =  localWeatherLiveResult.getLiveResult();
                            weather.setText(weatherlive.getWeather()+" "+weatherlive.getTemperature()+"℃");
                        }
                    });
            }
        });
        locationUtil.startLocation();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);/*
        int i=-1;
        if (Activity_Content.ArticleId!="")
            for (Data data:list){
                i++;
                if (data.getArticleId().equals(Activity_Content.ArticleId)){
                    return;
                }
            }
        if (i<list.size()&&i>=0)
        {
            list.get(i).setLike(Activity_Content.isLike);
            adapter.list=list;
            adapter.notifyDataSetChanged();
        }
        anyUtil.syso(i+""+Activity_Content.ArticleId);
        Activity_Content.ArticleId="";*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getCateoryList() {
        HttpUtil.SENDPOSTREQ(Constant.GETAllCATEGORY, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                if(!s.contains("200"))
                    return;
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(s);
                JsonObject object = element.getAsJsonObject();  // 转化为对象
                JsonArray comm = object.get("data").getAsJsonArray();

                for (JsonElement js:comm){
                    JsonObject jo = js.getAsJsonObject();
                    cateoryList.add(jo.get("categoryName").getAsString());
                }
            }
        });

    }
    List<String> cateoryList =new ArrayList<>();
    @OnClick(R.id.rl_search)
    public void starTosearch(){
        startActivity(new Intent(getContext(),activity_search.class));
    }
    @BindView(R.id.grid)
    GridView gridView;
    private void setGridView() {
        int size = 5;
        int length = 350;
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 20) * density)-20;
        int itemWidth = (int) (length * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        params.leftMargin=20;
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(40); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数

       GridViewAdapter adapter = new GridViewAdapter(getActivity().getApplicationContext(), dataList, R.layout.fragment_item_hsv);
       adapter.setCall(new call() {
           @Override
           public void setView(int p, View v) {
                Glide.with(v).load(Constant.BASE+"images/"+dataList.get(p).getThumbnail())
                        .into((ImageView) v.findViewById(R.id.img));

               ((TextView)v.findViewById(R.id.city)).setText("#"+dataList.get(p).getCategory().getCategoryName());

               ((TextView)v.findViewById(R.id.count)).setText(anyUtil.countProcess(dataList.get(p).getNice()));

               ((TextView)v.findViewById(R.id.title)).setText(dataList.get(p).getTitle()+(dataList.get(p).getTitle().length()>8?"...":""));

               v.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       startActivity(new Intent(getActivity(),Activity_Content.class).putExtra("ArticleId",dataList.get(p).getArticleId()));
                   }
               });

           }

       });
        gridView.setAdapter(adapter);
    }
    List<Data> dataList;
    @BindView(R.id.tl_6)
     CommonTabLayout mTabLayout_6;
    private void initdata() {
        //TODO 排行榜接口   item名字为fragment_item_list1
        String[] par= {"number"};String[] par1 = {"5"};
        HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + "android/hotArticle", par, par1, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                DataRoot dataRoot = new Gson().fromJson(s,DataRoot.class);
                dataList=dataRoot.getData();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            setGridView();
                    }
                });
            }
        });
    }

    private void setView(View view){
        anyUtil.setNavigation((AppCompatActivity) getActivity());
        view.setPadding(0,0,0,0);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset<=-50){
                    toolbar.setBackgroundColor(Color.rgb(240,237,232));
                }
                else
                {
                    toolbar.setBackgroundColor(Color.argb(0,0,0,0));
                }
            }
        });
        layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator)recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(null);
        adapter = new RecyclerViewAdapter(getContext(),uselist,heightlist);
        recyclerView.setAdapter(adapter);
        for (String s : mTitles){
            mTabEntities.add(new TabEntity(s,""));
        }
        mTabLayout_6.setTabData(mTabEntities);
        mTabLayout_6.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                smartRefreshLayout.resetNoMoreData();
                page=0;list.clear();uselist.clear();
                if (position==0)
                    categoryName=null;
                else
                    categoryName=cateoryList.get(position-1);
                startService();

            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        smartRefreshLayout.autoRefresh(400);
        smartRefreshLayout.setOnMultiListener(new OnMultiListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                mOffset = offset / 2;
                /*parallax.setTranslationY(mOffset - mScrollY);*/
                toolbar.setAlpha(1 - Math.min(percent, 1));
            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {

            }

            @Override
            public void onHeaderStartAnimator(RefreshHeader header, int headerHeight, int maxDragHeight) {

            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {

            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterStartAnimator(RefreshFooter footer, int footerHeight, int maxDragHeight) {

            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                anyUtil.syso(uselist.size()+">>onLoadMore()"+list.size());
               if(uselist.size()<list.size()){
                    int a =uselist.size();
                    for (int i = 0;i<10&&a+i<list.size();i++){
                        uselist.add(list.get(a+i));
                        int height = new Random().nextInt(300) + 300;//[100,300)的随机数
                        heightlist.add(height);
                    }
                    adapter.notifyDataSetChanged();
                    refreshLayout.finishLoadMore();
                }
                else{
                    if (list.size()%10==0){
                        page++;
                        startService();
                    }else {
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    anyUtil.syso(">>onRefresh()");

                uselist.clear();list.clear();page=0;heightlist.clear();
                startService();
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }
        });
    }
    private int mOffset = 0;
    private int mScrollY = 0;

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"官方推荐", "游浙江", "品浙江", "识浙江"};
    @BindView(R.id.hsv_list)
    HorizontalScrollView hsv_list;
    private void setBanner() {
        List<Object> b = new ArrayList();
        b.add(R.mipmap.b3);
        b.add(R.mipmap.p3);

        banner.setAdapter(new BannerImageAdapter(b) {
            @Override
            public void onBindView(Object holder, Object data, int position, int size) {
                Glide.with(Fregment_index.this)
                        .load(data)
                        .placeholder(R.drawable.ic_default_image)
                        .dontAnimate()
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(((BannerImageHolder)holder).imageView);
            }
        }).addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(getContext()))
                .setIntercept(false)

        ;
                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        BToast.warning(getContext()).text("position:"+position).show();
                    }
                });
    }

    private void registerReceiver() {
        intentFilter=new IntentFilter();
        intentFilter.addAction(Constant.BROADCAST_GETARTICLE_SUCCESS);
        intentFilter.addAction(Constant.BROADCAST_GETARTICLE_WRONG);
        receiver=new BReceiver();
        getActivity().registerReceiver(receiver,intentFilter);
    }
    private void startService(){
        UserUtil.isLogin_notshowDialog(getActivity());
        service=new Intent(getContext(), GetArticleListService.class);
        getActivity().startService(service);
    }
     class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{

         //屏幕的宽度(px值）
         int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
         //Item的宽度，或图片的宽度
         int width = screenWidth/2;
        private Context context;
        private List<Data> list;//数据
        private List<Integer> heightList;//装产出的随机数
        public RecyclerViewAdapter(Context context, List<Data> list,List<Integer> heightList) {
            this.context = context;
            this.list = list;
            //记录为每个控件产生的随机高度,避免滑回到顶部出现空白
            this.heightList = heightList;
            setHasStableIds(true);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //找到item的布局
            View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);

            return new MyViewHolder(view);//将布局设置给holder
        }

         @Override
         public long getItemId(int position) {
            return super.getItemId(position);
         }

         @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            String IMGNAME=list.get(position).getThumbnail();
            Glide.with(context).load(Constant.GETIMG_URL+IMGNAME)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_default_image)
                    .dontAnimate()
                    .error(R.drawable.g3)
                    .override(width,SIZE_ORIGINAL)
                    .into(holder.imageView);

            holder.menu.setText(list==null||list.get(position)==null?"诗画浙江":list.get(position).getTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!anyUtil.isFastDoubleClick(1000))
                    startActivity(new Intent(getActivity(),Activity_Content.class).putExtra("ArticleId",list.get(position).getArticleId()));
                }
            });
            //设置图片圆角角度
            RoundedCorners roundedCorners= new RoundedCorners(100);//px
            //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
            RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
            Glide.with(getActivity())
                    .load(Constant.GETHENDIMG_URL + list.get(position).getUser().getAvatar())
                    .dontAnimate().placeholder(R.mipmap.shzj_avatar_default)
                    .apply(options)
                    .into(holder.iv_touxiang);
            holder.tv_zan.setText(list.get(position).getNice());
            ViewGroup.LayoutParams params = holder.imageView.getLayoutParams();
            params.height = heightList.get(position);
            holder.imageView.setLayoutParams(params);
            /*holder.tv_zan.setText(list.get(position).ge);*/
            holder.tv_name.setText(list.get(position).getUser()==null||list.get(position)==null?"法外狂徒":list.get(position).getUser().getUsername());

            if (list.get(position).getLike())
                holder.iv_zan.setImageResource(R.drawable.ic_z_green_like_flash);
            else
                holder.iv_zan.setImageResource(R.drawable.ic_z_green_like);

        }


         class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView,iv_zan,iv_touxiang;
            TextView menu,tv_zan,tv_name;
            public MyViewHolder(View itemView) {
                super(itemView);
                imageView =  itemView.findViewById(R.id.imageView);
                menu =itemView.findViewById(R.id.text_menu);
                iv_zan=itemView.findViewById(R.id.iv_zan);
                iv_touxiang=itemView.findViewById(R.id.iv_tx);
                tv_name=itemView.findViewById(R.id.text_name);
                tv_zan= itemView.findViewById(R.id.tv_zan);
            }
        }
    }

    @Override
    public void onDestroyView() {
        locationUtil.destroyLocation();
        if (service != null) {
        getActivity().stopService(service);
        getActivity().unregisterReceiver(receiver);//动态注册的广播接收器需取消
        super.onDestroyView();
    }
    }

    class BReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.BROADCAST_GETARTICLE_SUCCESS.equals(intent.getAction())){

                DataRoot dataRoot=intent.getParcelableExtra(Constant.MESSAGE);
                if(dataRoot!=null){
                    list.addAll(dataRoot.getData());
                }
                if ("500".equals(dataRoot.getCode())){
                    smartRefreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                }
                for (int i =0 ; i<10;i++)
                {
                    if (i>=list.size()){
                        break;
                    }
                    int height = new Random().nextInt(300) + 300;//[300,600)的随机数
                    heightlist.add(height);
                    uselist.add(list.get(i));
                }
                int a = adapter.list.size();
                adapter.list=uselist;
                adapter.heightList=heightlist;
                adapter.notifyDataSetChanged();
                smartRefreshLayout.finishRefresh();
                smartRefreshLayout.finishLoadMore();
            }



            if(Constant.BROADCAST_GETARTICLE_WRONG.equals(intent.getAction())){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int i=0;boolean asd=false;
                        for (Data data: adapter.list){
                            if (data.getArticleId().equals(Activity_Content.ArticleId)){
                                asd=true;
                                break;
                            }
                            i++;
                        }
                        if (asd){
                            adapter.list.get(i).setLike(Activity_Content.like);
                            adapter.list.get(i).setNice(Activity_Content.count);
                        }
                        anyUtil.syso(Activity_Content.ArticleId+ "         "+i);
                        adapter.notifyDataSetChanged();
                    }
                });
            }



        }
    }

    public static class GridViewAdapter extends BaseAdapter {
        Context context;
        List<Data> list;
        int resId;
        call call;
        public GridViewAdapter(Context _context, List<Data> _list,int resourceId) {
            this.list = _list;
            this.context = _context;
            resId=resourceId;
        }

        public void setCall(call call) {
            this.call = call;
        }

        @Override
        public int getCount() {
            return list.size();
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
            if (convertView==null)
            {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(resId, null);
            }
            call.setView(position,convertView);
            return convertView;
        }
    }
    public interface call{void setView(int p,View v);}

}