package edu.zjff.shzj.util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.yqw.hotheart.HeartFrameLayout;
import com.yqw.hotheart.minterface.DoubleClickListener;
import com.yqw.hotheart.minterface.SimpleClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.VideoMessage;
import edu.zjff.shzj.util.cache.PreloadManager;
import edu.zjff.shzj.view.BaseRvAdapter;
import edu.zjff.shzj.view.BaseRvViewHolder;
import edu.zjff.shzj.view.ControllerView;
import edu.zjff.shzj.view.LikeView;


public class VideoAdapter extends BaseRvAdapter<VideoMessage, VideoAdapter.VideoViewHolder> {

    private static final String TAG = "TikTokAdapter";
    private List<VideoMessage> videos;
    private HeartEvent heartEvent;


    public VideoAdapter(Context context, List<VideoMessage> datas) {
        super(context, datas);
        this.videos = datas;
    }

    @Override
    protected void onBindData(VideoViewHolder holder, VideoMessage videoData, int position) {
        holder.controllerView.setVideoData(videoData);
        //设置封面
        //设置封面图片(Glide)
        /**
         * vframe/<Format>
         *       /offset/<Second>
         *       /w/<Width>
         *       /h/<Height>
         *       /rotate/<Degree>
         */
        Glide.with(holder.ivCover.getContext())
                .load(Constant.BASE+"videos/" + videoData.getUrl() + "?vframe/jpg/offset/1")
                .placeholder(android.R.color.transparent)
                .into(holder.ivCover);

        holder.likeView.setOnLikeListener(() -> {
                holder.controllerView.like(true);
        });
    }

    public void setHeartEvent(HeartEvent heartEvent) {
        this.heartEvent = heartEvent;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Log.d(TAG, "onBindViewHolder: " + position);
        Log.d(TAG, "预加载 " + position);
        View view = holder.itemView;
        HeartFrameLayout heartFrameLayout = view.findViewById(R.id.likeview2);
        heartFrameLayout.setOnDoubleClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick(View view) {
                System.out.println("双击了");
                heartEvent.doubleClick();
                holder.likeView.onLikeListener.onLikeListener();
            }
        });
        heartFrameLayout.setOnSimpleClickListener(new SimpleClickListener() {
            @Override
            public void onSimpleClick(View view) {
                System.out.println("单击了");
                heartEvent.singleClick();
            }
        });
        PreloadManager.getInstance(holder.itemView.getContext()).addPreloadTask(Constant.BASE + "videos/" + videos.get(position).getUrl() , position+1);
    }



    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    public class VideoViewHolder extends BaseRvViewHolder {
        @BindView(R.id.likeview)
        LikeView likeView;
        @BindView(R.id.controller)
        public ControllerView controllerView;
        @BindView(R.id.iv_cover)
        ImageView ivCover;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface HeartEvent{
        void doubleClick();
        void singleClick();
    }
}
