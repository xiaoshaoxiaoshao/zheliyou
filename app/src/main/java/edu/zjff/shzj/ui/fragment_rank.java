package edu.zjff.shzj.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kongzue.holderview.HolderView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;
import com.shzj.behavior.fragment.dummy.TabEntity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class fragment_rank extends Fragment {
    @BindView(R.id.tl_8)
    CommonTabLayout commonTabLayout;
    @BindView(R.id.rl_rank)
    RecyclerView recyclerView;
    @BindView(R.id.Refresh)
    SmartRefreshLayout refreshLayout;
    ArrayList<String> cateoryList= new ArrayList<>();
    rankRecycleAdapter rankRecycleAdapter;
    int page=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank,null);
        ButterKnife.bind(this,view);
        anyUtil.setStatusBarTransparent(getActivity());
        ArrayList<CustomTabEntity> tabEntities = new ArrayList<>();
        tabEntities.add(new TabEntity("","游浙江"));
        tabEntities.add(new TabEntity("","品浙江"));
        tabEntities.add(new TabEntity("","识浙江"));
        commonTabLayout.setTabData(tabEntities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout.setOnMultiListener(new OnMultiListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {

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
                if (page>=4){
                    refreshLayout.finishLoadMoreWithNoMoreData();
                }else{
                    page++;
                    requestData();
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                    page=0;
                    refreshLayout.resetNoMoreData();
                    requestData();
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }
        });
        rankRecycleAdapter=new rankRecycleAdapter(getContext());
        recyclerView.setAdapter(rankRecycleAdapter);
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                cateoryId=cateoryList.get(position);
                page=0;
                requestData();
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        requestcateoryList();
        requestData();
        return view;
    }

    List<Data> dataList=new ArrayList<>();
    String cateoryId="1";
    private void requestData() {
        String[] par= {"number","type","categoryId"};
        String[] par1 = {(page+1)*10+"","ranklist",cateoryId};
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
                        rankRecycleAdapter.notifyDataSetChanged();
                        refreshLayout.finishLoadMore();
                        refreshLayout.finishRefresh();
                        if (dataList.size()%10!=0){
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        }
                    }
                });
            }
        });
    }

    private void requestcateoryList() {
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
                JsonObject object = element.getAsJsonObject();// 转化为对象
                JsonArray comm = object.get("data").getAsJsonArray();

                for (JsonElement js:comm){
                    JsonObject jo = js.getAsJsonObject();
                    cateoryList.add(jo.get("categoryId").getAsString());
                }
            }
        });
    }

    class rankRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        Context context;
        rankRecycleAdapter(Context context){this.context=context;}
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_item_hsv,parent,false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            holder=(ViewHolder)holder;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),Activity_Content.class).putExtra("ArticleId",dataList.get(position).getArticleId()));
                }
            });
            ((ViewHolder) holder).city.setText("#"+dataList.get(position).getCategory().getCategoryName());
            ((ViewHolder) holder).title.setText(dataList.get(position).getTitle());//+(dataList.get(position).getTitle().length()>8?"...":"")
            ((ViewHolder) holder).count.setText(anyUtil.countProcess(dataList.get(position).getNice()));
            Glide.with(context).load(Constant.BASE+"images/"+dataList.get(position).getThumbnail())
                    .into(((ViewHolder) holder).iv_img);
        }

        @Override
        public int getItemCount() {
            return (dataList==null?0:dataList.size());
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView iv_img;
            TextView city,count,title;

                ViewHolder (View view)
                {
                    super(view);
                    iv_img=view.findViewById(R.id.img);
                    count= view.findViewById(R.id.count);
                    city= view.findViewById(R.id.city);
                    title= view.findViewById(R.id.title);
                    ViewGroup.LayoutParams layoutParams = iv_img.getLayoutParams();
                    anyUtil.setMargins(view.findViewById(R.id.cv_hsv),25,35,25,0);
                    layoutParams.height+=150;
                    iv_img.setLayoutParams(layoutParams);
                }
        }
    }
}
