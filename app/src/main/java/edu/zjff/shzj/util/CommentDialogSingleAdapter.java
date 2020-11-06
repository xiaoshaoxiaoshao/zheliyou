package edu.zjff.shzj.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tencent.mars.xlog.Log;

import edu.zjff.shzj.R;
import edu.zjff.shzj.entity.FirstLevelBean;
import edu.zjff.shzj.widget.VerticalCommentLayout;

/**
 * @author ganhuanhui
 * 时间：2019/12/19 0019
 * 描述：
 */
public class CommentDialogSingleAdapter extends BaseQuickAdapter<FirstLevelBean, BaseViewHolder> {

    private VerticalCommentLayout.CommentItemClickListener mItemClickListener;

    public CommentDialogSingleAdapter(VerticalCommentLayout.CommentItemClickListener mItemClickListener) {
        super(R.layout.item_comment_new);
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FirstLevelBean content) {
//      LinearLayout ll_like = helper.getView(R.id.ll_like);
        /*RelativeLayout rl_group = helper.getView(R.id.rl_group);
        rl_group.setId(Integer.parseInt(content.getId()));*/
        TextView commid =helper.getView(R.id.commId);
        commid.setText(content.getId());
        RoundedImageView iv_header = helper.getView(R.id.iv_header);
        TextView tv_user_name = helper.getView(R.id.tv_user_name);
        TextView tv_content = helper.getView(R.id.tv_content);
        helper.addOnClickListener(R.id.rl_group);
        //helper.addOnClickListener(R.id.ll_like);
/*
        TextView tv_like_count = helper.getView(R.id.tv_like_count);
        tv_like_count.setText(content.getLikeCount() + "");
        tv_like_count.setVisibility(content.getLikeCount() <= 0 ? View.GONE : View.VISIBLE);
*/

        helper.addOnLongClickListener(R.id.rl_group);
        tv_content.setText(content.getContent());
        tv_user_name.setText(content.getUserName());

        Glide.with(mContext).load(Constant.BASE1+"images/avatar/"+content.getHeadImg()).placeholder(R.mipmap.shzj_avatar_default).into(iv_header);

        if (content.getSecondLevelBeans() != null) {
            VerticalCommentLayout commentWidget = helper.getView(R.id.verticalCommentLayout);
            commentWidget.setVisibility(View.VISIBLE);
            int size = content.getSecondLevelBeans().size();
            commentWidget.setTotalCount(size);
            commentWidget.setPosition(helper.getAdapterPosition());
            commentWidget.setOnCommentItemClickListener(mItemClickListener);
            int limit = helper.getAdapterPosition() + 1;
            commentWidget.addCommentsWithLimit(content.getSecondLevelBeans(), size, false);
            Log.e("CommAdapter","size:"+size);
//          rl_group.setTag(commentWidget);
        }

    }


}
