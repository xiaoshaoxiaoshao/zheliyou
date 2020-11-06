package com.shzj.behavior.fragment;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.Serializable;
import java.util.List;


import com.bumptech.glide.Glide;
import com.shzj.behavior.Demo1Activity;
import com.shzj.behavior.fragment.dummy.DummyContent.DummyItem;

import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.Content;
import edu.zjff.shzj.entity.Data;
import edu.zjff.shzj.entity.DataRoot;
import edu.zjff.shzj.entity.VideoMessage;
import edu.zjff.shzj.ui.Activity_Content;
import edu.zjff.shzj.ui.Activity_video;
import edu.zjff.shzj.util.Constant;
import edu.zjff.shzj.util.anyUtil;
import edu.zjff.shzj.widget.ResizableImageView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List<?> mValues;
    private Context context;
    public MyItemRecyclerViewAdapter(List<?> items, Context context) {
        mValues = items;

        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object data = mValues.get(position);

        if ("edu.zjff.shzj.entity.VideoMessage".equals(data.getClass().getName())){
            anyUtil.syso(data.getClass().getName());
            String s =((VideoMessage) data).getTitle();
            if (s.length()>=8)
                s=s.substring(0,8)+"...";
            holder.title.setText(s);
            Glide.with(context).load(Constant.BASE+"videos/"+((VideoMessage) data).getUrl()+"?vframe/jpg/offset/10")
              .centerInside()

            .into(holder.img);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity_video.curPlayPos=position;
                    context.startActivity(new Intent(context, Activity_video.class).putExtra("VideoList", (Serializable) mValues));
                }
            });
        }
        else{
            anyUtil.syso(data.getClass().getName());
            String s =((Data) data).getTitle();
            if (s.length()>=8)
                s=s.substring(0,8)+"...";
            holder.title.setText(s);
            Glide.with(context).load(Constant.BASE+"images/"+((Data) data).getThumbnail()).into(holder.img);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, Activity_Content.class).putExtra("ArticleId", ((Data) data).getArticleId()));
                }
            });
        }
        /*
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);*/

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView img;
        public final TextView title;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            img= view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.title);
        }

    }
}
