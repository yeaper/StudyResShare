package cn.yyp.srs.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.yyp.srs.R;
import cn.yyp.srs.base.ImageLoaderFactory;
import cn.yyp.srs.bean.User;
import cn.yyp.srs.model.CampusDynamic;
import cn.yyp.srs.model.UserModel;
import cn.yyp.srs.model.i.OnDynamicCommentListener;
import cn.yyp.srs.model.i.OnDynamicDeleteListener;
import cn.yyp.srs.model.i.QueryUserListener;
import cn.yyp.srs.ui.ChatActivity;
import cn.yyp.srs.ui.CommentActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;


/**
 * 校园动态适配器
 */
public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<CampusDynamic> dataList = new ArrayList<>();
    private boolean isShowDelete = false;

    public void setDatas(List<CampusDynamic> list) {
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

    public void setDelete(boolean isShowDelete){
        this.isShowDelete = isShowDelete;
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

    public class MyHolder extends BaseViewHolder<CampusDynamic>{

        @Bind(R.id.dynamic_avatar)
        ImageView avatar;
        @Bind(R.id.dynamic_delete)
        ImageView delete;
        @Bind(R.id.dynamic_name)
        TextView name;
        @Bind(R.id.dynamic_time)
        TextView time;
        @Bind(R.id.dynamic_content)
        TextView content;
        @Bind(R.id.dynamic_comment)
        ImageView comment;
        @Bind(R.id.dynamic_comment_ll)
        LinearLayout comment_ll;
        @Bind(R.id.dynamic_comment_list)
        RecyclerView comment_list;
        @Bind(R.id.dynamic_more_comment)
        TextView more_comment;

        public MyHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
            super(context, root, R.layout.item_dynamic, onRecyclerViewListener);
        }

        @Override
        public void bindData(final CampusDynamic dynamic) {
            if(isShowDelete){
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onDynamicDeleteListener != null){
                            onDynamicDeleteListener.delete(dynamic.getObjectId());
                        }
                    }
                });
            }else{
                delete.setVisibility(View.GONE);
            }
            // 获取动态作者名、头像
            UserModel.getInstance().queryUserInfo(dynamic.getAuthorId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if(e == null){
                        ImageLoaderFactory.getLoader().loadAvator(avatar, s.getAvatar(), R.mipmap.head);
                        name.setText(s.getUsername());
                    }else{
                        name.setText(dynamic.getAuthorName());
                    }
                }
            });
            time.setText(dynamic.getTime());
            content.setText(dynamic.getContent());
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //评论
                    if(onDynamicCommentListener != null){
                        onDynamicCommentListener.onComment(UserModel.getInstance().getCurrentUser(), dynamic.getObjectId());
                    }
                }
            });
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 自己不能和自己聊天
                    if(!dynamic.getAuthorId().equals(UserModel.getInstance().getCurrentUser().getObjectId())){
                        BmobIMUserInfo info = new BmobIMUserInfo(dynamic.getAuthorId(), dynamic.getAuthorName(), "");
                        //TODO 会话：4.1、创建一个常态会话入口，好友聊天
                        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("c", conversationEntrance);
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra(getContext().getPackageName(), bundle);
                        getContext().startActivity(intent);
                    }
                }
            });

            if(dynamic.getCommentList() == null || dynamic.getCommentList().size()<=0){
                comment_ll.setVisibility(View.GONE);
            }else {
                comment_ll.setVisibility(View.VISIBLE);
                if(dynamic.getCommentList().size()>5){ //超过5条显示更多评论
                    more_comment.setVisibility(View.VISIBLE);
                }else{
                    more_comment.setVisibility(View.GONE);
                }
                comment_list.setLayoutManager(new LinearLayoutManager(getContext()));
                comment_list.setAdapter(new DynamicCommentAdapter(dynamic.getCommentList()));
            }

            more_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入评论列表页
                    Intent intent = new Intent(getContext(), CommentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("dynamicId", dynamic.getObjectId());
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                }
            });
        }
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private OnDynamicCommentListener onDynamicCommentListener;

    public void setOnDynamicCommentListener(OnDynamicCommentListener onDynamicCommentListener) {
        this.onDynamicCommentListener = onDynamicCommentListener;
    }

    private OnDynamicDeleteListener onDynamicDeleteListener;

    public void setOnDynamicDeleteListener(OnDynamicDeleteListener onDynamicDeleteListener) {
        this.onDynamicDeleteListener = onDynamicDeleteListener;
    }
}
