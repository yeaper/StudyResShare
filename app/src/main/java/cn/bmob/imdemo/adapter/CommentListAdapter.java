package cn.bmob.imdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.base.ImageLoaderFactory;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.model.DynamicComment;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.model.i.QueryUserListener;
import cn.bmob.v3.exception.BmobException;


/**
 * 评论适配器
 */
public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<DynamicComment> dataList = new ArrayList<>();

    public void setDatas(List<DynamicComment> list) {
        dataList.clear();
        if (null != list) {
            dataList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        dataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(parent.getContext(), parent, onRecyclerViewListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyHolder extends BaseViewHolder<DynamicComment>{

        @Bind(R.id.comment_avatar)
        ImageView avatar;
        @Bind(R.id.comment_name)
        TextView name;
        @Bind(R.id.comment_time)
        TextView time;
        @Bind(R.id.comment_content)
        TextView content;

        public MyHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
            super(context, root, R.layout.item_comment, onRecyclerViewListener);
        }

        @Override
        public void bindData(final DynamicComment comment) {
            // 获取头像
            UserModel.getInstance().queryUserInfo(comment.getCommenterId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if(e == null){
                        ImageLoaderFactory.getLoader().loadAvator(avatar, s.getAvatar(), R.mipmap.head);
                    }
                }
            });
            name.setText(comment.getCommenterName());
            time.setText(comment.getTime());
            content.setText(comment.getContent());
        }
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
}
