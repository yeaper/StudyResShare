package cn.bmob.imdemo.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.imdemo.R;
import cn.bmob.imdemo.adapter.DynamicAdapter;
import cn.bmob.imdemo.adapter.SearchResFileAdapter;
import cn.bmob.imdemo.base.ParentWithNaviFragment;
import cn.bmob.imdemo.bean.Friend;
import cn.bmob.imdemo.bean.User;
import cn.bmob.imdemo.event.PublishDynamicEvent;
import cn.bmob.imdemo.model.CampusDynamic;
import cn.bmob.imdemo.model.DynamicComment;
import cn.bmob.imdemo.model.ResFile;
import cn.bmob.imdemo.model.UserModel;
import cn.bmob.imdemo.model.i.OnDynamicCommentListener;
import cn.bmob.imdemo.model.i.OnDynamicDeleteListener;
import cn.bmob.imdemo.ui.LoginActivity;
import cn.bmob.imdemo.ui.MainActivity;
import cn.bmob.imdemo.ui.PublishDynamicActivity;
import cn.bmob.imdemo.ui.UserInfoActivity;
import cn.bmob.imdemo.util.TimeUtil;
import cn.bmob.imdemo.util.Util;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static cn.bmob.v3.Bmob.getApplicationContext;

/**
 * 校园动态
 */
public class DynamicFragment extends ParentWithNaviFragment implements OnDynamicCommentListener {

    @Bind(R.id.fab_publish_dynamic)
    FloatingActionButton publish_dynamic;
    @Bind(R.id.sw_refresh)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.rc_view)
    RecyclerView recyclerView;
    DynamicAdapter adapter;
    List<CampusDynamic> datas = new ArrayList<>();


    @Override
    protected String title() {
        return "校园动态";
    }

    public static DynamicFragment newInstance() {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DynamicFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dynamic, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new DynamicAdapter();
        adapter.setOnDynamicCommentListener(this);
        adapter.setDelete(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDynamic();
            }
        });
        getDynamic();
    }

    @OnClick({R.id.fab_publish_dynamic})
    public void click(View v){
        switch (v.getId()){
            case R.id.fab_publish_dynamic:
                getActivity().startActivity(new Intent(getActivity(), PublishDynamicActivity.class));
                break;
        }
    }

    private void getDynamic(){
        refreshLayout.setRefreshing(true);
        adapter.clear();
        datas.clear();
        BmobQuery<CampusDynamic> query = new BmobQuery<>();
        query.findObjects(new FindListener<CampusDynamic>() {
            @Override
            public void done(final List<CampusDynamic> list, BmobException e) {
                refreshLayout.setRefreshing(false);
                if(e == null){
                    if(list.size()>0){
                        UserModel.getInstance().queryFriends(
                                new FindListener<Friend>() {
                                    @Override
                                    public void done(List<Friend> friends, BmobException e) {
                                        refreshLayout.setRefreshing(false);
                                        if (e == null) {
                                            for(CampusDynamic dynamic: list){
                                                for(Friend friend: friends){//好友和自己发布的
                                                    if(dynamic.getAuthorId().equals(friend.getFriendUser().getObjectId())
                                                            || dynamic.getAuthorId().equals(UserModel.getInstance().getCurrentUser().getObjectId())){
                                                        datas.add(dynamic);
                                                        break;
                                                    }
                                                }
                                            }

                                        }else{
                                            for(CampusDynamic dynamic: list){ //自己发布的
                                                if(dynamic.getAuthorId().equals(UserModel.getInstance().getCurrentUser().getObjectId())){
                                                    datas.add(dynamic);
                                                }
                                            }
                                        }
                                        adapter.setDatas(datas);
                                    }
                                });
                    }else{
                        showToast("暂无动态");
                    }
                }else{
                    showToast("暂无动态");
                }

            }
        });


    }

    @Override
    public void onComment(final User user, final String dynamicId) {
        final EditText et = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle("评论")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(et.getText().toString().trim().length() > 0){
                            comment(et.getText().toString().trim(), user, dynamicId);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 评论操作
     * @param content
     */
    private void comment(final String content, final User user, final String dynamicId){
        CampusDynamic dynamic = new CampusDynamic();
        dynamic.setObjectId(dynamicId);
        dynamic.add("commentList", new DynamicComment(user.getObjectId(), user.getUsername()
                , content, TimeUtil.getCurrTime(System.currentTimeMillis())));
        dynamic.update(new UpdateListener() {

            @Override
            public void done(BmobException e) {
                if(e==null){
                    for(int i=0;i<datas.size();i++){
                        if(datas.get(i).getObjectId().equals(dynamicId)){
                            datas.get(i).getCommentList().add(new DynamicComment(user.getObjectId()
                                    , user.getUsername(), content, ""));
                            adapter.setDatas(datas);
                            break;
                        }
                    }
                }else{
                    showToast("评论失败："+e.getMessage());
                }
            }

        });
    }

    @Subscribe
    public void onEventMainThread(PublishDynamicEvent event){
        getDynamic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
