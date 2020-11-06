package edu.zjff.shzj.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.czp.searchmlist.mSearchLayout;
import com.google.gson.Gson;
import com.kongzue.holderview.HolderView;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.listener.OnMultiListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.HttpUtil;
import edu.zjff.shzj.util.SharedPreferencesUtils;
import edu.zjff.shzj.util.anyUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class activity_search extends AppCompatActivity {
    @BindView(R.id.msearchlayout)
    mSearchLayout msearchLy;
    @BindView(R.id.Refresh)
    RefreshLayout refreshLayout;
    @BindView(R.id.rl_search)
    RecyclerView recyclerView;
    @BindView(R.id.holdView)
    HolderView holderView;
    String key;
    int page=1;
    private List<Data> dataList = new ArrayList<>();
    searhRecycleAdapter adapter = new searhRecycleAdapter(activity_search.this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        //历史搜索数据
        anyUtil.setStatusBarTransparent(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        holderView.showEmpty();
        String shareData = (String) SharedPreferencesUtils.getParam(getApplicationContext(),"AlloldDataList","");
        List skills = Arrays.asList(shareData.split(","));
        //热门搜索数据
        String shareHotData = "杭州,宁波,舟山,温州,德清,湖州";
        List skillHots = Arrays.asList(shareHotData.split(","));

        msearchLy.initData(skills, skillHots, new mSearchLayout.setSearchCallBackListener() {
            public void Search(String str) {
                //进行或联网搜索  str搜索关键词
                refreshLayout.resetNoMoreData();
                key=str;page=1;
                starSearch();
            }

            public void Back() {
                //取消搜索
                finish();
            }

            public void ClearOldData() {
                //清除历史搜索记录  执行更新本地 原始历史搜索数据
                SharedPreferencesUtils.setParam(getApplicationContext(),"AlloldDataList","");
            }

            public void SaveOldData(ArrayList<String> AlloldDataList) {
                //保存所有历史搜索数据  请保存以便下次使用
                String str = "";
                if(AlloldDataList.size()>0){
                    for (String s:AlloldDataList)
                        str+=s+",";
                    str= str.substring(0,str.length()-1);
                }

                SharedPreferencesUtils.setParam(getApplicationContext(),"AlloldDataList",str);

            }

            @Override
            public void OnCanclePress() {
                msearchLy.scrollView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                holderView.setVisibility(View.GONE);
            }
        });
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
                page++;
                starSearch();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }
        });
    }

    private void starSearch() {
        msearchLy.scrollView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        String[] pars={"keyword","page"};
        String[] keys={key,page+""};
        HttpUtil.SendPostRequestAddSrings(Constant.BASE1 + "android/searchArticle", pars, keys, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String s = response.body().string();
                DataRoot dataRoot = new Gson().fromJson(s,DataRoot.class);
                anyUtil.syso(dataRoot.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (page>1){
                        if (dataRoot.getData()!=null){

                            dataList.addAll(dataRoot.getData());
                            refreshLayout.finishLoadMore();
                        }
                        else
                            refreshLayout.finishLoadMoreWithNoMoreData();
                    }else{

                            dataList=dataRoot.getData();
                        }
                        adapter.notifyDataSetChanged();
                        if(dataList!=null&&dataList.size()%10!=0)
                            refreshLayout.finishLoadMoreWithNoMoreData();
                        if (dataList==null){
                            recyclerView.setVisibility(View.GONE);
                            holderView.setVisibility(View.VISIBLE);
                        }else
                            refreshLayout.setEnableLoadMore(true);
                    }
                });
            }
        });
    }


    class searhRecycleAdapter extends RecyclerView.Adapter<searhRecycleAdapter.ViewHolder>{
        Context context;
        searhRecycleAdapter (Context context){this.context=context;}
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_serch,parent,false);

            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.title.setText(dataList.get(position).getTitle().length()>17?
                    anyUtil.matcherSearchTitle(Color.rgb(23,110,67),dataList.get(position).getTitle().substring(0,17),key)+"...":
                    anyUtil.matcherSearchTitle(Color.rgb(23,110,67),dataList.get(position).getTitle(),key));
            holder.content.setText(dataList.get(position).getContent().length()>24?
                    anyUtil.matcherSearchTitle(Color.rgb(23,110,67),dataList.get(position).getContent().substring(0,24),key)+"...":
                    anyUtil.matcherSearchTitle(Color.rgb(23,110,67),dataList.get(position).getContent(),key));
            holder.id.setText(dataList.get(position).getArticleId());
            holder.categoryname.setText(dataList.get(position).getUser().getUsername()+"/"+anyUtil.stampToDate(dataList.get(position).getPubDate()));
            holder.count.setText(dataList.get(position).getNice()+"喜欢");

            Glide.with(getApplicationContext())
                    .load(Constant.BASE+"images/"+dataList.get(position).getThumbnail())
                    .placeholder(R.drawable.ic_default_image)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(holder.img);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity_search.this,Activity_Content.class).putExtra("ArticleId",dataList.get(position).getArticleId()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return (dataList==null?0:dataList.size());
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.img)
                    ImageView img;
            @BindView(R.id.title)
                    TextView title;
            @BindView(R.id.content)
                    TextView content;
            @BindView(R.id.categoryname)
                    TextView categoryname;
            @BindView(R.id.count)
            TextView count;
            @BindView(R.id.id)
            TextView id;
            ViewHolder (View view)
            {
                super(view);
                ButterKnife.bind(this,view);
            }
        }
    }
}
